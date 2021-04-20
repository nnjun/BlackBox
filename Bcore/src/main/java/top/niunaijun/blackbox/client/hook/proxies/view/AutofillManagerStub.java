package top.niunaijun.blackbox.client.hook.proxies.view;

import android.content.ComponentName;

import java.lang.reflect.Method;

import mirror.android.os.ServiceManager;
import mirror.android.view.IAutoFillManager;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.StubManifest;
import top.niunaijun.blackbox.client.BClient;
import top.niunaijun.blackbox.client.hook.BinderInvocationStub;
import top.niunaijun.blackbox.client.hook.MethodHook;

/**
 * Created by Milk on 4/8/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class AutofillManagerStub extends BinderInvocationStub {
    public static final String TAG = "AutofillManagerStub";

    public AutofillManagerStub() {
        super(ServiceManager.getService.call("autofill"));
    }

    @Override
    protected Object getWho() {
        return IAutoFillManager.Stub.asInterface.call(ServiceManager.getService.call("autofill"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("autofill");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new StartSession());
    }

    static class StartSession extends MethodHook {

        @Override
        protected String getMethodName() {
            return "startSession";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] == null)
                        continue;
                    if (args[i] instanceof ComponentName) {
                        args[i] = new ComponentName(BlackBoxCore.getHostPkg(), StubManifest.getStubActivity(BClient.getVPid()));
                    }
                }
            }
            return method.invoke(who, args);
        }
    }
}
