package top.niunaijun.blackbox.server.pm;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageParser;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Milk on 4/21/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class BPackage implements Parcelable {
    public ArrayList<Activity> activities = new ArrayList<Activity>(0);
    public ArrayList<Activity> receivers = new ArrayList<Activity>(0);
    public ArrayList<Provider> providers = new ArrayList<Provider>(0);
    public ArrayList<Service> services = new ArrayList<Service>(0);
    public ArrayList<Instrumentation> instrumentation = new ArrayList<Instrumentation>(0);
    public ArrayList<Permission> permissions = new ArrayList<Permission>(0);
    public ArrayList<PermissionGroup> permissionGroups = new ArrayList<PermissionGroup>(0);
    public ArrayList<String> requestedPermissions = new ArrayList<String>();
    public Signature[] mSignatures;
    public SigningDetails mSigningDetails;
    public Bundle mAppMetaData;
    public BPackageSettings mExtras;
    public String packageName;
    public int mPreferredOrder;
    public String mSharedUserId;
    public ArrayList<String> usesLibraries;
    public ArrayList<String> usesOptionalLibraries;
    public int mVersionCode;
    public ApplicationInfo applicationInfo;
    public String mVersionName;
    public String baseCodePath;


    public final static class Activity extends Component {
        public ActivityInfo info;

    }

    public static final class Service extends Component {
        public ServiceInfo info;

    }

    public static final class Provider extends Component {
        public ProviderInfo info;
    }

    public static final class Instrumentation extends Component {
        public InstrumentationInfo info;
    }

    public static final class Permission extends Component {
        public PermissionInfo info;
    }

    public static final class PermissionGroup extends Component {
        public PermissionGroupInfo info;
    }

    public class ActivityIntentInfo extends IntentInfo {
        public Activity activity;
    }

    public class ServiceIntentInfo extends IntentInfo {
        public Service service;
    }

    public class ProviderIntentInfo extends IntentInfo {
        public Provider provider;
    }

    public static final class SigningDetails implements Parcelable {
        public Signature[] signatures;

        public static final PackageParser.SigningDetails UNKNOWN = null;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedArray(this.signatures, flags);
        }

        public SigningDetails() {
        }

        protected SigningDetails(Parcel in) {
            this.signatures = in.createTypedArray(Signature.CREATOR);
        }

        public static final Creator<SigningDetails> CREATOR = new Creator<SigningDetails>() {
            @Override
            public SigningDetails createFromParcel(Parcel source) {
                return new SigningDetails(source);
            }

            @Override
            public SigningDetails[] newArray(int size) {
                return new SigningDetails[size];
            }
        };
    }

    public static class IntentInfo implements Parcelable {
        public IntentFilter intentFilter;
        public boolean hasDefault;
        public int labelRes;
        public String nonLocalizedLabel;
        public int icon;
        public int logo;
        public int banner;

        public IntentInfo() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.intentFilter, flags);
            dest.writeByte(this.hasDefault ? (byte) 1 : (byte) 0);
            dest.writeInt(this.labelRes);
            dest.writeString(this.nonLocalizedLabel);
            dest.writeInt(this.icon);
            dest.writeInt(this.logo);
            dest.writeInt(this.banner);
        }

        protected IntentInfo(Parcel in) {
            this.intentFilter = in.readParcelable(IntentFilter.class.getClassLoader());
            this.hasDefault = in.readByte() != 0;
            this.labelRes = in.readInt();
            this.nonLocalizedLabel = in.readString();
            this.icon = in.readInt();
            this.logo = in.readInt();
            this.banner = in.readInt();
        }

        public static final Creator<IntentInfo> CREATOR = new Creator<IntentInfo>() {
            @Override
            public IntentInfo createFromParcel(Parcel source) {
                return new IntentInfo(source);
            }

            @Override
            public IntentInfo[] newArray(int size) {
                return new IntentInfo[size];
            }
        };
    }

    public static class Component<II extends BPackage.IntentInfo> {
        public BPackage owner;
        public ArrayList<II> intents;
        public String className;
        public Bundle metaData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeTypedList(this.activities);
//        dest.writeTypedList(this.receivers);
//        dest.writeTypedList(this.providers);
//        dest.writeTypedList(this.services);
//        dest.writeTypedList(this.instrumentation);
//        dest.writeTypedList(this.permissions);
//        dest.writeTypedList(this.permissionGroups);
        dest.writeStringList(this.requestedPermissions);
        dest.writeTypedArray(this.mSignatures, flags);
        dest.writeParcelable(this.mSigningDetails, flags);
        dest.writeBundle(this.mAppMetaData);
        dest.writeParcelable(this.mExtras, flags);
        dest.writeString(this.packageName);
        dest.writeInt(this.mPreferredOrder);
        dest.writeString(this.mSharedUserId);
        dest.writeStringList(this.usesLibraries);
        dest.writeStringList(this.usesOptionalLibraries);
        dest.writeInt(this.mVersionCode);
        dest.writeParcelable(this.applicationInfo, flags);
        dest.writeString(this.mVersionName);
        dest.writeString(this.baseCodePath);
    }

    public BPackage() {
    }

    protected BPackage(Parcel in) {
//        this.activities = in.createTypedArrayList(Activity.CREATOR);
//        this.receivers = in.createTypedArrayList(Activity.CREATOR);
//        this.providers = in.createTypedArrayList(Provider.CREATOR);
//        this.services = in.createTypedArrayList(Service.CREATOR);
//        this.instrumentation = in.createTypedArrayList(Instrumentation.CREATOR);
//        this.permissions = in.createTypedArrayList(Permission.CREATOR);
//        this.permissionGroups = in.createTypedArrayList(PermissionGroup.CREATOR);
//        this.requestedPermissions = in.createStringArrayList();
//        this.mSignatures = in.createTypedArray(Signature.CREATOR);
//        this.mSigningDetails = in.readParcelable(PackageParser.SigningDetails.class.getClassLoader());
//        this.mAppMetaData = in.readBundle();
//        this.mExtras = in.readParcelable(Object.class.getClassLoader());
//        this.packageName = in.readString();
//        this.mPreferredOrder = in.readInt();
//        this.mSharedUserId = in.readString();
//        this.usesLibraries = in.createStringArrayList();
//        this.usesOptionalLibraries = in.createStringArrayList();
//        this.mVersionCode = in.readInt();
//        this.applicationInfo = in.readParcelable(ApplicationInfo.class.getClassLoader());
//        this.mVersionName = in.readString();
//        this.baseCodePath = in.readString();
    }

    public static final Parcelable.Creator<BPackage> CREATOR = new Parcelable.Creator<BPackage>() {
        @Override
        public BPackage createFromParcel(Parcel source) {
            return new BPackage(source);
        }

        @Override
        public BPackage[] newArray(int size) {
            return new BPackage[size];
        }
    };
}
