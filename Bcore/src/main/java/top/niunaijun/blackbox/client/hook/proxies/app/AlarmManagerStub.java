package top.niunaijun.blackbox.client.hook.proxies.app;

import android.content.Context;

import java.lang.reflect.Method;

import mirror.android.app.IAlarmManager;
import mirror.android.os.ServiceManager;
import top.niunaijun.blackbox.client.hook.BinderInvocationStub;
import top.niunaijun.blackbox.client.hook.MethodHook;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Created by Milk on 4/3/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class AlarmManagerStub extends BinderInvocationStub {

    public AlarmManagerStub() {
        super(ServiceManager.getService.call(Context.ALARM_SERVICE));
    }

    @Override
    protected Object getWho() {
        return IAlarmManager.Stub.asInterface.call(ServiceManager.getService.call(Context.ALARM_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.ALARM_SERVICE);
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new MethodHook() {
            @Override
            protected String getMethodName() {
                return "set";
            }

            @Override
            protected Object hook(Object who, Method method, Object[] args) throws Throwable {
                MethodParameterUtils.replaceFirstAppPkg(args);
                return method.invoke(who, args);
            }
        });
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
