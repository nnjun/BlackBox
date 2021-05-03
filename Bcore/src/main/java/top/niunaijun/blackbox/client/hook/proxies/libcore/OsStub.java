package top.niunaijun.blackbox.client.hook.proxies.libcore;

import android.util.Log;

import java.lang.reflect.Method;

import mirror.libcore.io.Libcore;
import top.niunaijun.blackbox.client.BClient;
import top.niunaijun.blackbox.client.hook.ClassInvocationStub;
import top.niunaijun.blackbox.client.hook.IOManager;
import top.niunaijun.blackbox.client.hook.MethodHook;
import top.niunaijun.blackbox.utils.compat.ObjectsCompat;

/**
 * Created by Milk on 4/9/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class OsStub extends ClassInvocationStub {
    public static final String TAG = "OsStub";
    private Object mBase;

    public OsStub() {
        mBase = Libcore.os.get();
    }

    @Override
    protected Object getWho() {
        return mBase;
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        Libcore.os.set(proxyInvocation);
    }

    @Override
    protected void onBindMethod() {
        addMethodHook(new Getuid());
    }

    @Override
    public boolean isBadEnv() {
        return Libcore.os.get() != getProxyInvocation();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null)
                    continue;
                if (args[i] instanceof String && ((String) args[i]).startsWith("/")) {
                    String orig = (String) args[i];
                    args[i] = IOManager.get().redirectPath(orig);
//                    if (!ObjectsCompat.equals(orig, args[i])) {
//                        Log.d(TAG, "redirectPath: " + orig + "  => " + args[i]);
//                    }
                }
            }
        }
        return super.invoke(proxy, method, args);
    }

    static class Getuid extends MethodHook {
        @Override
        protected String getMethodName() {
            return "getuid";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int uid = (int) method.invoke(who, args);
//            if (!BClient.getClient().isInit()) {
//                return uid;
//            }
//            return BClient.getBaseVUid();
            return uid;
        }
    }

    static class Access extends MethodHook {
        @Override
        protected String getMethodName() {
            return "access";
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String path = (String) args[0];
            Log.d(TAG, "access: " + path);
            try {
                return method.invoke(who, args);
            } catch (Throwable e) {
                throw e.getCause();
            }
        }
    }
}
