package top.niunaijun.blackbox.client.hook.proxies.am;

import android.app.ActivityManager;
import android.app.IServiceConnection;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Process;
import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import mirror.android.app.ActivityManagerNative;
import mirror.android.app.ActivityManagerOreo;

import mirror.android.content.ContentProviderNative;
import mirror.android.util.Singleton;
import top.niunaijun.blackbox.client.hook.env.ClientSystemEnv;
import top.niunaijun.blackbox.client.hook.proxies.context.providers.ContentProviderStub;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.entity.ClientConfig;
import top.niunaijun.blackbox.client.StubManifest;
import top.niunaijun.blackbox.client.BClient;
import top.niunaijun.blackbox.client.hook.ClassInvocationStub;
import top.niunaijun.blackbox.client.hook.MethodHook;
import top.niunaijun.blackbox.client.hook.delegate.ContentProviderDelegate;
import top.niunaijun.blackbox.client.hook.delegate.ServiceConnectionDelegate;
import top.niunaijun.blackbox.client.stub.StubActivity;
import top.niunaijun.blackbox.utils.ComponentUtils;
import top.niunaijun.blackbox.utils.MethodParameterUtils;
import top.niunaijun.blackbox.utils.Reflector;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

import static android.content.pm.PackageManager.GET_META_DATA;

