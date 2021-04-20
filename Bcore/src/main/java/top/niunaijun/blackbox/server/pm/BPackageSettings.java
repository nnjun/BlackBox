package top.niunaijun.blackbox.server.pm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Milk on 4/21/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class BPackageSettings implements Parcelable {
    public HashMap<String, BPackage> mPackages = new HashMap<>();

    public BPackageSettings() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPackages.size());
        for (Map.Entry<String, BPackage> entry : this.mPackages.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
    }

    protected BPackageSettings(Parcel in) {
        int mPackagesSize = in.readInt();
        this.mPackages = new HashMap<String, BPackage>(mPackagesSize);
        for (int i = 0; i < mPackagesSize; i++) {
            String key = in.readString();
            BPackage value = in.readParcelable(BPackage.class.getClassLoader());
            this.mPackages.put(key, value);
        }
    }

    public static final Creator<BPackageSettings> CREATOR = new Creator<BPackageSettings>() {
        @Override
        public BPackageSettings createFromParcel(Parcel source) {
            return new BPackageSettings(source);
        }

        @Override
        public BPackageSettings[] newArray(int size) {
            return new BPackageSettings[size];
        }
    };
}
