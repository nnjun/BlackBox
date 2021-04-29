package top.niunaijun.blackbox.server;

import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.ConditionVariable;

import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;
import android.text.TextUtils;

import java.util.Arrays;

import top.niunaijun.blackbox.entity.ClientConfig;
import top.niunaijun.blackbox.client.IBClient;
import top.niunaijun.blackbox.client.StubManifest;

public class ProcessRecord extends Binder implements Parcelable {
    public final ApplicationInfo info;
    final public String processName;
    public IBClient client;
    public IInterface appThread;
    public int pid;
    public int uid;
    public int vuid;
    public int vpid;
    public int callingVUid;
    public int userId;
    public int baseVUid;

    public ConditionVariable initLock = new ConditionVariable();

    public ProcessRecord(ApplicationInfo info, String processName, int vuid, int vpid, int callingVUid) {
        this.info = info;
        this.vuid = vuid;
        this.vpid = vpid;
        this.userId = 0;
        this.callingVUid = callingVUid;
        this.processName = processName;
    }

    public int getCallingVUid() {
        return callingVUid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessRecord that = (ProcessRecord) o;
        return pid == that.pid &&
                vuid == that.vuid &&
                vpid == that.vpid &&
                uid == that.uid &&
                userId == that.userId &&
                baseVUid == that.baseVUid &&
                TextUtils.equals(processName, that.processName);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{processName, pid, vuid, vpid, uid, pid, userId});
    }

    public String getProviderAuthority() {
        return StubManifest.getStubAuthorities(vpid);
    }

    public ClientConfig getClientConfig() {
        ClientConfig config = new ClientConfig();
        config.packageName = info.packageName;
        config.processName = processName;
        config.vpid = vpid;
        config.vuid = vuid;
        config.uid = uid;
        config.userId = userId;
        config.token = this;
        config.baseVUid = baseVUid;
        return config;
    }

    public void kill() {
        if (pid > 0) {
//            VActivityManagerService.get().beforeProcessKilled(this);
            try {
                Process.killProcess(pid);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public String getPackageName() {
        return info.packageName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.info, flags);
        dest.writeString(this.processName);
        dest.writeInt(this.pid);
        dest.writeInt(this.vuid);
        dest.writeInt(this.vpid);
        dest.writeInt(this.uid);
        dest.writeInt(this.callingVUid);
        dest.writeInt(this.userId);
        dest.writeInt(this.baseVUid);
    }

    protected ProcessRecord(Parcel in) {
        this.info = in.readParcelable(ApplicationInfo.class.getClassLoader());
        this.processName = in.readString();
        this.pid = in.readInt();
        this.vuid = in.readInt();
        this.vpid = in.readInt();
        this.uid = in.readInt();
        this.callingVUid = in.readInt();
        this.userId = in.readInt();
        this.baseVUid = in.readInt();
    }

    public static final Creator<ProcessRecord> CREATOR = new Creator<ProcessRecord>() {
        @Override
        public ProcessRecord createFromParcel(Parcel source) {
            return new ProcessRecord(source);
        }

        @Override
        public ProcessRecord[] newArray(int size) {
            return new ProcessRecord[size];
        }
    };
}
