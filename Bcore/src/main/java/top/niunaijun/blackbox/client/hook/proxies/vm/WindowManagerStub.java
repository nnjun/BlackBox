package top.niunaijun.blackbox.client.hook.proxies.vm;

import android.os.IInterface;

import java.lang.reflect.Method;

import mirror.android.os.ServiceManager;
import mirror.android.view.IWindowManager;
import mirror.android.view.WindowManagerGlobal;
import top.niunaijun.blackbox.client.hook.BinderInvocationStub;
import top.niunaijun.blackbox.client.hook.MethodHook;

/**
 * Created by Milk on 4/6/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class WindowManagerStub extends BinderInvocationStub {
    public static final String TAG = "WindowManagerStub";

    public WindowManagerStub() {
        super(ServiceManager.getService.call("window"));
    }

    @Override
    protected Object getWho() {
        return IWindowManager.Stub.asInterface.call(ServiceManager.getService.call("window"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("window");
        WindowManagerGlobal.sWindowManagerService.set(null);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new OpenSession());
    }

    static class OpenSession extends MethodHook {
        @Override
        protected String getMethodName() {
            return "openSession";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IInterface session = (IInterface) method.invoke(who, args);
            WindowSessionStub windowSessionStub = new WindowSessionStub(session);
            windowSessionStub.injectHook();
            return windowSessionStub.getProxyInvocation();
        }
    }
}
