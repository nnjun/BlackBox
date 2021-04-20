package top.niunaijun.blackbox.client.hook.proxies.am;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;

import java.lang.reflect.Method;

import mirror.android.app.IActivityTaskManager;
import mirror.android.os.ServiceManager;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.BClient;
import top.niunaijun.blackbox.client.hook.BinderInvocationStub;
import top.niunaijun.blackbox.client.hook.MethodHook;
import top.niunaijun.blackbox.client.hook.provider.FileProviderHandler;
import top.niunaijun.blackbox.utils.ComponentUtils;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.utils.compat.StartActivityCompat;

import static android.content.pm.PackageManager.GET_META_DATA;

/**
 * Created by Milk on 3/31/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ActivityTaskManagerStub extends BinderInvocationStub {
    public static final String TAG = "ActivityTaskManager";

    public ActivityTaskManagerStub() {
        super(ServiceManager.getService.call("activity_task"));
    }

    @Override
    protected Object getWho() {
        return IActivityTaskManager.Stub.asInterface.call(ServiceManager.getService.call("activity_task"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("activity_task");
    }

    @Override
    protected void onBindMethod() {
        addMethodHook(new CommonStub.StartActivity());
        addMethodHook(new CommonStub.StartActivities());
        addMethodHook(new CommonStub.ActivityDestroyed());
        addMethodHook(new CommonStub.ActivityResumed());
        addMethodHook(new CommonStub.FinishActivity());
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

}
