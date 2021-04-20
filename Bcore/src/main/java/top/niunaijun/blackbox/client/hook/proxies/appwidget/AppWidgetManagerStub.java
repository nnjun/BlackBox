package top.niunaijun.blackbox.client.hook.proxies.appwidget;

import android.content.Context;

import java.lang.reflect.Method;

import mirror.android.os.ServiceManager;
import mirror.com.android.internal.appwidget.IAppWidgetService;
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
 */
public class AppWidgetManagerStub extends BinderInvocationStub {

    public AppWidgetManagerStub() {
        super(ServiceManager.getService.call(Context.APPWIDGET_SERVICE));
    }

    @Override
    protected Object getWho() {
        return IAppWidgetService.Stub.asInterface.call(ServiceManager.getService.call(Context.APPWIDGET_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.APPWIDGET_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceAllAppPkg(args);
        return super.invoke(proxy, method, args);
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ResultStaticMethodProxy("startListening", new int[0]));
        addMethodHook(new ResultStaticMethodProxy("stopListening", 0));
        addMethodHook(new ResultStaticMethodProxy("allocateAppWidgetId", 0));
        addMethodHook(new ResultStaticMethodProxy("deleteAppWidgetId", 0));
        addMethodHook(new ResultStaticMethodProxy("deleteHost", 0));
        addMethodHook(new ResultStaticMethodProxy("deleteAllHosts", 0));
        addMethodHook(new ResultStaticMethodProxy("getAppWidgetViews", null));
        addMethodHook(new ResultStaticMethodProxy("getAppWidgetIdsForHost", null));
        addMethodHook(new ResultStaticMethodProxy("createAppWidgetConfigIntentSender", null));
        addMethodHook(new ResultStaticMethodProxy("updateAppWidgetIds", 0));
        addMethodHook(new ResultStaticMethodProxy("updateAppWidgetOptions", 0));
        addMethodHook(new ResultStaticMethodProxy("getAppWidgetOptions", null));
        addMethodHook(new ResultStaticMethodProxy("partiallyUpdateAppWidgetIds", 0));
        addMethodHook(new ResultStaticMethodProxy("updateAppWidgetProvider", 0));
        addMethodHook(new ResultStaticMethodProxy("notifyAppWidgetViewDataChanged", 0));
        addMethodHook(new ResultStaticMethodProxy("getInstalledProvidersForProfile", null));
        addMethodHook(new ResultStaticMethodProxy("getAppWidgetInfo", null));
        addMethodHook(new ResultStaticMethodProxy("hasBindAppWidgetPermission", false));
        addMethodHook(new ResultStaticMethodProxy("setBindAppWidgetPermission", 0));
        addMethodHook(new ResultStaticMethodProxy("bindAppWidgetId", false));
        addMethodHook(new ResultStaticMethodProxy("bindRemoteViewsService", 0));
        addMethodHook(new ResultStaticMethodProxy("unbindRemoteViewsService", 0));
        addMethodHook(new ResultStaticMethodProxy("getAppWidgetIds", new int[0]));
        addMethodHook(new ResultStaticMethodProxy("isBoundWidgetPackage", false));
    }
}
