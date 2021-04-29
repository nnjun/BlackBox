package top.niunaijun.blackbox.client.hook;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import top.niunaijun.blackbox.utils.MethodParameterUtils;
import top.niunaijun.blackbox.utils.Slog;

/**
 * Created by Milk on 3/30/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public abstract class ClassInvocationStub implements InvocationHandler, IInjectHook {
    public static final String TAG = ClassInvocationStub.class.getSimpleName();

    private Map<String, MethodHook> mMethodHookMap = new HashMap<>();
    private Object mBase;
    private Object mProxyInvocation;

    protected abstract Object getWho();

    protected abstract void inject(Object baseInvocation, Object proxyInvocation);

    protected abstract void onBindMethod();

    protected Object getProxyInvocation() {
        return mProxyInvocation;
    }

    protected Object getBase() {
        return mBase;
    }

    @Override
    public void injectHook() {
        mBase = getWho();
        mProxyInvocation = Proxy.newProxyInstance(mBase.getClass().getClassLoader(), MethodParameterUtils.getAllInterface(mBase.getClass()), this);
        inject(mBase, mProxyInvocation);

        onBindMethod();
    }

    protected void addMethodHook(MethodHook methodHook) {
        mMethodHookMap.put(methodHook.getMethodName(), methodHook);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodHook methodHook = mMethodHookMap.get(method.getName());
        if (methodHook == null || !method.getName().equals(methodHook.getMethodName())) {
            try {
                log(method, args, null, "beforeHook");
                Object invoke = method.invoke(mBase, args);
                log(method, args, invoke, "afterHook");
                return invoke;
            } catch (Throwable e) {
                throw e.getCause();
            }
        }

        log(method, args, null, "beforeHook");
        Object result = methodHook.beforeHook(mBase, method, args);
        if (result != null) {
            return result;
        }
        result = methodHook.hook(mBase, method, args);
        result = methodHook.afterHook(result);
        log(method, args, result, "afterHook");
        return result;
    }

    private void log(Method method, Object[] args, Object result, String event) {
//        String argStr;
//        // Arguments to string is done before the method is called because the method might actually change it
//        try {
//            argStr = Arrays.toString(args);
//            argStr = argStr.substring(1, argStr.length() - 1);
//        } catch (Throwable e) {
//            argStr = "" + e.getMessage();
//        }
//
//        String retString;
//        if (method.getReturnType().equals(void.class)) {
//            retString = "void";
//        } else {
//            retString = String.valueOf(result);
//        }
//        if (enableLog()) {
//            Slog.d(TAG, event + "  " + method.getDeclaringClass().getSimpleName() + "." + method.getName() + "(" + argStr + ") => " + retString);
//        }
    }

    protected boolean enableLog() {
        return false;
    }
}
