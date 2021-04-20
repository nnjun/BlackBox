package top.niunaijun.blackbox.server.am;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;

import java.util.List;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.ClientConfig;
import top.niunaijun.blackbox.server.pm.BPackageManagerService;
import top.niunaijun.blackbox.client.record.service.UnbindRecord;
import top.niunaijun.blackbox.server.ProcessRecord;
import top.niunaijun.blackbox.server.BProcessManager;

import static android.content.pm.PackageManager.GET_META_DATA;

/**
 * Created by Milk on 3/31/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class BActivityManagerService extends IBActivityManagerService.Stub {
    public static final String TAG = "VActivityManagerService";
    private static BActivityManagerService sService = new BActivityManagerService();

    private final ActiveServices mActiveServices = new ActiveServices();
    private final ActivityStack mStack = new ActivityStack();

    public static BActivityManagerService get() {
        return sService;
    }

    @Override
    public ComponentName startService(Intent intent, String resolvedType, int userId) {
        synchronized (mActiveServices) {
            mActiveServices.startService(intent, resolvedType, userId);
        }
        return null;
    }

    @Override
    public IBinder acquireContentProviderClient(ProviderInfo providerInfo) throws RemoteException {
        ProcessRecord processRecord = BProcessManager.get().startProcessIfNeedLocked(providerInfo.processName, 0, providerInfo.packageName, -1, Process.myUid());
        if (processRecord == null) {
            throw new RuntimeException("Unable to create process " + providerInfo.name);
        }
        return processRecord.client.acquireContentProviderClient(providerInfo);
    }

    @Override
    public Intent sendBroadcast(Intent intent, String resolvedType, int userId) throws RemoteException {
        List<ResolveInfo> resolves = BPackageManagerService.get().queryBroadcastReceivers(intent, GET_META_DATA, resolvedType, userId);

        for (ResolveInfo resolve : resolves) {
            ProcessRecord processRecord = BProcessManager.get().startProcessIfNeedLocked(resolve.activityInfo.processName, userId, resolve.activityInfo.packageName, -1, Process.myUid());
            if (processRecord == null) {
//                throw new RuntimeException("Unable to create process " + resolve.activityInfo.name);
                continue;
            }
        }

        if (intent.getPackage() != null) {
            intent.setPackage(BlackBoxCore.getHostPkg());
        }
        if (intent.getComponent() != null) {
            intent.setComponent(null);
//            Intent shadow = new Intent();
//            shadow.setPackage(VirtualCore.getHostPkg());
//            shadow.setAction(StubManifest.getStubReceiver());
//            StubBroadcastRecord.saveStub(shadow, intent, receivers, userId);
        }
        return intent;
    }

    @Override
    public IBinder peekService(Intent intent, String resolvedType, int userId) throws RemoteException {
        synchronized (mActiveServices) {
            return mActiveServices.peekService(intent, resolvedType, userId);
        }
    }

    @Override
    public void onActivityCreated(int taskId, IBinder token, IBinder activityRecord) throws RemoteException {
        synchronized (mStack) {
            int callingPid = Binder.getCallingPid();
            ProcessRecord process = BProcessManager.get().findProcessByPid(callingPid);
            if (process == null) {
                return;
            }
            ActivityRecord record = (ActivityRecord) activityRecord;
            mStack.onActivityCreated(process, taskId, token, record);
        }
    }

    @Override
    public void onActivityResumed(IBinder token) throws RemoteException {
        synchronized (mStack) {
            int callingPid = Binder.getCallingPid();
            ProcessRecord process = BProcessManager.get().findProcessByPid(callingPid);
            if (process == null) {
                return;
            }
            mStack.onActivityResumed(process.userId, token);
        }
    }

    @Override
    public void onActivityDestroyed(IBinder token) throws RemoteException {
        synchronized (mStack) {
            int callingPid = Binder.getCallingPid();
            ProcessRecord process = BProcessManager.get().findProcessByPid(callingPid);
            if (process == null) {
                return;
            }
            mStack.onActivityDestroyed(process.userId, token);
        }
    }

    @Override
    public void onFinishActivity(IBinder token) throws RemoteException {
        synchronized (mStack) {
            int callingPid = Binder.getCallingPid();
            ProcessRecord process = BProcessManager.get().findProcessByPid(callingPid);
            if (process == null) {
                return;
            }
            mStack.onFinishActivity(process.userId, token);
        }
    }

    @Override
    public void onStartCommand(Intent intent, int userId) throws RemoteException {
        synchronized (mActiveServices) {
            mActiveServices.onStartCommand(intent, userId);
        }
    }

    @Override
    public UnbindRecord onServiceUnbind(Intent proxyIntent, int userId) throws RemoteException {
        synchronized (mActiveServices) {
            return mActiveServices.onServiceUnbind(proxyIntent, userId);
        }
    }

    @Override
    public void onServiceDestroy(Intent proxyIntent, int userId) throws RemoteException {
        synchronized (mActiveServices) {
            mActiveServices.onServiceDestroy(proxyIntent, userId);
        }
    }

    @Override
    public int stopService(Intent intent, String resolvedType, int userId) {
        synchronized (mActiveServices) {
            return mActiveServices.stopService(intent, resolvedType, userId);
        }
    }

    @Override
    public Intent bindService(Intent service, IBinder binder, String resolvedType, int userId) throws RemoteException {
        synchronized (mActiveServices) {
            return mActiveServices.bindService(service, binder, resolvedType, userId);
        }
    }

    @Override
    public void unbindService(IBinder binder, int userId) throws RemoteException {
        synchronized (mActiveServices) {
            mActiveServices.unbindService(binder, userId);
        }
    }

    @Override
    public ClientConfig initProcess(String packageName, String processName, int userId) throws RemoteException {
        ProcessRecord processRecord = BProcessManager.get().startProcessIfNeedLocked(processName, userId, packageName, -1, Process.myUid());
        if (processRecord == null)
            return null;
        return processRecord.getClientConfig();
    }

    @Override
    public void startActivity(Intent intent, int userId) {
        synchronized (mStack) {
            mStack.startActivityLocked(userId, intent, null, null, null, -1, -1, null);
        }
    }

    @Override
    public int startActivityAms(int userId, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, Bundle options) throws RemoteException {
        synchronized (mStack) {
            return mStack.startActivityLocked(userId, intent, resolvedType, resultTo, resultWho, requestCode, flags, options);
        }
    }

    @Override
    public int startActivities(int userId, Intent[] intent, String[] resolvedType, IBinder resultTo, Bundle options) throws RemoteException {
        synchronized (mStack) {
            return mStack.startActivitiesLocked(userId, intent, resolvedType, resultTo, options);
        }
    }
}
