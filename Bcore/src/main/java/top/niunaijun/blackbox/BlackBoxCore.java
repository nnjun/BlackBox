package top.niunaijun.blackbox;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;

import top.niunaijun.blackbox.client.StubManifest;
import top.niunaijun.blackbox.client.frameworks.BUserManager;
import top.niunaijun.blackbox.client.frameworks.BXpoesdManager;
import top.niunaijun.blackbox.client.hook.HookManager;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.entity.pm.InstallResult;
import top.niunaijun.blackbox.entity.pm.InstalledModule;
import top.niunaijun.blackbox.server.DaemonService;
import top.niunaijun.blackbox.server.user.BUserHandle;
import top.niunaijun.blackbox.server.user.BUserInfo;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.utils.compat.BundleCompat;
import top.niunaijun.blackbox.utils.compat.XPoesdParserCompat;
import top.niunaijun.blackbox.utils.provider.ProviderCall;
import top.niunaijun.blackbox.client.frameworks.BActivityManager;
import top.niunaijun.blackbox.client.frameworks.BJobManager;
import top.niunaijun.blackbox.client.frameworks.BPackageManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
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
    private Thread.UncaughtExceptionHandler mExceptionHandler;

    public static BlackBoxCore get() {
        return sVirtualCore;
    }

    public static PackageManager getPackageManager() {
        return sContext.getPackageManager();
    }

    public static String getHostPkg() {
        return getContext().getPackageName();
    }

    public static Context getContext() {
        return sContext;
    }

    public Thread.UncaughtExceptionHandler getExceptionHandler() {
        return mExceptionHandler;
    }

    public void setExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
        mExceptionHandler = exceptionHandler;
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

    public boolean launchApk(String packageName, int userId) {
        Intent launchIntentForPackage = getBPackageManager().getLaunchIntentForPackage(packageName, userId);
        if (launchIntentForPackage == null) {
            return false;
        }
        startActivity(launchIntentForPackage, userId);
        return true;
    }

    public boolean launchXPModule(String packageName) {
        return launchApk(packageName, BUserHandle.USER_XPOESD);
    }

    public boolean isInstalled(String packageName, int userId) {
        return getBPackageManager().isInstalled(packageName, userId);
    }

    public void uninstallPackageAsUser(String packageName, int userId) {
        getBPackageManager().uninstallPackageAsUser(packageName, userId);
    }

    public void uninstallPackage(String packageName) {
        getBPackageManager().uninstallPackage(packageName);
    }

    public InstallResult installPackageAsUser(String packageName, int userId) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
            return getBPackageManager().installPackageAsUser(packageInfo.applicationInfo.sourceDir, InstallOption.installBySystem(), userId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return new InstallResult().installError(e.getMessage());
        }
    }

    public InstallResult installPackageAsUser(File apk, int userId) {
        return getBPackageManager().installPackageAsUser(apk.getAbsolutePath(), InstallOption.installByStorage(), userId);
    }

    public InstallResult installPackageAsUser(Uri apk, int userId) {
        return getBPackageManager().installPackageAsUser(apk.toString(), InstallOption.installByStorage().makeUriFile(), userId);
    }

    public InstallResult installXPModule(File apk) {
        return getBPackageManager().installPackageAsUser(apk.getAbsolutePath(), InstallOption.installByStorage().makeXPoesd(), BUserHandle.USER_XPOESD);
    }

    public InstallResult installXPModule(Uri apk) {
        return getBPackageManager().installPackageAsUser(apk.toString(), InstallOption.installByStorage()
                .makeXPoesd()
                .makeUriFile(), BUserHandle.USER_XPOESD);
    }

    public InstallResult installXPModule(String packageName) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
            String path = packageInfo.applicationInfo.sourceDir;
            return getBPackageManager().installPackageAsUser(path, InstallOption.installBySystem().makeXPoesd(), BUserHandle.USER_XPOESD);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return new InstallResult().installError(e.getMessage());
        }
    }

    public void uninstallXPModule(String packageName) {
        uninstallPackageAsUser(packageName, BUserHandle.USER_XPOESD);
    }

    public boolean isXPEnable() {
        return BXpoesdManager.get().isXPEnable();
    }

    public void setXPEnable(boolean enable) {
        BXpoesdManager.get().setXPEnable(enable);
    }

    public boolean isXPoesdModule(File file) {
        return XPoesdParserCompat.isXPModule(file.getAbsolutePath());
    }

    public boolean isInstalledXPoesdModule(String packageName) {
        return isInstalled(packageName, BUserHandle.USER_XPOESD);
    }

    public boolean isModuleEnable(String packageName) {
        return BXpoesdManager.get().isModuleEnable(packageName);
    }

    public void setModuleEnable(String packageName, boolean enable) {
        BXpoesdManager.get().setModuleEnable(packageName, enable);
    }

    public List<InstalledModule> getInstalledXPModules() {
        return BXpoesdManager.get().getInstalledModules();
    }

    public List<ApplicationInfo> getInstalledApplications(int flags, int userId) {
        return getBPackageManager().getInstalledApplications(flags, userId);
    }

    public List<PackageInfo> getInstalledPackages(int flags, int userId) {
        return getBPackageManager().getInstalledPackages(flags, userId);
    }

    public List<BUserInfo> getUsers() {
        return BUserManager.get().getUsers();
    }

    public BUserInfo createUser(int userId) {
        return BUserManager.get().createUser(userId);
    }

    public void deleteUser(int userId) {
        BUserManager.get().deleteUser(userId);
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
