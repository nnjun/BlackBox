package top.niunaijun.blackbox.client.hook.proxies.os;


import java.lang.reflect.Method;

import mirror.android.os.IDeviceIdentifiersPolicyService;
import mirror.android.os.ServiceManager;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.hook.BinderInvocationStub;
import top.niunaijun.blackbox.client.hook.MethodHook;

/**
 * Created by Milk on 4/3/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class DeviceIdentifiersPolicyStub extends BinderInvocationStub {

    public DeviceIdentifiersPolicyStub() {
        super(ServiceManager.getService.call("device_identifiers"));
    }

    @Override
    protected Object getWho() {
        return IDeviceIdentifiersPolicyService.Stub.asInterface.call(ServiceManager.getService.call("device_identifiers"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("device_identifiers");
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new MethodHook() {
            @Override
            protected String getMethodName() {
                return "getSerialForPackage";
            }

            @Override
            protected Object hook(Object who, Method method, Object[] args) throws Throwable {
                args[0] = BlackBoxCore.getHostPkg();
                return method.invoke(who, args);
            }
        });
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
