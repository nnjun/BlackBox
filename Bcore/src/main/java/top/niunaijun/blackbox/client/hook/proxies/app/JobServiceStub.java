package top.niunaijun.blackbox.client.hook.proxies.app;

import android.app.job.JobInfo;
import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.Method;

import mirror.android.app.job.IJobScheduler;
import mirror.android.os.ServiceManager;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.BClient;
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
public class JobServiceStub extends BinderInvocationStub {
    public static final String TAG = "JobServiceStub";

    public JobServiceStub() {
        super(ServiceManager.getService.call(Context.JOB_SCHEDULER_SERVICE));
    }

    @Override
    protected Object getWho() {
        IBinder jobScheduler = ServiceManager.getService.call("jobscheduler");
        return IJobScheduler.Stub.asInterface.call(jobScheduler);
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new MethodHook() {
            @Override
            protected String getMethodName() {
                return "schedule";
            }

            @Override
            protected Object hook(Object who, Method method, Object[] args) throws Throwable {
                JobInfo jobInfo = (JobInfo) args[0];
                JobInfo proxyJobInfo = BlackBoxCore.getBJobManager()
                        .schedule(jobInfo);
                args[0] = proxyJobInfo;
                return method.invoke(who, args);
            }
        });
        addMethodHook(new MethodHook() {
            @Override
            protected String getMethodName() {
                return "cancel";
            }

            @Override
            protected Object hook(Object who, Method method, Object[] args) throws Throwable {
                args[0] = BlackBoxCore.getBJobManager()
                        .cancel(BClient.getClientConfig().processName, (Integer) args[0]);
                return method.invoke(who, args);
            }
        });
        addMethodHook(new MethodHook() {
            @Override
            protected String getMethodName() {
                return "cancelAll";
            }

            @Override
            protected Object hook(Object who, Method method, Object[] args) throws Throwable {
                BlackBoxCore.getBJobManager().cancelAll(BClient.getClientConfig().processName);
                return method.invoke(who, args);
            }
        });
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
