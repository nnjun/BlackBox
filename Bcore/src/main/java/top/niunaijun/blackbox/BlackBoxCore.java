package top.niunaijun.blackbox;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;

import top.niunaijun.blackbox.client.StubManifest;
import top.niunaijun.blackbox.client.hook.HookManager;
import top.niunaijun.blackbox.server.DaemonService;
import top.niunaijun.blackbox.utils.AbiUtils;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.utils.compat.BundleCompat;
import top.niunaijun.blackbox.utils.provider.ProviderCall;
import top.niunaijun.blackbox.client.frameworks.BActivityManager;
import top.niunaijun.blackbox.client.frameworks.BJobManager;
import top.niunaijun.blackbox.client.frameworks.BPackageManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


import me.weishu.reflection.Reflection;
import mirror.android.app.ActivityThread;
import top.niunaijun.blackbox.client.frameworks.BStorageManager;
import top.niunaijun.blackbox.client.hook.delegate.ContentProviderDelegate;
import top.niunaijun.blackbox.server.ServiceManager;

/**
 * Created by Milk on 3/30/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
@SuppressLint("StaticFieldLeak")
public class BlackBoxCore {
    public static final String TAG = "VirtualCore";

    private static BlackBoxCore sVirtualCore = new BlackBoxCore();
    private static Context sContext;
    private ProcessType mProcessType;
    private Map<String, IBinder> mServices = new HashMap<>();

    public static BlackBoxCore get() {
        return sVirtualCore;
    }

    public static PackageManager getPackageManager() {
        return sContext.getPackageManager();
    }

    private static Map<String, Boolean> sInstalled = new HashMap<>();

    public static boolean isAppInstalled(String packageName) {
        if (sInstalled.get(packageName) != null) {
            return sInstalled.get(packageName);
        }
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            sInstalled.put(packageName, packageInfo != null);
            return sInstalled.get(packageName);
        } catch (Exception e) {
            sInstalled.put(packageName, false);
            return false;
        }
    }

    public static String getHostPkg() {
        return getContext().getPackageName();
    }

    public static Context getContext() {
        return sContext;
    }

    public void doAttachBaseContext(Context context) {
        Reflection.unseal(context);
        sContext = context;
        String processName = getProcessName(getContext());
        if (processName.equals(BlackBoxCore.getHostPkg())) {
            mProcessType = ProcessType.Main;
        } else if (processName.endsWith(getContext().getString(R.string.black_box_service_name))) {
            mProcessType = ProcessType.Server;
        } else {
            mProcessType = ProcessType.VAppClient;
        }
        if (BlackBoxCore.get().isVirtualProcess()) {
            if (processName.endsWith("p0")) {
//                android.os.Debug.waitForDebugger();
            }
//            android.os.Debug.waitForDebugger();
        }
        if (isServerProcess()) {
            Intent intent = new Intent();
            intent.setClass(getContext(), DaemonService.class);
            if (BuildCompat.isOreo()) {
                getContext().startForegroundService(intent);
            } else {
                getContext().startService(intent);
            }
        }
        HookManager.get().init();
    }

    public void doCreate() {
        // fix contentProvider
        if (isVirtualProcess()) {
            ContentProviderDelegate.init();
        }
        if (!isServerProcess()) {
            initService();
        }
    }

    private void initService() {
        get().getService(ServiceManager.ACTIVITY_MANAGER);
        get().getService(ServiceManager.PACKAGE_MANAGER);
        get().getService(ServiceManager.STORAGE_MANAGER);
        get().getService(ServiceManager.JOB_MANAGER);
    }

    public static Object mainThread() {
        return ActivityThread.currentActivityThread.call();
    }

    public void launchApk(File apk) throws RuntimeException {
        if (!AbiUtils.isSupport(apk)) {
            throw new RuntimeException("The current environment does not support running this app");
        }
        int userId = 0;
        PackageInfo packageInfo = getBPackageManager().loadPackage(apk.getAbsolutePath(), userId);
        if (packageInfo != null) {
            Intent launchIntentForPackage = getBPackageManager().getLaunchIntentForPackage(packageInfo.packageName, userId);
            if (launchIntentForPackage == null) {
                return;
            }
            startActivity(launchIntentForPackage, userId);
        }
    }

    public void launchApk(String packageName) throws RuntimeException {
        try {
            int userId = 0;
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (!AbiUtils.isSupport(new File(packageInfo.applicationInfo.sourceDir))) {
                throw new RuntimeException("The current environment does not support running this app");
            }
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            getBPackageManager().loadPackage(applicationInfo.sourceDir, userId);
            Intent launchIntentForPackage = getBPackageManager().getLaunchIntentForPackage(packageInfo.packageName, userId);
            if (launchIntentForPackage == null) {
                return;
            }
            startActivity(launchIntentForPackage, userId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void startActivity(Intent intent, int userId) {
        getBActivityManager().startActivity(intent, userId);
    }

    public static BJobManager getBJobManager() {
        return BJobManager.get();
    }

    public static BPackageManager getBPackageManager() {
        return BPackageManager.get();
    }

    public static BActivityManager getBActivityManager() {
        return BActivityManager.get();
    }

    public static BStorageManager getBStorageManager() {
        return BStorageManager.get();
    }

    public IBinder getService(String name) {
        IBinder binder = mServices.get(name);
        if (binder != null && binder.isBinderAlive()) {
            return binder;
        }
        Bundle bundle = new Bundle();
        bundle.putString("_VM_|_server_name_", name);
        Bundle vm = ProviderCall.callSafely(StubManifest.getBindProvider(), "VM", null, bundle);
        assert vm != null;
        binder = BundleCompat.getBinder(vm, "_VM_|_server_");
        mServices.put(name, binder);
        return binder;
    }

    /**
     * Process type
     */
    private enum ProcessType {
        /**
         * Server process
         */
        Server,
        /**
         * Virtual app process
         */
        VAppClient,
        /**
         * Main process
         */
        Main,
    }

    public boolean isVirtualProcess() {
        return mProcessType == ProcessType.VAppClient;
//        return mProcessType == ProcessType.VAppClient || mProcessType == ProcessType.Main;
    }

    public boolean isMainProcess() {
        return mProcessType == ProcessType.Main;
    }

    public boolean isServerProcess() {
        return mProcessType == ProcessType.Server;
    }

    private static String getProcessName(Context context) {
        int pid = Process.myPid();
        String processName = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
            if (info.pid == pid) {
                processName = info.processName;
                break;
            }
        }
        if (processName == null) {
            throw new RuntimeException("processName = null");
        }
        return processName;
    }

    public static boolean is64Bit() {
        if (BuildCompat.isM()) {
            return Process.is64Bit();
        } else {
            return Build.CPU_ABI.equals("arm64-v8a");
        }
    }
}
