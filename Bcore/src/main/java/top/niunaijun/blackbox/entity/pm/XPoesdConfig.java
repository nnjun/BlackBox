package top.niunaijun.blackbox.entity.pm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Milk on 5/2/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class XPoesdConfig implements Parcelable {
    public boolean enable;
    public Map<String, Boolean> moduleState = new HashMap<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.enable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.moduleState.size());
        for (Map.Entry<String, Boolean> entry : this.moduleState.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
    }

    public XPoesdConfig() {
    }

    public XPoesdConfig(Parcel in) {
        this.enable = in.readByte() != 0;
        int mModuleStateSize = in.readInt();
        this.moduleState = new HashMap<String, Boolean>(mModuleStateSize);
        for (int i = 0; i < mModuleStateSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.moduleState.put(key, value);
        }
    }

    public static final Parcelable.Creator<XPoesdConfig> CREATOR = new Parcelable.Creator<XPoesdConfig>() {
        @Override
        public XPoesdConfig createFromParcel(Parcel source) {
            return new XPoesdConfig(source);
        }

        @Override
        public XPoesdConfig[] newArray(int size) {
            return new XPoesdConfig[size];
        }
    };
}
