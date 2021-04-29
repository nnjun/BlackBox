package top.niunaijun.blackbox.server;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.entity.ClientConfig;
import top.niunaijun.blackbox.client.StubManifest;
import top.niunaijun.blackbox.server.pm.BPackageManagerService;
import top.niunaijun.blackbox.server.user.BUserHandle;
import top.niunaijun.blackbox.utils.Slog;
import top.niunaijun.blackbox.utils.compat.ApplicationThreadCompat;
import top.niunaijun.blackbox.utils.compat.BundleCompat;
import top.niunaijun.blackbox.utils.provider.ProviderCall;
import top.niunaijun.blackbox.client.IBClient;

/**
 * Created by Milk on 4/2/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class BProcessManager {
    public static final String TAG = "VProcessManager";

    public static BProcessManager sVProcessManager = new BProcessManager();
    private Map<Integer, Map<String, ProcessRecord>> mProcessMap = new HashMap<>();
    //    private Map<String, ProcessRecord> mProcessMap = new HashMap<>();
    private final List<ProcessRecord> mPidsSelfLocked = new ArrayList<>();

    private final Object mProcessLock = new Object();
    private AtomicInteger mProcess = new AtomicInteger(0);

    public static BProcessManager get() {
        return sVProcessManager;
    }

    public ProcessRecord startProcessIfNeedLocked(String packageName, String processName, int userId, int vpid, int callingUid, int callingPid) {
        runProcessGC();
        ApplicationInfo info = BPackageManagerService.get().getApplicationInfo(packageName, 0, userId);
        if (info == null)
            return null;
        ProcessRecord app;
        int vuid = BUserHandle.getUid(userId, BPackageManagerService.get().getAppId(packageName));
        Map<String, ProcessRecord> vProcess = mProcessMap.get(vuid);

        if (vProcess == null) {
            vProcess = new HashMap<>();
        }
        synchronized (mProcessLock) {
            if (vpid == -1) {
                app = vProcess.get(processName);
                if (app != null) {
                    if (app.initLock != null) {
                        app.initLock.block();
                    }
                    if (app.client != null) {
                        return app;
                    }
                }
                vpid = getUsingVPidL();
                Slog.d(TAG, "init vUid = " + vuid + ", vPid = " + vpid);
            }
            if (vpid == -1) {
                throw new RuntimeException("No processes available");
            }
//            if (app != null) {
//                Slog.w(TAG, "remove invalid process record: " + app.processName);
//                mProcessMap.remove(app.processName);
//                mPidsSelfLocked.remove(app);
//            }
            app = new ProcessRecord(info, processName, 0, vpid, callingUid);
            app.uid = vuid;
            app.vuid = vuid;
            app.userId = userId;
            app.baseVUid = BUserHandle.getAppId(info.uid);

            vProcess.put(processName, app);
            mPidsSelfLocked.add(app);

            mProcessMap.put(app.vuid, vProcess);
            if (!initProcessL(app)) {
                //init process fail
                vProcess.remove(processName);
                mPidsSelfLocked.remove(app);
                app = null;
            } else {
                app.pid = getPid(BlackBoxCore.getContext(), StubManifest.getProcessName(app.vpid));
            }
        }
        return app;
    }

    private int getUsingVPidL() {
        ActivityManager manager = (ActivityManager) BlackBoxCore.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();
        for (int i = 0; i < StubManifest.FREE_COUNT; i++) {
            boolean using = false;
            for (ProcessRecord processRecord : mPidsSelfLocked) {
                if (processRecord.vpid == i) {
                    using = true;
                    break;
                }
            }
            if (using)
                continue;
            return i;
        }
        return -1;
    }

    public void restartProcess(String packageName, String processName, int userId) {
        synchronized (mProcessLock) {
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            ProcessRecord app;
            synchronized (mProcessLock) {
                app = findProcessByPid(callingPid);
            }
            if (app == null) {
                String stubProcessName = getProcessName(BlackBoxCore.getContext(), callingPid);
                int vpid = parseVPid(stubProcessName);
                startProcessIfNeedLocked(packageName, processName, userId, vpid, callingUid, callingPid);
            }
        }
    }

    private int parseVPid(String stubProcessName) {
        String prefix;
        if (stubProcessName == null) {
            return -1;
        } else {
            prefix = BlackBoxCore.getHostPkg() + ":p";
        }
        if (stubProcessName.startsWith(prefix)) {
            try {
                return Integer.parseInt(stubProcessName.substring(prefix.length()));
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return -1;
    }

    private void runProcessGC() {
        // todo
    }

    private boolean initProcessL(ProcessRecord record) {
        Log.d(TAG, "initProcess: " + record.processName);
        ClientConfig clientConfig = record.getClientConfig();
        Bundle bundle = new Bundle();
        bundle.putParcelable("_VM_|_client_config_", clientConfig);
        Bundle init = ProviderCall.callSafely(record.getProviderAuthority(), "_VM_|_init_process_", null, bundle);
        IBinder client = BundleCompat.getBinder(init, "_VM_|_client_");
        if (client == null || !client.isBinderAlive()) {
            return false;
        }
        attachClientL(record, client);
        return true;
    }

    private void attachClientL(final ProcessRecord app, final IBinder client) {
        IBClient ivClient = IBClient.Stub.asInterface(client);
        if (ivClient == null) {
            app.kill();
            return;
        }
        try {
            client.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    Log.d(TAG, "Client Died: " + app.processName);
                    client.unlinkToDeath(this, 0);
                    onProcessDie(app);
                }
            }, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        app.client = ivClient;
        try {
            app.appThread = ApplicationThreadCompat.asInterface(ivClient.getActivityThread());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        app.initLock.open();
    }

    public void onProcessDie(ProcessRecord record) {
        synchronized (mProcessLock) {
            record.kill();
            Map<String, ProcessRecord> remove = mProcessMap.remove(record.vuid);
            if (remove != null)
                remove.remove(record.processName);
            mPidsSelfLocked.remove(record);
        }
    }

    public ProcessRecord findProcessRecord(String packageName, String processName, int userId) {
        synchronized (mProcessLock) {
            int appId = BPackageManagerService.get().getAppId(packageName);
            int vuid = BUserHandle.getUid(userId, appId);
            Map<String, ProcessRecord> processRecordMap = mProcessMap.get(vuid);
            if (processRecordMap == null)
                return null;
            return processRecordMap.get(processName);
        }
    }

    public void killAllByPackageName(String packageName) {
        synchronized (mProcessLock) {
            synchronized (mPidsSelfLocked) {
                List<ProcessRecord> tmp = new ArrayList<>(mPidsSelfLocked);
                int appId = BPackageManagerService.get().getAppId(packageName);
                for (ProcessRecord processRecord : mPidsSelfLocked) {
                    int appId1 = BUserHandle.getAppId(processRecord.vuid);
                    if (appId == appId1) {
                        mProcessMap.remove(processRecord.vuid);
                        tmp.remove(processRecord);
                        processRecord.kill();
                    }
                }
                mPidsSelfLocked.clear();
                mPidsSelfLocked.addAll(tmp);
            }
        }
    }

    public void killPackageAsUser(String packageName, int userId) {
        synchronized (mProcessLock) {
            int vuid = BUserHandle.getUid(userId, BPackageManagerService.get().getAppId(packageName));
            Map<String, ProcessRecord> process = mProcessMap.get(vuid);
            if (process == null)
                return;
            for (ProcessRecord value : process.values()) {
                value.kill();
            }
            mProcessMap.remove(vuid);
        }
    }


    public int getUserIdByCallingPid(int callingPid) {
        synchronized (mProcessLock) {
            ProcessRecord callingProcess = BProcessManager.get().findProcessByPid(callingPid);
            if (callingProcess == null) {
                return 0;
            }
            return callingProcess.userId;
        }
    }

    public ProcessRecord findProcessByPid(int pid) {
        synchronized (mPidsSelfLocked) {
            for (ProcessRecord processRecord : mPidsSelfLocked) {
                if (processRecord.pid == pid)
                    return processRecord;
            }
            return null;
        }
    }

    private static String getProcessName(Context context, int pid) {
        String processName = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
            if (info.pid == pid) {
                processName = info.processName;
                break;
            }
        }
        if (processName == null) {
            throw new RuntimeException("processName = null");
        }
        return processName;
    }

    public static int getPid(Context context, String processName) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo runningAppProcess : runningAppProcesses) {
                if (runningAppProcess.processName.equals(processName)) {
                    return runningAppProcess.pid;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return -1;
    }
}
