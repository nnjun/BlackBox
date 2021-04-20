package top.niunaijun.blackbox.client.hook.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Created by Milk on 3/30/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class CallingPackageStub implements InvocationHandler {
    public static final String TAG = "CallingPackageStub";

    private Object mObject;

    public static Object create(Object orig) {
        return Proxy.newProxyInstance(orig.getClass().getClassLoader(), MethodParameterUtils.getAllInterface(orig.getClass()), new CallingPackageStub(orig));
    }

    public CallingPackageStub(Object object) {
        mObject = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceFirstAppPkg(args);
//        if (args != null) {
//            Log.d(TAG, "========= CallingPackageStub SSSSSS==========");
//            for (Object arg : args) {
//                if (arg == null)
//                    continue;
//                if (arg instanceof String){
//                    Log.d(TAG, "arg: " + arg);
//                }
//            }
//            Log.d(TAG, "========= CallingPackageStub EEEEEEE==========");
//        }
        return method.invoke(mObject, args);
    }
}
