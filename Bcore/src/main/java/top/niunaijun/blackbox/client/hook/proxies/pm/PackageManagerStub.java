package top.niunaijun.blackbox.client.hook.proxies.pm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.IInterface;
import android.os.Process;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

import mirror.android.app.ActivityThread;
import mirror.android.app.ContextImpl;
import mirror.android.content.pm.ParceledListSlice;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.BClient;
import top.niunaijun.blackbox.client.hook.BinderInvocationStub;
import top.niunaijun.blackbox.client.hook.MethodHook;
import top.niunaijun.blackbox.client.hook.env.ClientSystemEnv;
import top.niunaijun.blackbox.utils.ComponentUtils;
import top.niunaijun.blackbox.utils.MethodParameterUtils;
import top.niunaijun.blackbox.utils.Reflector;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.utils.compat.ParceledListSliceCompat;

/**
 * Created by Milk on 3/30/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class PackageManagerStub extends BinderInvocationStub {
    public static final String TAG = "PackageManagerStub";

    public PackageManagerStub() {
        super(ActivityThread.sPackageManager.get().asBinder());
    }

    @Override
    protected Object getWho() {
        return ActivityThread.sPackageManager.get();
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        ActivityThread.sPackageManager.set((IInterface) proxyInvocation);
        replaceSystemService("package");
        Object systemContext = ActivityThread.getSystemContext.call(BlackBoxCore.mainThread());
        PackageManager packageManager = ContextImpl.mPackageManager.get(systemContext);
        if (packageManager != null) {
            try {
                Reflector.on("android.app.ApplicationPackageManager")
                        .field("mPM")
                        .set(packageManager, proxyInvocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ResolveIntent());
        addMethodHook(new SetComponentEnabledSetting());
        addMethodHook(new GetPackageInfo());
        addMethodHook(new GetProviderInfo());
        addMethodHook(new GetServiceInfo());
        addMethodHook(new GetActivityInfo());
        addMethodHook(new GetReceiverInfo());
        addMethodHook(new GetInstalledApplications());
        addMethodHook(new GetInstalledPackages());
        addMethodHook(new GetApplicationInfo());
        addMethodHook(new QueryContentProviders());
        addMethodHook(new ResolveContentProvider());
        addMethodHook(new CanRequestPackageInstalls());
        addMethodHook(new GetPackageUid());
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    static class ResolveIntent extends MethodHook {

        @Override
        protected String getMethodName() {
            return "resolveIntent";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[0];
            String resolvedType = (String) args[1];
            int flags = (int) args[2];
            ResolveInfo resolveInfo = BlackBoxCore.getBPackageManager().resolveIntent(intent, resolvedType, flags, BClient.getUserId());
            if (resolveInfo != null) {
                return resolveInfo;
            }
            return method.invoke(who, args);
        }
    }

    static class SetComponentEnabledSetting extends MethodHook {

        @Override
        protected String getMethodName() {
            return "setComponentEnabledSetting";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            // todo
            return 0;
        }
    }

    static class GetPackageInfo extends MethodHook {

        @Override
        protected String getMethodName() {
            return "getPackageInfo";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String packageName = (String) args[0];
            int flag = (int) args[1];
//            if (ClientSystemEnv.isFakePackage(packageName)) {
//                packageName = BlackBoxCore.getHostPkg();
//            }
            PackageInfo packageInfo = BlackBoxCore.getBPackageManager().getPackageInfo(packageName, flag, BClient.getUserId());
            if (packageInfo != null) {
                return packageInfo;
            }
            if (ClientSystemEnv.isOpenPackage(packageName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    static class GetPackageUid extends MethodHook {

        @Override
        protected String getMethodName() {
            return "getPackageUid";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    static class GetProviderInfo extends MethodHook {

        @Override
        protected String getMethodName() {
            return "getProviderInfo";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            int flags = (int) args[1];
            ProviderInfo providerInfo = BlackBoxCore.getBPackageManager().getProviderInfo(componentName, flags, BClient.getUserId());
            if (providerInfo != null)
                return providerInfo;
            if (ClientSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    static class GetReceiverInfo extends MethodHook {

        @Override
        protected String getMethodName() {
            return "getReceiverInfo";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            int flags = (int) args[1];
            ActivityInfo receiverInfo = BlackBoxCore.getBPackageManager().getReceiverInfo(componentName, flags, BClient.getUserId());
            if (receiverInfo != null)
                return receiverInfo;
            if (ClientSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    static class GetActivityInfo extends MethodHook {

        @Override
        protected String getMethodName() {
            return "getActivityInfo";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            int flags = (int) args[1];
            ActivityInfo activityInfo = BlackBoxCore.getBPackageManager().getActivityInfo(componentName, flags, BClient.getUserId());
            if (activityInfo != null)
                return activityInfo;
            if (ClientSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }


    static class GetServiceInfo extends MethodHook {

        @Override
        protected String getMethodName() {
            return "getServiceInfo";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            int flags = (int) args[1];
            ServiceInfo serviceInfo = BlackBoxCore.getBPackageManager().getServiceInfo(componentName, flags, BClient.getUserId());
            if (serviceInfo != null)
                return serviceInfo;
            if (ClientSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    static class GetInstalledApplications extends MethodHook {

        @Override
        protected String getMethodName() {
            return "getInstalledApplications";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int flags = (int) args[0];
            List<ApplicationInfo> installedApplications = BlackBoxCore.getBPackageManager().getInstalledApplications(flags, BClient.getUserId());
            return ParceledListSliceCompat.create(installedApplications);
        }
    }

    static class GetInstalledPackages extends MethodHook {

        @Override
        protected String getMethodName() {
            return "getInstalledPackages";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int flags = (int) args[0];
            List<PackageInfo> installedPackages = BlackBoxCore.getBPackageManager().getInstalledPackages(flags, BClient.getUserId());
            return ParceledListSliceCompat.create(installedPackages);
        }
    }

    static class GetApplicationInfo extends MethodHook {

        @Override
        protected String getMethodName() {
            return "getApplicationInfo";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String packageName = (String) args[0];
            int flags = (int) args[1];
//            if (ClientSystemEnv.isFakePackage(packageName)) {
//                packageName = BlackBoxCore.getHostPkg();
//            }
            ApplicationInfo applicationInfo = BlackBoxCore.getBPackageManager().getApplicationInfo(packageName, flags, BClient.getUserId());
            if (applicationInfo != null) {
                return applicationInfo;
            }
            if (ClientSystemEnv.isOpenPackage(packageName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    static class QueryContentProviders extends MethodHook {

        @Override
        protected String getMethodName() {
            return "queryContentProviders";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int flags = (int) args[2];
            List<ProviderInfo> providers = BlackBoxCore.getBPackageManager().
                    queryContentProviders(BClient.getVProcessName(), Process.myUid(), flags, BClient.getUserId());
            return ParceledListSliceCompat.create(providers);
        }
    }

    static class ResolveContentProvider extends MethodHook {

        @Override
        protected String getMethodName() {
            return "resolveContentProvider";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String authority = (String) args[0];
            int flags = (int) args[1];
            ProviderInfo providerInfo = BlackBoxCore.getBPackageManager().resolveContentProvider(authority, flags, BClient.getUserId());
            if (providerInfo == null) {
                return method.invoke(who, args);
            }
            return providerInfo;
        }
    }

    static class CanRequestPackageInstalls extends MethodHook {

        @Override
        protected String getMethodName() {
            return "canRequestPackageInstalls";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }
}
