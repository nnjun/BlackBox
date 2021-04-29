package top.niunaijun.blackbox.entity;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Milk on 4/1/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ClientConfig implements Parcelable {
    public String packageName;
    public String processName;
    public int vpid;
    public int vuid;
    public int uid;
    public int userId;
    public int baseVUid;
    public IBinder token;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.packageName);
        dest.writeString(this.processName);
        dest.writeInt(this.vpid);
        dest.writeInt(this.vuid);
        dest.writeInt(this.uid);
        dest.writeInt(this.userId);
        dest.writeInt(this.baseVUid);
        dest.writeStrongBinder(token);
    }

    public ClientConfig() {
    }

    protected ClientConfig(Parcel in) {
        this.packageName = in.readString();
        this.processName = in.readString();
        this.vpid = in.readInt();
        this.vuid = in.readInt();
        this.uid = in.readInt();
        this.userId = in.readInt();
        this.baseVUid = in.readInt();
        this.token = in.readStrongBinder();
    }

    public static final Parcelable.Creator<ClientConfig> CREATOR = new Parcelable.Creator<ClientConfig>() {
        @Override
        public ClientConfig createFromParcel(Parcel source) {
            return new ClientConfig(source);
        }

        @Override
        public ClientConfig[] newArray(int size) {
            return new ClientConfig[size];
        }
    };
}
