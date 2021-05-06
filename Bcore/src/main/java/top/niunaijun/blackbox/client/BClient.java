package top.niunaijun.blackbox.client;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.app.Service;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.RemoteException;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import com.swift.sandhook.SandHook;
import com.swift.sandhook.SandHookConfig;
import com.swift.sandhook.xposedcompat.XposedCompat;

import java.io.File;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import mirror.android.app.ActivityManagerNative;
import mirror.android.app.ActivityThread;
import mirror.android.app.ActivityThreadNMR1;
import mirror.android.app.ActivityThreadQ;
import mirror.android.app.ContextImpl;
import mirror.android.app.LoadedApk;
import mirror.android.security.net.config.NetworkSecurityConfigProvider;
import mirror.com.android.internal.content.ReferrerIntent;
import mirror.dalvik.system.VMRuntime;
import top.niunaijun.blackbox.client.frameworks.BXposedManager;
import top.niunaijun.blackbox.client.hook.HookManager;
import top.niunaijun.blackbox.client.hook.IOManager;
import top.niunaijun.blackbox.client.hook.env.VirtualRuntime;
import top.niunaijun.blackbox.client.hook.fixer.ContextFixer;
import top.niunaijun.blackbox.client.hook.proxies.app.HCallbackStub;
import top.niunaijun.blackbox.client.hook.proxies.context.providers.ContentProviderStub;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.hook.delegate.ActivityLifecycleDelegate;
import top.niunaijun.blackbox.client.hook.delegate.AppInstrumentation;
import top.niunaijun.blackbox.client.hook.delegate.ContentProviderDelegate;
import top.niunaijun.blackbox.entity.ClientConfig;
import top.niunaijun.blackbox.entity.pm.InstalledModule;
import top.niunaijun.blackbox.server.ClientServiceManager;
import top.niunaijun.blackbox.utils.FileUtils;
import top.niunaijun.blackbox.utils.Slog;
import top.niunaijun.blackbox.utils.compat.ActivityManagerCompat;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.utils.compat.StrictModeCompat;


