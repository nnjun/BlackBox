package top.niunaijun.blackbox.server;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.ClientConfig;
import top.niunaijun.blackbox.client.StubManifest;
import top.niunaijun.blackbox.server.pm.BPackageManagerService;
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

    private Map<String, ProcessRecord> mProcessMap = new HashMap<>();
    private List<ProcessRecord> mPidsSelfLocked = new ArrayList<>();

    private final Object mProcessLock = new Object();
    private AtomicInteger mProcess = new AtomicInteger(0);

    public static BProcessManager get() {
        return sVProcessManager;
    }

    public ProcessRecord startProcessIfNeedLocked(String processName, int userId, String packageName, int vpid, int callingUid, int callingPid) {
        runProcessGC();
        ApplicationInfo info = BPackageManagerService.get().getApplicationInfo(packageName, 0, userId);
        ProcessRecord app = null;
        synchronized (mProcessLock) {
            if (vpid == -1) {
                app = mProcessMap.get(processName);
                if (app != null) {
                    if (app.initLock != null) {
                        app.initLock.block();
                    }
                    if (app.client != null) {
                        return app;
                    }
                }
            }
            if (vpid == -1) {
                // next index
                // todo process
                vpid = mProcess.getAndIncrement();
            }
//            if (app != null) {
//                Slog.w(TAG, "remove invalid process record: " + app.processName);
//                mProcessMap.remove(app.processName);
//                mPidsSelfLocked.remove(app);
//            }
            app = new ProcessRecord(info, processName, 0, vpid, callingUid);
            app.uid = info.uid;
            mProcessMap.put(app.processName, app);
            mPidsSelfLocked.add(app);
            if (!initProcess(app)) {
                //init process fail
                mProcessMap.remove(app.processName);
                mPidsSelfLocked.remove(app);
                app = null;
            } else {
                app.pid = getPid(BlackBoxCore.getContext(), StubManifest.getProcessName(app.vpid));
            }
        }
        return app;
    }

    public void restartProcess(String packageName, String processName, int userId) {
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        synchronized (this) {
            ProcessRecord app;
            synchronized (mProcessLock) {
                app = findProcessByPid(callingPid);
            }
            if (app == null) {
                String stubProcessName = getProcessName(BlackBoxCore.getContext(), callingPid);
                int vpid = parseVPid(stubProcessName);
                startProcessIfNeedLocked(processName, userId, packageName, vpid, callingUid, callingPid);
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

    private boolean initProcess(ProcessRecord record) {
        Log.d(TAG, "initProcess: " + record.processName);
        ClientConfig clientConfig = record.getClientConfig();
        Bundle bundle = new Bundle();
        bundle.putParcelable("_VM_|_client_config_", clientConfig);
        Bundle init = ProviderCall.callSafely(record.getProviderAuthority(), "_VM_|_init_process_", null, bundle);
        IBinder client = BundleCompat.getBinder(init, "_VM_|_client_");
        if (client == null || !client.isBinderAlive()) {
            return false;
        }
        attachClient(record, client);
        return true;
    }

    private void attachClient(final ProcessRecord app, final IBinder client) {
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
        record.kill();
        mProcessMap.remove(record.processName);
        mPidsSelfLocked.remove(record);
    }

    public ProcessRecord findProcessRecord(String processName) {
        return mProcessMap.get(processName);
    }

    public ProcessRecord findProcessByPid(int pid) {
        for (ProcessRecord processRecord : mPidsSelfLocked) {
            if (processRecord.pid == pid)
                return processRecord;
        }
        return null;
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
