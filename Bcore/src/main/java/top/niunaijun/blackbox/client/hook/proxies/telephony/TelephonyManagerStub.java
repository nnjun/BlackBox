package top.niunaijun.blackbox.client.hook.proxies.telephony;

import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.Method;

import mirror.android.os.ServiceManager;
import mirror.com.android.internal.telephony.ITelephony;
import top.niunaijun.blackbox.client.hook.BinderInvocationStub;
import top.niunaijun.blackbox.client.hook.MethodHook;

/**
 * Created by Milk on 4/2/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class TelephonyManagerStub extends BinderInvocationStub {

    public TelephonyManagerStub() {
        super(ServiceManager.getService.call(Context.TELEPHONY_SERVICE));
    }

    @Override
    protected Object getWho() {
        IBinder telephony = ServiceManager.getService.call(Context.TELEPHONY_SERVICE);
        return ITelephony.Stub.asInterface.call(telephony);
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new MethodHook() {
            @Override
            protected String getMethodName() {
                return "getDeviceId";
            }

            @Override
            protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//                MethodParameterUtils.replaceFirstAppPkg(args);
//                return method.invoke(who, args);
                return "";
            }
        });

        addMethodHook(new MethodHook() {
            @Override
            protected String getMethodName() {
                return "getImeiForSlot";
            }

            @Override
            protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//                MethodParameterUtils.replaceFirstAppPkg(args);
//                return method.invoke(who, args);
                return "";
            }
        });
        addMethodHook(new IsUserDataEnabled());
        addMethodHook(new IsUserDataEnabled());
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    static class IsUserDataEnabled extends MethodHook {
        @Override
        protected String getMethodName() {
            return "isUserDataEnabled";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return true;
        }
    }

    static class GetSubscriberId extends MethodHook {
        @Override
        protected String getMethodName() {
            return "getSubscriberId";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return true;
        }
    }
}
