package top.niunaijun.blackbox.server.pm;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;

import java.io.File;
import java.util.Locale;

import mirror.android.content.pm.ApplicationInfoL;
import mirror.android.content.pm.ApplicationInfoN;
import mirror.android.content.pm.SigningInfo;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.hook.IOManager;
import top.niunaijun.blackbox.utils.ArrayUtils;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Created by Milk on 4/15/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
@SuppressLint("SdCardPath")
public class PackageManagerCompat {

    public static PackageInfo generatePackageInfo(PackageSetting ps, int flags, int userId) {
        if (ps == null) {
            return null;
        }
        PackageParser.Package p = ps.pkg;
        if (p != null) {
            PackageInfo packageInfo = null;
            try {
                packageInfo = generatePackageInfo(p, flags, 0, 0, 0);
            } catch (Throwable ignored) {
            }
            return packageInfo;
        }
        return null;
    }

    public static PackageInfo generatePackageInfo(PackageParser.Package p, int flags, long firstInstallTime, long lastUpdateTime, int userId) {
        PackageInfo pi = null;
        try {
            pi = BlackBoxCore.getPackageManager().getPackageInfo(BlackBoxCore.getHostPkg(), flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pi == null) {
            return null;
        }
        pi.packageName = p.packageName;
        pi.versionCode = p.mVersionCode;
        pi.versionName = p.mVersionName;
        pi.sharedUserId = p.mSharedUserId;
        pi.sharedUserLabel = p.mSharedUserLabel;
        pi.applicationInfo = generateApplicationInfo(p, flags, userId);

        pi.firstInstallTime = firstInstallTime;
        pi.lastUpdateTime = lastUpdateTime;
        if (!p.requestedPermissions.isEmpty()) {
            String[] requestedPermissions = new String[p.requestedPermissions.size()];
            p.requestedPermissions.toArray(requestedPermissions);
            pi.requestedPermissions = requestedPermissions;
        }

        if ((flags & PackageManager.GET_GIDS) != 0) {
            pi.gids = new int[]{};
        }
        if ((flags & PackageManager.GET_CONFIGURATIONS) != 0) {
            int N = p.configPreferences != null ? p.configPreferences.size() : 0;
            if (N > 0) {
                pi.configPreferences = new ConfigurationInfo[N];
                p.configPreferences.toArray(pi.configPreferences);
            }
            N = p.reqFeatures != null ? p.reqFeatures.size() : 0;
            if (N > 0) {
                pi.reqFeatures = new FeatureInfo[N];
                p.reqFeatures.toArray(pi.reqFeatures);
            }
        }
        if ((flags & PackageManager.GET_ACTIVITIES) != 0) {
            final int N = p.activities.size();
            if (N > 0) {
                int num = 0;
                final ActivityInfo[] res = new ActivityInfo[N];
                for (int i = 0; i < N; i++) {
                    final PackageParser.Activity a = p.activities.get(i);
                    res[num++] = generateActivityInfo(a, flags, userId);
                }
                pi.activities = ArrayUtils.trimToSize(res, num);
            }
        }
        if ((flags & PackageManager.GET_RECEIVERS) != 0) {
            final int N = p.receivers.size();
            if (N > 0) {
                int num = 0;
                final ActivityInfo[] res = new ActivityInfo[N];
                for (int i = 0; i < N; i++) {
                    final PackageParser.Activity a = p.receivers.get(i);
                    res[num++] = generateActivityInfo(a, flags, userId);
                }
                pi.receivers = ArrayUtils.trimToSize(res, num);
            }
        }
        if ((flags & PackageManager.GET_SERVICES) != 0) {
            final int N = p.services.size();
            if (N > 0) {
                int num = 0;
                final ServiceInfo[] res = new ServiceInfo[N];
                for (int i = 0; i < N; i++) {
                    final PackageParser.Service s = p.services.get(i);
                    res[num++] = generateServiceInfo(s, flags, userId);
                }
                pi.services = ArrayUtils.trimToSize(res, num);
            }
        }
        if ((flags & PackageManager.GET_PROVIDERS) != 0) {
            final int N = p.providers.size();
            if (N > 0) {
                int num = 0;
                final ProviderInfo[] res = new ProviderInfo[N];
                for (int i = 0; i < N; i++) {
                    final PackageParser.Provider pr = p.providers.get(i);
                    ProviderInfo providerInfo = generateProviderInfo(pr, flags, userId);
                    if (providerInfo != null) {
                        res[num++] = providerInfo;
                    }
                }
                pi.providers = ArrayUtils.trimToSize(res, num);
            }
        }
        if ((flags & PackageManager.GET_INSTRUMENTATION) != 0) {
            int N = p.instrumentation.size();
            if (N > 0) {
                pi.instrumentation = new InstrumentationInfo[N];
                for (int i = 0; i < N; i++) {
                    pi.instrumentation[i] = generateInstrumentationInfo(
                            p.instrumentation.get(i), flags);
                }
            }
        }
        if ((flags & PackageManager.GET_PERMISSIONS) != 0) {
            int N = p.permissions.size();
            if (N > 0) {
                pi.permissions = new PermissionInfo[N];
                for (int i = 0; i < N; i++) {
                    pi.permissions[i] = generatePermissionInfo(p.permissions.get(i), flags);
                }
            }
            N = p.requestedPermissions.size();
            if (N > 0) {
                pi.requestedPermissions = new String[N];
                pi.requestedPermissionsFlags = new int[N];
                for (int i = 0; i < N; i++) {
                    final String perm = p.requestedPermissions.get(i);
                    pi.requestedPermissions[i] = perm;
                    // The notion of required permissions is deprecated but for compatibility.
//                    pi.requestedPermissionsFlags[i] |= PackageInfo.REQUESTED_PERMISSION_REQUIRED;
//                    if (grantedPermissions != null && grantedPermissions.contains(perm)) {
//                        pi.requestedPermissionsFlags[i] |= PackageInfo.REQUESTED_PERMISSION_GRANTED;
//                    }
                }
            }
        }
        if ((flags & PackageManager.GET_SIGNATURES) != 0) {
            if (BuildCompat.isPie()) {
                pi.signatures = p.mSigningDetails.signatures;
            } else {
                pi.signatures = p.mSignatures;
            }
        }
        if (BuildCompat.isPie()) {
            if ((flags & PackageManager.GET_SIGNING_CERTIFICATES) != 0) {
                SigningInfo.mSigningDetails.set(pi.signingInfo, p.mSigningDetails);
            }
        }
        return pi;
    }

    public static ActivityInfo generateActivityInfo(PackageParser.Activity a, int flags, int userId) {
        // Make shallow copies so we can store the metadata safely
        ActivityInfo ai = new ActivityInfo(a.info);
        ai.metaData = a.metaData;
        ai.applicationInfo = generateApplicationInfo(a.owner, flags, userId);
        return ai;
    }

    public static final ServiceInfo generateServiceInfo(PackageParser.Service s, int flags, int userId) {
        // Make shallow copies so we can store the metadata safely
        ServiceInfo si = new ServiceInfo(s.info);
        si.metaData = s.metaData;
        si.applicationInfo = generateApplicationInfo(s.owner, flags, userId);
        return si;
    }

    public static final ProviderInfo generateProviderInfo(PackageParser.Provider p, int flags, int userId) {
        // Make shallow copies so we can store the metadata safely
        ProviderInfo pi = new ProviderInfo(p.info);
        if (pi.authority == null)
            return null;
        pi.metaData = p.metaData;
        if ((flags & PackageManager.GET_URI_PERMISSION_PATTERNS) == 0) {
            pi.uriPermissionPatterns = null;
        }
        pi.applicationInfo = generateApplicationInfo(p.owner, flags, userId);
        return pi;
    }

    public static final PermissionInfo generatePermissionInfo(
            PackageParser.Permission p, int flags) {
        if (p == null) return null;
        if ((flags & PackageManager.GET_META_DATA) == 0) {
            return p.info;
        }
        PermissionInfo pi = new PermissionInfo(p.info);
        pi.metaData = p.metaData;
        return pi;
    }

    public static final InstrumentationInfo generateInstrumentationInfo(
            PackageParser.Instrumentation i, int flags) {
        if (i == null) return null;
        if ((flags & PackageManager.GET_META_DATA) == 0) {
            return i.info;
        }
        InstrumentationInfo ii = new InstrumentationInfo(i.info);
        ii.metaData = i.metaData;
        return ii;
    }

    public static ApplicationInfo generateApplicationInfo(PackageParser.Package p, int flags, int userId) {
        ApplicationInfo baseApplication;
        try {
            baseApplication = BlackBoxCore.getPackageManager().getApplicationInfo(BlackBoxCore.getHostPkg(), flags);
        } catch (Exception e) {
            return null;
        }
        if (p.applicationInfo == null) {
            p.applicationInfo = BlackBoxCore.getPackageManager().getPackageArchiveInfo(p.baseCodePath, 0).applicationInfo;
        }
        ApplicationInfo ai = new ApplicationInfo(p.applicationInfo);
        if ((flags & PackageManager.GET_META_DATA) != 0) {
            ai.metaData = p.mAppMetaData;
        }
        ai.dataDir = String.format(Locale.CHINA, "/data/user/%d/%s", userId, p.packageName);
        ai.nativeLibraryDir = new File(ai.dataDir, "lib").getAbsolutePath();
        ai.processName = BPackageManagerService.fixProcessName(p.packageName, ai.packageName);
        ai.publicSourceDir = p.baseCodePath;
        ai.sourceDir = p.baseCodePath;
        ai.uid = baseApplication.uid;
        if (BuildCompat.isL()) {
            ApplicationInfoL.primaryCpuAbi.set(ai, Build.CPU_ABI);
            ApplicationInfoL.scanPublicSourceDir.set(ai, ApplicationInfoL.scanPublicSourceDir.get(baseApplication));
            ApplicationInfoL.scanSourceDir.set(ai, ApplicationInfoL.scanSourceDir.get(baseApplication));
        }
        if (BuildCompat.isN()) {
            ai.deviceProtectedDataDir = String.format(Locale.CHINA, "/data/user_de/%d/%s", userId, p.packageName);

            if (ApplicationInfoN.deviceEncryptedDataDir != null) {
                ApplicationInfoN.deviceEncryptedDataDir.set(ai, ai.deviceProtectedDataDir);
            }
            if (ApplicationInfoN.credentialEncryptedDataDir != null) {
                ApplicationInfoN.credentialEncryptedDataDir.set(ai, ai.dataDir);
            }
            if (ApplicationInfoN.deviceProtectedDataDir != null) {
                ApplicationInfoN.deviceProtectedDataDir.set(ai, ai.deviceProtectedDataDir);
            }
            if (ApplicationInfoN.credentialProtectedDataDir != null) {
                ApplicationInfoN.credentialProtectedDataDir.set(ai, ai.dataDir);
            }
        }
        IOManager.redirectApplication(ai);
        return ai;
    }
}