/**
 * Created by Milk on 3/30/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ActivityManagerStub extends ClassInvocationStub {
    public static final String TAG = "ActivityManagerStub";

    @Override
    protected Object getWho() {
        Object iActivityManager = null;
        if (BuildCompat.isOreo()) {
            iActivityManager = ActivityManagerOreo.IActivityManagerSingleton.get();
        } else if (BuildCompat.isL()) {
            iActivityManager = ActivityManagerNative.gDefault.get();
        }
        return Singleton.get.call(iActivityManager);
    }

    @Override
    protected void inject(Object base, Object proxy) {
        Object iActivityManager = null;
        if (BuildCompat.isOreo()) {
            iActivityManager = ActivityManagerOreo.IActivityManagerSingleton.get();
        } else if (BuildCompat.isL()) {
            iActivityManager = ActivityManagerNative.gDefault.get();
        }
        Singleton.mInstance.set(iActivityManager, proxy);
    }

    @Override
    protected void onBindMethod() {
        addMethodHook(new GetContentProvider());
        addMethodHook(new StartService());
        addMethodHook(new StopService());
        addMethodHook(new BindService());
        addMethodHook(new BindIsolatedService());
        addMethodHook(new UnbindService());
        addMethodHook(new GetRunningAppProcesses());
        addMethodHook(new GetIntentSender());
        addMethodHook(new GetIntentSenderWithFeature());
        addMethodHook(new BroadcastIntent());
        addMethodHook(new BroadcastIntentWithFeature());
        addMethodHook(new PublishService());
        addMethodHook(new PeekService());
        addMethodHook(new SendIntentSender());
        addMethodHook(new RegisterReceiver());
        addMethodHook(new GrantUriPermission());

        addMethodHook(new CommonStub.StartActivity());
        addMethodHook(new CommonStub.StartActivities());
        addMethodHook(new CommonStub.ActivityDestroyed());
        addMethodHook(new CommonStub.ActivityResumed());
        addMethodHook(new CommonStub.FinishActivity());
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    static class GetContentProvider extends MethodHook {
        @Override
        protected String getMethodName() {
            return "getContentProvider";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Exception {
            int authIndex = getAuthIndex();
            Object auth = args[authIndex];
            Object content = null;

            if (auth instanceof String) {
                if (StubManifest.isStub((String) auth)) {
                    return method.invoke(who, args);
                }

                if (BuildCompat.isQ()) {
                    args[1] = BlackBoxCore.getHostPkg();
                }

                if (auth.equals("settings") || auth.equals("media") || auth.equals("telephony")) {
                    content = method.invoke(who, args);
                    ContentProviderDelegate.update(content, (String) auth);
                    return content;
                } else {
                    Log.d(TAG, "hook getContentProvider: " + auth);


                    ProviderInfo providerInfo = BlackBoxCore.getBPackageManager().resolveContentProvider((String) auth, GET_META_DATA, BClient.getUserId());
                    if (providerInfo == null || !providerInfo.packageName.equals(BClient.getVPackageName())) {
                        Object invoke = method.invoke(who, args);
                        if (invoke != null) {
                            Object provider = Reflector.with(invoke)
                                    .field("provider")
                                    .get();
                            if (provider != null && !(provider instanceof Proxy)) {
                                Reflector.with(invoke)
                                        .field("provider")
                                        .set(new ContentProviderStub().wrapper((IInterface) provider, BlackBoxCore.getHostPkg()));
                            }
                        }
                        return invoke;
                    }

                    IBinder providerBinder = null;
                    if (BClient.getVPid() != -1) {
                        ClientConfig clientConfig = BlackBoxCore.getBActivityManager().initProcess(providerInfo.packageName, providerInfo.processName, BClient.getUserId());
                        if (clientConfig.vpid != BClient.getVPid()) {
                            providerBinder = BlackBoxCore.getBActivityManager().acquireContentProviderClient(providerInfo);
                        }
                        args[authIndex] = StubManifest.getStubAuthorities(clientConfig.vpid);
                        args[getUserIndex()] = 0;
                    }
                    content = method.invoke(who, args);

                    Reflector.with(content)
                            .field("info")
                            .set(providerInfo);

                    if (providerBinder != null) {
                        Reflector.with(content)
                                .field("provider")
                                .set(ContentProviderNative.asInterface.call(providerBinder));
                    }
                }

                return content;
            }
            return method.invoke(who, args);
        }

        private int getAuthIndex() {
            // 10.0
            if (BuildCompat.isQ()) {
                return 2;
            } else {
                return 1;
            }
        }

        private int getUserIndex() {
            return getAuthIndex() + 1;
        }
    }

    static class StartService extends MethodHook {

        @Override
        protected String getMethodName() {
            return "startService";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[1];
            String resolvedType = (String) args[2];
            ResolveInfo resolveInfo = BlackBoxCore.getBPackageManager().resolveService(intent, 0, resolvedType, BClient.getUserId());
            if (resolveInfo == null) {
                return method.invoke(who, args);
            }
            return BlackBoxCore.getBActivityManager().startService(intent, resolvedType, BClient.getUserId());
        }
    }

    static class StopService extends MethodHook {
        @Override
        protected String getMethodName() {
            return "stopService";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[1];
            String resolvedType = (String) args[2];
            return BlackBoxCore.getBActivityManager().stopService(intent, resolvedType, BClient.getUserId());
        }
    }

    static class BindService extends MethodHook {
        @Override
        protected String getMethodName() {
            return "bindService";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[2];
            String resolvedType = (String) args[3];
            IServiceConnection connection = (IServiceConnection) args[4];
            // 暂不支持gms
            if (ComponentUtils.isGmsService(intent)) {
                return 0;
            }
            ResolveInfo resolveInfo = BlackBoxCore.getBPackageManager().resolveService(intent, 0, resolvedType, BClient.getUserId());
            if (resolveInfo != null || ClientSystemEnv.isOpenPackage(intent.getComponent())) {
                Intent proxyIntent = BlackBoxCore.getBActivityManager().bindService(intent,
                        connection == null ? null : connection.asBinder(),
                        resolvedType,
                        BClient.getUserId());
                if (connection != null) {
                    args[4] = ServiceConnectionDelegate.createProxy(connection, intent);
                }
                if (proxyIntent != null) {
                    args[2] = proxyIntent;
                    return method.invoke(who, args);
                }
            }
            return 0;
        }
    }

    // 10.0
    static class BindIsolatedService extends BindService {
        @Override
        protected String getMethodName() {
            return "bindIsolatedService";
        }

        @Override
        protected Object beforeHook(Object who, Method method, Object[] args) throws Throwable {
            // instanceName
            args[6] = null;
            return super.beforeHook(who, method, args);
        }
    }

    static class UnbindService extends MethodHook {
        @Override
        protected String getMethodName() {
            return "unbindService";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IServiceConnection iServiceConnection = (IServiceConnection) args[0];
            if (iServiceConnection == null) {
                return method.invoke(who, args);
            }
            BlackBoxCore.getBActivityManager().unbindService(iServiceConnection.asBinder(), BClient.getUserId());
            ServiceConnectionDelegate delegate = ServiceConnectionDelegate.getDelegate(iServiceConnection.asBinder());
            if (delegate != null) {
                args[0] = delegate;
            }
            return method.invoke(who, args);
        }
    }

    static class GetRunningAppProcesses extends MethodHook {
        @Override
        protected String getMethodName() {
            return "getRunningAppProcesses";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            List<ActivityManager.RunningAppProcessInfo> invoke = (List<ActivityManager.RunningAppProcessInfo>) method.invoke(who, args);
            if (invoke == null || BClient.getClientConfig() == null)
                return null;
            boolean findSelf = false;
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : invoke) {
                if (runningAppProcessInfo.pid == Process.myPid()) {
                    runningAppProcessInfo.processName = BClient.getVProcessName();
                    findSelf = true;
                }
            }
            if (!findSelf) {
                invoke.add(new ActivityManager.RunningAppProcessInfo(BClient.getVProcessName(), Process.myPid(), new String[]{BClient.getVPackageName()}));
            }
            return invoke;
        }
    }

    static class GetIntentSender extends MethodHook {
        @Override
        protected String getMethodName() {
            return "getIntentSender";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            Intent[] intents = (Intent[]) args[getIntentsIndex()];

            // todo
            for (Intent intent : intents) {
                intent.setComponent(new ComponentName(BlackBoxCore.getHostPkg(), StubActivity.P0.class.getName()));
            }
            return method.invoke(who, args);
        }

        private int getIntentsIndex() {
            if (BuildCompat.isR()) {
                return 6;
            } else {
                return 5;
            }
        }
    }

    static class GetIntentSenderWithFeature extends GetIntentSender {
        @Override
        protected String getMethodName() {
            return "getIntentSenderWithFeature";
        }
    }

    static class BroadcastIntentWithFeature extends BroadcastIntent {
        @Override
        protected String getMethodName() {
            return "broadcastIntentWithFeature";
        }
    }

    static class BroadcastIntent extends MethodHook {

        @Override
        protected String getMethodName() {
            return "broadcastIntent";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int intentIndex = getIntentIndex(args);
            Intent intent = (Intent) args[intentIndex];
            String resolvedType = (String) args[intentIndex + 1];
            Intent proxyIntent = BlackBoxCore.getBActivityManager().sendBroadcast(intent, resolvedType, BClient.getUserId());
            if (proxyIntent != null) {
                args[intentIndex] = proxyIntent;
            }
            for (int i = 0; i < args.length; i++) {
                Object o = args[i];
                if (o instanceof String[]) {
                    args[i] = null;
                }
            }
            return method.invoke(who, args);
        }

        int getIntentIndex(Object[] args) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof Intent) {
                    return i;
                }
            }
            return 1;
        }
    }

    static class PublishService extends MethodHook {

        @Override
        protected String getMethodName() {
            return "publishService";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return method.invoke(who, args);
        }
    }

    static class PeekService extends MethodHook {

        @Override
        protected String getMethodName() {
            return "peekService";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            Intent intent = (Intent) args[0];
            String resolvedType = (String) args[1];
            IBinder peek = BlackBoxCore.getBActivityManager().peekService(intent, resolvedType, BClient.getUserId());
            return peek;
        }
    }

    // todo
    static class SendIntentSender extends MethodHook {

        @Override
        protected String getMethodName() {
            return "sendIntentSender";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    static class RegisterReceiver extends MethodHook {

        @Override
        protected String getMethodName() {
            return "registerReceiver";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            args[4] = null;
            return method.invoke(who, args);
        }
    }

    static class GrantUriPermission extends MethodHook {

        @Override
        protected String getMethodName() {
            return "grantUriPermission";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastUserId(args);
            return method.invoke(who, args);
        }
    }
}
