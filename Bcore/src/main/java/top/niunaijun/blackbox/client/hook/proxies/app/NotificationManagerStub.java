package top.niunaijun.blackbox.client.hook.proxies.app;

import android.content.Context;
import android.os.IInterface;

import java.lang.reflect.Method;

import mirror.android.app.NotificationManager;
import top.niunaijun.blackbox.client.hook.BinderInvocationStub;
import top.niunaijun.blackbox.client.hook.MethodHook;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Created by Milk on 4/2/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class NotificationManagerStub extends BinderInvocationStub {

    public NotificationManagerStub() {
        super(NotificationManager.getService.call().asBinder());
    }

    @Override
    protected Object getWho() {
        return NotificationManager.getService.call();
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        NotificationManager.sService.set((IInterface) getProxyInvocation());
        replaceSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new CancelNotificationWithTag());
        addMethodHook(new CreateNotificationChannels());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceAllAppPkg(args);
        return super.invoke(proxy, method, args);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    public static class CancelNotificationWithTag extends MethodHook {

        @Override
        protected String getMethodName() {
            return "cancelNotificationWithTag";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    public static class CreateNotificationChannels extends MethodHook {

        @Override
        protected String getMethodName() {
            return "createNotificationChannels";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }
}
