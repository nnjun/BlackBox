package top.niunaijun.blackbox.client.hook.proxies.pm;

import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Method;

import mirror.android.content.pm.IShortcutService;
import mirror.android.os.ServiceManager;
import top.niunaijun.blackbox.client.hook.BinderInvocationStub;
import top.niunaijun.blackbox.client.hook.ResultStaticMethodProxy;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Created by Milk on 4/5/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 * 未实现，全部拦截
 */
public class ShortcutManagerStub extends BinderInvocationStub {

    public ShortcutManagerStub() {
        super(ServiceManager.getService.call(Context.SHORTCUT_SERVICE));
    }

    @Override
    protected Object getWho() {
        return IShortcutService.Stub.asInterface.call(ServiceManager.getService.call(Context.SHORTCUT_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.SHORTCUT_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ResultStaticMethodProxy("requestPinShortcut", true));
        addMethodHook(new ResultStaticMethodProxy("setDynamicShortcuts", true));
        addMethodHook(new ResultStaticMethodProxy("addDynamicShortcuts", true));
        addMethodHook(new ResultStaticMethodProxy("createShortcutResultIntent", new Intent()));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceAllAppPkg(args);
        return super.invoke(proxy, method, args);
    }
}