/**
 * Created by Milk on 3/31/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class BClient extends IBClient.Stub {
    public static final String TAG = "BClient";

    private static BClient sBClient;
    private AppBindData mBoundApplication;
    private Application mInitialApplication;
    private ClientConfig mClientConfig;
    private IInterface mActivityThread;
    private List<ProviderInfo> mProviders = new ArrayList<>();

    public static final int HANDLE_FINISH = 0;
    public static final int HANDLE_NEW_INTENT = 0;

    private final Handler mH = new Handler(Looper.getMainLooper());

    public static BClient getClient() {
        if (sBClient == null) {
            synchronized (BClient.class) {
                if (sBClient == null) {
                    sBClient = new BClient();
                }
            }
        }
        return sBClient;
    }

    public static synchronized ClientConfig getClientConfig() {
        return getClient().mClientConfig;
    }

    public static List<ProviderInfo> getProviders() {
        return getClient().mProviders;
    }

    public static String getVProcessName() {
        if (getClientConfig() != null) {
            return getClientConfig().processName;
        } else if (getClient().mBoundApplication != null) {
            return getClient().mBoundApplication.processName;
        } else {
            return null;
        }
    }

    public static String getVPackageName() {
        if (getClientConfig() != null) {
            return getClientConfig().packageName;
        } else if (getClient().mInitialApplication != null) {
            return getClient().mInitialApplication.getPackageName();
        } else {
            return null;
        }
    }

    public static Application getApplication() {
        return getClient().mInitialApplication;
    }

    public static int getVPid() {
        return getClientConfig() == null ? -1 : getClientConfig().vpid;
    }

    public static int getVUid() {
        return getClientConfig() == null ? 10000 : getClientConfig().vuid;
    }

    public static int getBaseVUid() {
        return getClientConfig() == null ? 10000 : getClientConfig().baseVUid;
    }

    public static int getUid() {
        return getClientConfig() == null ? -1 : getClientConfig().uid;
    }

    public static int getUserId() {
        return getClientConfig() == null ? 0 : getClientConfig().userId;
    }

    public void initProcess(ClientConfig clientConfig) {
        if (this.mClientConfig != null) {
            // 该进程已被attach
            throw new RuntimeException("reject init process: " + clientConfig.processName + ", this process is : " + this.mClientConfig.processName);
        }
        this.mClientConfig = clientConfig;
    }

    public boolean isInit() {
        return mBoundApplication != null;
    }

    public Service createService(ServiceInfo serviceInfo) {
        if (!BClient.getClient().isInit()) {
            BClient.getClient().bindApplication(serviceInfo.packageName, serviceInfo.processName);
        }
        ClassLoader classLoader = LoadedApk.getClassLoader.call(mBoundApplication.info);
        Service service;
        try {
            service = (Service) classLoader.loadClass(serviceInfo.name).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to instantiate service " + serviceInfo.name
                            + ": " + e.toString(), e);
        }

        try {
            Context context = BlackBoxCore.getContext().createPackageContext(
                    serviceInfo.packageName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY
            );
            ContextImpl.setOuterContext.call(context, service);
            mirror.android.app.Service.attach.call(
                    service,
                    context,
                    BlackBoxCore.mainThread(),
                    serviceInfo.name,
                    BClient.getClient().getActivityThread(),
                    mInitialApplication,
                    ActivityManagerNative.getDefault.call()
            );
            ContextFixer.fix(context);
            service.onCreate();
            return service;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to create service " + serviceInfo.name
                            + ": " + e.toString(), e);
        }
    }

    public JobService createJobService(ServiceInfo serviceInfo) {
        if (!BClient.getClient().isInit()) {
            BClient.getClient().bindApplication(serviceInfo.packageName, serviceInfo.processName);
        }
        ClassLoader classLoader = LoadedApk.getClassLoader.call(mBoundApplication.info);
        JobService service;
        try {
            service = (JobService) classLoader.loadClass(serviceInfo.name).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to instantiate service " + serviceInfo.name
                            + ": " + e.toString(), e);
        }

        try {
            Context context = BlackBoxCore.getContext().createPackageContext(
                    serviceInfo.packageName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY
            );
            ContextImpl.setOuterContext.call(context, service);
            mirror.android.app.Service.attach.call(
                    service,
                    context,
                    BlackBoxCore.mainThread(),
                    serviceInfo.name,
                    BClient.getClient().getActivityThread(),
                    mInitialApplication,
                    ActivityManagerNative.getDefault.call()
            );
            ContextFixer.fix(context);
            service.onCreate();
            service.onBind(null);
            return service;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to create JobService " + serviceInfo.name
                            + ": " + e.toString(), e);
        }
    }

    public void bindApplication(final String packageName, final String processName) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            final ConditionVariable conditionVariable = new ConditionVariable();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    handleBindApplication(packageName, processName);
                    conditionVariable.open();
                }
            });
            conditionVariable.block();
        } else {
            handleBindApplication(packageName, processName);
        }
    }

    public synchronized void handleBindApplication(String packageName, String processName) {
        try {
            CrashHandler.create();
        } catch (Throwable ignored) {
        }

        PackageInfo packageInfo = BlackBoxCore.getBPackageManager().getPackageInfo(packageName, PackageManager.GET_PROVIDERS, BClient.getUserId());
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        if (packageInfo.providers == null) {
            packageInfo.providers = new ProviderInfo[]{};
        }
        mProviders.addAll(Arrays.asList(packageInfo.providers));

        Object boundApplication = ActivityThread.mBoundApplication.get(BlackBoxCore.mainThread());

        Context packageContext = createPackageContext(applicationInfo);
        Object loadedApk = ContextImpl.mPackageInfo.get(packageContext);
        LoadedApk.mSecurityViolation.set(loadedApk, false);
        // fix applicationInfo
        LoadedApk.mApplicationInfo.set(loadedApk, applicationInfo);

        int targetSdkVersion = applicationInfo.targetSdkVersion;
        if (targetSdkVersion < Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.ThreadPolicy newPolicy = new StrictMode.ThreadPolicy.Builder(StrictMode.getThreadPolicy()).permitNetwork().build();
            StrictMode.setThreadPolicy(newPolicy);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (targetSdkVersion < Build.VERSION_CODES.N) {
                StrictModeCompat.disableDeathOnFileUriExposure();
            }
        }

        VMRuntime.setTargetSdkVersion.call(VMRuntime.getRuntime.call(), applicationInfo.targetSdkVersion);
        VMCore.init(Build.VERSION.SDK_INT);
        assert packageContext != null;
        IOManager.get().enableRedirect(packageContext);

        AppBindData bindData = new AppBindData();
        bindData.appInfo = applicationInfo;
        bindData.processName = processName;
        bindData.info = loadedApk;
        bindData.providers = mProviders;

        ActivityThread.AppBindData.instrumentationName.set(boundApplication,
                new ComponentName(bindData.appInfo.packageName, Instrumentation.class.getName()));
        ActivityThread.AppBindData.appInfo.set(boundApplication, bindData.appInfo);
        ActivityThread.AppBindData.info.set(boundApplication, bindData.info);
        ActivityThread.AppBindData.processName.set(boundApplication, bindData.processName);
        ActivityThread.AppBindData.providers.set(boundApplication, bindData.providers);

        mBoundApplication = bindData;

        //ssl适配
        if (NetworkSecurityConfigProvider.install != null) {
            Security.removeProvider("AndroidNSSP");
            NetworkSecurityConfigProvider.install.call(packageContext);
        }
        Application application;
        try {
            BlackBoxCore.get().getAppLifecycleCallback().beforeCreateApplication(packageName, processName, packageContext);
            application = LoadedApk.makeApplication.call(loadedApk, false, null);

            mInitialApplication = application;
            ActivityThread.mInitialApplication.set(BlackBoxCore.mainThread(), mInitialApplication);
            ContextFixer.fix((Context) ActivityThread.getSystemContext.call(BlackBoxCore.mainThread()));
            ContextFixer.fix(mInitialApplication);
            installProviders(mInitialApplication, bindData.processName, bindData.providers);

            BlackBoxCore.get().getAppLifecycleCallback().beforeApplicationOnCreate(packageName, processName, application);
            AppInstrumentation.get().callApplicationOnCreate(application);
            BlackBoxCore.get().getAppLifecycleCallback().afterApplicationOnCreate(packageName, processName, application);

            registerReceivers(mInitialApplication);
            application.registerActivityLifecycleCallbacks(new ActivityLifecycleDelegate());
            HookManager.get().checkEnv(HCallbackStub.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to makeApplication", e);
        }
        VirtualRuntime.setupRuntime(bindData.processName, applicationInfo);
    }

    private Context createPackageContext(ApplicationInfo info) {
        try {
            return BlackBoxCore.getContext().createPackageContext(info.packageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void installProviders(Context context, String processName, List<ProviderInfo> provider) {
        long origId = Binder.clearCallingIdentity();
        try {
            for (ProviderInfo providerInfo : provider) {
                try {
                    if (processName.equals(providerInfo.processName)) {
                        ActivityThread.installProvider(BlackBoxCore.mainThread(), context, providerInfo, null);
                    }
                } catch (Throwable ignored) {
                }
            }
        } finally {
            Binder.restoreCallingIdentity(origId);
            ContentProviderDelegate.init();
        }
    }

    public void loadXposed(Context context) {
        String vPackageName = getVPackageName();
        String vProcessName = getVProcessName();
        if (!TextUtils.isEmpty(vPackageName) && !TextUtils.isEmpty(vProcessName) && BXposedManager.get().isXPEnable()) {
            assert vPackageName != null;
            assert vProcessName != null;

            XposedCompat.packageName = vPackageName;
            XposedCompat.processName = vProcessName;
            XposedCompat.cacheDir = new File(context.getCacheDir(), vProcessName);
            FileUtils.mkdirs(XposedCompat.cacheDir);
            XposedCompat.context = context;
            XposedCompat.classLoader = context.getClassLoader();
            XposedCompat.isFirstApplication = vPackageName.equals(vProcessName);

            SandHook.disableVMInline();
            //android Q
            if (BuildCompat.isQ()) {
                XposedCompat.useInternalStub = false;
                XposedCompat.cacheDir = XposedCompat.context.getCacheDir();
            }

            List<InstalledModule> installedModules = BXposedManager.get().getInstalledModules();
            for (InstalledModule installedModule : installedModules) {
                if (!installedModule.enable) {
                    continue;
                }
                try {
                    XposedCompat.loadModule(installedModule.getApplication().sourceDir,
                            context.getCacheDir().getAbsolutePath(),
                            installedModule.getApplication().nativeLibraryDir,
                            BlackBoxCore.getContext().getClassLoader());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            try {
                XposedCompat.callXposedModuleInit();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        if (BlackBoxCore.get().isHideXposed()) {
            VMCore.hideXposed();
        }
    }

    @Override
    public IBinder getActivityThread() {
        return ActivityThread.getApplicationThread.call(BlackBoxCore.mainThread());
    }

    @Override
    public void bindApplication() {
        if (!isInit()) {
            bindApplication(getVPackageName(), getVProcessName());
        }
    }

    @Override
    public void stopService(ComponentName componentName) {
        ClientServiceManager.get().stopService(componentName);
    }

    @Override
    public void restartJobService(String selfId) throws RemoteException {

    }

    @Override
    public IBinder acquireContentProviderClient(ProviderInfo providerInfo) throws RemoteException {
        if (!isInit()) {
            bindApplication(BClient.getClientConfig().packageName, BClient.getClientConfig().processName);
        }
        ContentProviderClient contentProviderClient = BlackBoxCore.getContext()
                .getContentResolver().acquireContentProviderClient(providerInfo.authority);

        IInterface iInterface = mirror.android.content.ContentProviderClient.mContentProvider.get(contentProviderClient);
        if (iInterface == null)
            return null;
        IInterface proxyIInterface = new ContentProviderStub().wrapper(iInterface, BlackBoxCore.getHostPkg());
        return proxyIInterface.asBinder();
    }

    public void registerReceivers(Application application) {
        try {
            Intent intent = new Intent();
            intent.setPackage(application.getPackageName());
            List<ResolveInfo> resolves = BlackBoxCore.getBPackageManager().queryBroadcastReceivers(intent, PackageManager.GET_RESOLVED_FILTER, null, BClient.getUserId());
            for (ResolveInfo resolve : resolves) {
                try {
                    if (resolve.activityInfo.processName != null && !resolve.activityInfo.processName.equals(BClient.getVProcessName())) {
                        continue;
                    }
                    BroadcastReceiver broadcastReceiver = (BroadcastReceiver) mInitialApplication.getClassLoader().loadClass(resolve.activityInfo.name).newInstance();
                    mInitialApplication.registerReceiver(broadcastReceiver, resolve.filter);
                } catch (Throwable e) {
//                        e.printStackTrace();
                    Slog.d(TAG, "Unable to registerReceiver " + resolve.activityInfo.name
                            + ": " + e.toString());
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public IBinder peekService(Intent intent) {
        return ClientServiceManager.get().peekService(intent);
    }

    @Override
    public void finishActivity(final IBinder token) {
        mH.post(new Runnable() {
            @Override
            public void run() {
                Map<IBinder, Object> activities = ActivityThread.mActivities.get(BlackBoxCore.mainThread());
                if (activities.isEmpty())
                    return;
                Object clientRecord = activities.get(token);
                if (clientRecord == null)
                    return;
                Activity activity = ActivityThread.ActivityClientRecord.activity.get(clientRecord);

                while (activity.getParent() != null) {
                    activity = activity.getParent();
                }

                int resultCode = mirror.android.app.Activity.mResultCode.get(activity);
                Intent resultData = mirror.android.app.Activity.mResultData.get(activity);
                ActivityManagerCompat.finishActivity(token, resultCode, resultData);
                mirror.android.app.Activity.mFinished.set(activity, true);
            }
        });
    }

    @Override
    public void handleNewIntent(final IBinder token, final Intent intent) {
        mH.post(new Runnable() {
            @Override
            public void run() {
                Intent newIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    newIntent = ReferrerIntent.ctor.newInstance(intent, BlackBoxCore.getHostPkg());
                } else {
                    newIntent = intent;
                }
                if (ActivityThread.performNewIntents != null) {
                    ActivityThread.performNewIntents.call(
                            BlackBoxCore.mainThread(),
                            token,
                            Collections.singletonList(newIntent)
                    );
                } else if (ActivityThreadNMR1.performNewIntents != null) {
                    ActivityThreadNMR1.performNewIntents.call(
                            BlackBoxCore.mainThread(),
                            token,
                            Collections.singletonList(newIntent),
                            true);
                } else if (ActivityThreadQ.handleNewIntent != null) {
                    ActivityThreadQ.handleNewIntent.call(BlackBoxCore.mainThread(), token, Collections.singletonList(newIntent));
                }
            }
        });
    }

    public static class AppBindData {
        int vpid;
        String processName;
        ApplicationInfo appInfo;
        List<ProviderInfo> providers;
        Object info;
    }
}
