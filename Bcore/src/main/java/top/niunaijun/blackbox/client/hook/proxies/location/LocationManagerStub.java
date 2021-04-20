package top.niunaijun.blackbox.client.hook.proxies.location;

import android.content.Context;

import java.lang.reflect.Method;

import mirror.android.location.ILocationManager;
import mirror.android.os.ServiceManager;
import top.niunaijun.blackbox.client.hook.BinderInvocationStub;
import top.niunaijun.blackbox.client.hook.MethodHook;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Created by Milk on 4/8/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class LocationManagerStub extends BinderInvocationStub {

    public LocationManagerStub() {
        super(ServiceManager.getService.call(Context.LOCATION_SERVICE));
    }

    @Override
    protected Object getWho() {
        return ILocationManager.Stub.asInterface.call(ServiceManager.getService.call(Context.LOCATION_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new RegisterGnssStatusCallback());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceLastAppPkg(args);
        return super.invoke(proxy, method, args);
    }

    static class RegisterGnssStatusCallback extends MethodHook {

        @Override
        protected String getMethodName() {
            return "registerGnssStatusCallback";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }
}
