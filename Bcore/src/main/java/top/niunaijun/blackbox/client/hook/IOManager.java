package top.niunaijun.blackbox.client.hook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import mirror.android.content.pm.ApplicationInfoL;
import mirror.android.content.pm.ApplicationInfoN;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.BClient;
import top.niunaijun.blackbox.client.VMCore;
import top.niunaijun.blackbox.utils.FileUtils;
import top.niunaijun.blackbox.utils.Reflector;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Created by Milk on 4/9/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
@SuppressLint("SdCardPath")
public class IOManager {
    private static IOManager sIOManager = new IOManager();
    private Map<String, String> mRedirectMap = new HashMap<>();

    private static final Map<String, Map<String, String>> sCachePackageRedirect = new HashMap<>();

    public static IOManager get() {
        return sIOManager;
    }

    // /data/data/com.google/  ----->  /data/data/com.virtual/data/com.google/
    public void addRedirect(String origPath, String redirectPath) {
        if (TextUtils.isEmpty(origPath) || TextUtils.isEmpty(redirectPath) || mRedirectMap.get(origPath) != null)
            return;
        mRedirectMap.put(origPath, redirectPath);
        File redirectFile = new File(redirectPath);
        if (!redirectFile.exists()) {
            redirectFile.mkdirs();
        }
        VMCore.addIORule(origPath, redirectPath);
    }

    public Map<String, String> generateRule(ApplicationInfo info) {
        Map<String, String> rule = new HashMap<>();
        if (info == null)
            return rule;
        Map<String, String> cache = sCachePackageRedirect.get(info.packageName);
        if (cache != null) {
            return cache;
        }
        ApplicationInfo hostInfo = BlackBoxCore.getContext().getApplicationInfo();
        File virtualRoot = new File(new File(hostInfo.dataDir), "virtual");

        rule.put("/data/data/" + info.packageName, new File(virtualRoot, "/data/data/" + info.packageName).getAbsolutePath());
        rule.put(info.dataDir, new File(virtualRoot, info.dataDir).getAbsolutePath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            rule.put(info.deviceProtectedDataDir, new File(virtualRoot, info.deviceProtectedDataDir).getAbsolutePath());
        }
        if (BuildCompat.isN()) {
            try {
                Object credentialProtectedDataDir = Reflector.with(info)
                        .field("credentialProtectedDataDir")
                        .get();
                if (credentialProtectedDataDir != null) {
                    String dir = (String) credentialProtectedDataDir;
                    rule.put(dir, new File(virtualRoot, dir).getAbsolutePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (BlackBoxCore.getContext().getExternalCacheDir() != null) {
            File sdcardVirtualRoot = BlackBoxCore.getContext().getExternalFilesDir("virtual");

            // sdcard
            rule.put("/sdcard/Android/data/" + info.packageName,
                    new File(sdcardVirtualRoot, "/sdcard/Android/data/" + info.packageName).getAbsolutePath());
            rule.put("/sdcard/android/data/" + info.packageName,
                    new File(sdcardVirtualRoot, "/sdcard/android/data/" + info.packageName).getAbsolutePath());

            rule.put("/storage/emulated/0/android/data/" + info.packageName,
                    new File(sdcardVirtualRoot, "/storage/emulated/0/android/data/" + info.packageName).getAbsolutePath());
            rule.put("/storage/emulated/0/Android/data/" + info.packageName,
                    new File(sdcardVirtualRoot, "/storage/emulated/0/Android/data/" + info.packageName).getAbsolutePath());
        }
        sCachePackageRedirect.put(info.packageName, rule);
        return rule;
    }

    public String redirectPath(String path) {
        if (TextUtils.isEmpty(path))
            return path;
        for (String orig : mRedirectMap.keySet()) {
            if (path.startsWith(orig)) {
                path = path.replace(orig, Objects.requireNonNull(mRedirectMap.get(orig)));
                break;
            }
        }
        return path;
    }

    public File redirectPath(File path) {
        if (path == null)
            return null;
        String pathStr = path.getAbsolutePath();
        return new File(redirectPath(pathStr));
    }

    public String redirectPath(String path, Map<String, String> rule) {
        if (TextUtils.isEmpty(path))
            return path;
        for (String orig : rule.keySet()) {
            if (path.startsWith(orig)) {
                path = path.replace(orig, Objects.requireNonNull(rule.get(orig)));
                break;
            }
        }
        return path;
    }

    public File redirectPath(File path, Map<String, String> rule) {
        if (path == null)
            return null;
        String pathStr = path.getAbsolutePath();
        return new File(redirectPath(pathStr, rule));
    }

    public static void redirectApplication(ApplicationInfo applicationInfo) {
        if (applicationInfo == null) return;

        try {
            Map<String, String> rule = new HashMap<>(IOManager.get().generateRule(applicationInfo));
            applicationInfo.dataDir = IOManager.get().redirectPath(applicationInfo.dataDir, rule);
            FileUtils.mkdirs(applicationInfo.dataDir);
            applicationInfo.nativeLibraryDir = new File(applicationInfo.dataDir, "libs").getAbsolutePath();
            FileUtils.mkdirs(applicationInfo.nativeLibraryDir);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ApplicationInfoL.scanPublicSourceDir.set(applicationInfo, applicationInfo.dataDir);
                ApplicationInfoL.scanSourceDir.set(applicationInfo, applicationInfo.dataDir);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                applicationInfo.deviceProtectedDataDir = IOManager.get().redirectPath(applicationInfo.deviceProtectedDataDir, rule);
                FileUtils.mkdirs(applicationInfo.deviceProtectedDataDir);
                String credentialProtectedDataDir = IOManager.get().redirectPath(ApplicationInfoN.credentialProtectedDataDir.get(applicationInfo), rule);
                ApplicationInfoN.credentialProtectedDataDir.set(applicationInfo, credentialProtectedDataDir);
                FileUtils.mkdirs(credentialProtectedDataDir);
            }
        } catch (Exception ignored) {
        }
    }

    // 由于正常情况Application已完成重定向，以下重定向是怕代码写死。
    public void enableRedirect(Context context) {
        Map<String, String> rule = new HashMap<>();
        String packageName = context.getPackageName();

        try {
            ApplicationInfo packageInfo = BlackBoxCore.getBPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA, BClient.getUserId());
            rule.put("/data/data/" + packageName, packageInfo.dataDir);
            rule.put("/data/user/0/" + packageName, packageInfo.dataDir);

            if (BlackBoxCore.getContext().getExternalCacheDir() != null && context.getExternalCacheDir() != null) {
                File external = context.getExternalCacheDir().getParentFile();

                // sdcard
                rule.put("/sdcard/Android/data/" + packageName,
                        external.getAbsolutePath());
                rule.put("/sdcard/android/data/" + packageName, external.getAbsolutePath());

                rule.put("/storage/emulated/0/android/data/" + packageName,
                        external.getAbsolutePath());
                rule.put("/storage/emulated/0/Android/data/" + packageName,
                        external.getAbsolutePath());

                rule.put("/storage/emulated/0/Android/data/" + packageName + "/files",
                        new File(external.getAbsolutePath(), "files").getAbsolutePath());
                rule.put("/storage/emulated/0/Android/data/" + packageName + "/cache",
                        new File(external.getAbsolutePath(), "cache").getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String key : rule.keySet()) {
            get().addRedirect(key, rule.get(key));
        }
    }
}
