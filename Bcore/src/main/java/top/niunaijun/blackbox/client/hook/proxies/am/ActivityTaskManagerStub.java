package top.niunaijun.blackbox.client.hook.proxies.am;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;

import java.lang.reflect.Method;

import mirror.android.app.IActivityTaskManager;
import mirror.android.os.ServiceManager;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.BClient;
import top.niunaijun.blackbox.client.hook.BinderInvocationStub;
import top.niunaijun.blackbox.client.hook.MethodHook;
import top.niunaijun.blackbox.client.hook.provider.FileProviderHandler;
import top.niunaijun.blackbox.utils.ComponentUtils;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.utils.compat.StartActivityCompat;

import static android.content.pm.PackageManager.GET_META_DATA;

/**
 * Created by Milk on 3/31/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ActivityTaskManagerStub extends BinderInvocationStub {
    public static final String TAG = "ActivityTaskManager";

    public ActivityTaskManagerStub() {
        super(ServiceManager.getService.call("activity_task"));
    }

    @Override
    protected Object getWho() {
        return IActivityTaskManager.Stub.asInterface.call(ServiceManager.getService.call("activity_task"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("activity_task");
    }

    @Override
    protected void onBindMethod() {
        addMethodHook(new StartActivity());
        addMethodHook(new StartActivities());
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    static class StartActivity extends MethodHook {

        @Override
        protected String getMethodName() {
            return "startActivity";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Object arg = args[getIntentIndex()];
            if (arg instanceof Intent) {
                Intent intent = (Intent) arg;
                if (intent.getParcelableExtra("_VM_|_target_") != null) {
                    return method.invoke(who, args);
                }
                if (ComponentUtils.isRequestInstall(intent)) {
                    intent.setData(FileProviderHandler.convertFileUri(BClient.getApplication(), intent.getData()));
                    return method.invoke(who, args);
                }
                if (!ComponentUtils.isSelf(intent)) {
                    return method.invoke(who, args);
                }
                ResolveInfo resolveInfo = BlackBoxCore.getVPackageManager().resolveActivity(
                        intent,
                        GET_META_DATA,
                        StartActivityCompat.getResolvedType(args),
                        BClient.getUserId());
                if (resolveInfo == null) {
                    return method.invoke(who, args);
                }

                intent.setExtrasClassLoader(who.getClass().getClassLoader());
                BlackBoxCore.getVActivityManager().startActivityAms(BClient.getUserId(),
                        StartActivityCompat.getIntent(args),
                        StartActivityCompat.getResolvedType(args),
                        StartActivityCompat.getResultTo(args),
                        StartActivityCompat.getResultWho(args),
                        StartActivityCompat.getRequestCode(args),
                        StartActivityCompat.getFlags(args),
                        StartActivityCompat.getOptions(args));
                return 0;
            }
            return method.invoke(who, args);
        }

        private int getIntentIndex() {
            if (BuildCompat.isR()) {
                return 3;
            } else {
                return 2;
            }
        }
    }

    static class StartActivities extends MethodHook {

        @Override
        protected String getMethodName() {
            return "startActivities";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent[] intents = (Intent[]) args[2];
            String[] resolvedTypes = (String[]) args[3];
            IBinder resultTo = (IBinder) args[4];
            Bundle options = (Bundle) args[5];
            if (!ComponentUtils.isSelf(intents)) {
                return method.invoke(who, args);
            }

            for (Intent intent : intents) {
                intent.setExtrasClassLoader(who.getClass().getClassLoader());
            }
            return BlackBoxCore.getVActivityManager().startActivities(BClient.getUserId(),
                    intents, resolvedTypes, resultTo, options);
        }
    }
}
