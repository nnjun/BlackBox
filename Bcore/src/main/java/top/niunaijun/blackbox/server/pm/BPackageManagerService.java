package top.niunaijun.blackbox.server.pm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.ArrayMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.entity.pm.InstallResult;
import top.niunaijun.blackbox.utils.NativeUtils;
import top.niunaijun.blackbox.utils.Reflector;
import top.niunaijun.blackbox.utils.compat.ObjectsCompat;
import top.niunaijun.blackbox.utils.compat.PackageParserCompat;

import static android.content.pm.PackageManager.MATCH_DIRECT_BOOT_UNAWARE;


/**
 * Created by Milk on 4/1/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class BPackageManagerService extends IBPackageManagerService.Stub {
    public static final String TAG = "VPackageManagerService";
    public static BPackageManagerService sService = new BPackageManagerService();
    private Settings mSettings;
    private ComponentResolver mComponentResolver;
    final ArrayMap<String, PackageParser.Package> mPackages = new ArrayMap<>();
    final ArrayMap<String, PackageParser.Package> mFilePackages = new ArrayMap<>();

    public static BPackageManagerService get() {
        return sService;
    }

    public BPackageManagerService() {
        mSettings = new Settings();
        mComponentResolver = new ComponentResolver();
    }

    @Override
    public ApplicationInfo getApplicationInfo(String packageName, int flags, int userId) {
        if (ObjectsCompat.equals(packageName, BlackBoxCore.getHostPkg())) {
            try {
                return BlackBoxCore.getPackageManager().getApplicationInfo(packageName, flags);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        flags = updateFlags(flags, userId);
        // reader
        synchronized (mPackages) {
            // Normalize package name to handle renamed packages and static libs
            PackageParser.Package p = mPackages.get(packageName);
            if (p != null) {
                return PackageManagerCompat.generateApplicationInfo(p, flags, userId);
            }
        }
        return null;
    }

    @Override
    public ResolveInfo resolveService(Intent intent, int flags, String resolvedType, int userId) {
        List<ResolveInfo> query = queryIntentServicesInternal(
                intent, resolvedType, flags, userId);
        if (query != null) {
            if (query.size() >= 1) {
                // If there is more than one service with the same priority,
                // just arbitrarily pick the first one.
                return query.get(0);
            }
        }
        return null;
    }

    private List<ResolveInfo> queryIntentServicesInternal(Intent intent, String resolvedType, int flags, int userId) {
        ComponentName comp = intent.getComponent();
        if (comp == null) {
            if (intent.getSelector() != null) {
                intent = intent.getSelector();
                comp = intent.getComponent();
            }
        }
        if (comp != null) {
            final List<ResolveInfo> list = new ArrayList<>(1);
            final ServiceInfo si = getServiceInfo(comp, flags, userId);
            if (si != null) {
                // When specifying an explicit component, we prevent the service from being
                // used when either 1) the service is in an instant application and the
                // caller is not the same instant application or 2) the calling package is
                // ephemeral and the activity is not visible to ephemeral applications.
                final ResolveInfo ri = new ResolveInfo();
                ri.serviceInfo = si;
                list.add(ri);
            }
            return list;
        }

        // reader
        synchronized (mPackages) {
            String pkgName = intent.getPackage();
            if (pkgName == null) {
                return null;
            }
            final PackageParser.Package pkg = mPackages.get(pkgName);
            if (pkg != null) {
                return mComponentResolver.queryServices(intent, resolvedType, flags, pkg.services,
                        userId);
            }
            return Collections.emptyList();
        }
    }

    @Override
    public ResolveInfo resolveActivity(Intent intent, int flags, String resolvedType, int userId) {
        List<ResolveInfo> resolves = queryIntentActivities(intent, resolvedType, flags, userId);
        return chooseBestActivity(intent, resolvedType, flags, resolves);
    }

    @Override
    public ProviderInfo resolveContentProvider(String authority, int flags, int userId) {
        return mComponentResolver.queryProvider(authority, flags, userId);
    }

    @Override
    public ResolveInfo resolveIntent(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
        List<ResolveInfo> resolves = queryIntentActivities(intent, resolvedType, flags, userId);
        return chooseBestActivity(intent, resolvedType, flags, resolves);
    }

    private ResolveInfo chooseBestActivity(Intent intent, String resolvedType,
                                           int flags, List<ResolveInfo> query) {
        if (query != null) {
            final int N = query.size();
            if (N == 1) {
                return query.get(0);
            } else if (N > 1) {
                // If there is more than one activity with the same priority,
                // then let the user decide between them.
                ResolveInfo r0 = query.get(0);
                ResolveInfo r1 = query.get(1);
                // If the first activity has a higher priority, or a different
                // default, then it is always desirable to pick it.
                if (r0.priority != r1.priority
                        || r0.preferredOrder != r1.preferredOrder
                        || r0.isDefault != r1.isDefault) {
                    return query.get(0);
                }
            }
        }
        return null;
    }

    private List<ResolveInfo> queryIntentActivities(Intent intent,
                                                    String resolvedType, int flags, int userId) {
        final String pkgName = intent.getPackage();
        ComponentName comp = intent.getComponent();
        if (comp == null) {
            if (intent.getSelector() != null) {
                intent = intent.getSelector();
                comp = intent.getComponent();
            }
        }

        if (comp != null) {
            final List<ResolveInfo> list = new ArrayList<>(1);
            final ActivityInfo ai = getActivity(comp, flags, userId);
            if (ai != null) {
                // When specifying an explicit component, we prevent the activity from being
                // used when either 1) the calling package is normal and the activity is within
                // an ephemeral application or 2) the calling package is ephemeral and the
                // activity is not visible to ephemeral applications.
                final ResolveInfo ri = new ResolveInfo();
                ri.activityInfo = ai;
                list.add(ri);
                return list;
            }
        }

        // reader
        synchronized (mPackages) {
            if (pkgName != null) {
                return mComponentResolver.queryActivities(intent, resolvedType, flags, userId);
            }
        }
        return Collections.emptyList();
    }


    private ActivityInfo getActivity(ComponentName component, int flags,
                                     int userId) {
        flags = updateFlags(flags, userId);
        synchronized (mPackages) {
            PackageParser.Activity a = mComponentResolver.getActivity(component);

            if (a != null) {
                PackageSetting ps = mSettings.mPackages.get(component.getPackageName());
                if (ps == null) return null;
                return PackageManagerCompat.generateActivityInfo(a, flags, userId);
            }
        }
        return null;
    }

    @Override
    public PackageInfo getPackageInfo(String packageName, int flags, int userId) throws RemoteException {
        if (ObjectsCompat.equals(packageName, BlackBoxCore.getHostPkg())) {
            try {
                return BlackBoxCore.getPackageManager().getPackageInfo(packageName, flags);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        flags = updateFlags(flags, userId);
        // reader
        synchronized (mPackages) {
            // Normalize package name to handle renamed packages and static libs
            PackageParser.Package p = mPackages.get(packageName);
            if (p != null) {
                final PackageSetting ps = (PackageSetting) p.mExtras;
                return PackageManagerCompat.generatePackageInfo(ps, flags, userId);
            }
        }
        return null;
    }

    @Override
    public ServiceInfo getServiceInfo(ComponentName component, int flags, int userId) {
        synchronized (mPackages) {
            PackageParser.Service s = mComponentResolver.getService(component);
            PackageSetting ps = mSettings.mPackages.get(component.getPackageName());
            if (ps == null) return null;
            return PackageManagerCompat.generateServiceInfo(
                    s, flags, userId);
        }
    }

    @Override
    public ActivityInfo getReceiverInfo(ComponentName component, int flags, int userId) {
        synchronized (mPackages) {
            PackageParser.Activity a = mComponentResolver.getReceiver(component);
            if (a != null) {
                PackageSetting ps = mSettings.mPackages.get(component.getPackageName());
                if (ps == null) return null;
                return PackageManagerCompat.generateActivityInfo(
                        a, flags, userId);
            }
        }
        return null;
    }

    @Override
    public ActivityInfo getActivityInfo(ComponentName component, int flags, int userId) throws RemoteException {
        synchronized (mPackages) {
            PackageParser.Activity a = mComponentResolver.getActivity(component);

            if (a != null) {
                PackageSetting ps = mSettings.mPackages.get(component.getPackageName());
                if (ps == null) return null;
                return PackageManagerCompat.generateActivityInfo(
                        a, flags, userId);
            }
        }
        return null;
    }

    @Override
    public ProviderInfo getProviderInfo(ComponentName component, int flags, int userId) throws RemoteException {
        synchronized (mPackages) {
            PackageParser.Provider p = mComponentResolver.getProvider(component);
            if (p != null) {
                PackageSetting ps = mSettings.mPackages.get(component.getPackageName());
                if (ps == null) return null;
                return PackageManagerCompat.generateProviderInfo(
                        p, flags, userId);
            }
        }
        return null;
    }

    @Override
    public List<ResolveInfo> queryIntentActivities(Intent intent, int flags, String resolvedType, int userId) throws RemoteException {
        final String pkgName = intent.getPackage();
        ComponentName comp = intent.getComponent();
        if (comp == null) {
            if (intent.getSelector() != null) {
                intent = intent.getSelector();
                comp = intent.getComponent();
            }
        }

        if (comp != null) {
            final List<ResolveInfo> list = new ArrayList<>(1);
            final ActivityInfo ai = getActivityInfo(comp, flags, userId);
            if (ai != null) {
                // When specifying an explicit component, we prevent the activity from being
                // used when either 1) the calling package is normal and the activity is within
                // an ephemeral application or 2) the calling package is ephemeral and the
                // activity is not visible to ephemeral applications.
                final ResolveInfo ri = new ResolveInfo();
                ri.activityInfo = ai;
                list.add(ri);
            }
            return list;
        }

        // reader
        List<ResolveInfo> result;
        synchronized (mPackages) {
            if (pkgName != null) {
                final PackageParser.Package pkg = mPackages.get(pkgName);
                result = null;
                if (pkg != null) {
                    result = mComponentResolver.queryActivities(
                            intent, resolvedType, flags, pkg.activities, userId);
                }
                if (result == null || result.size() == 0) {
                    // the caller wants to resolve for a particular package; however, there
                    // were no installed results, so, try to find an ephemeral result
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                }
                return result;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<ResolveInfo> queryBroadcastReceivers(Intent intent, int flags, String resolvedType, int userId) throws RemoteException {
        ComponentName comp = intent.getComponent();
        if (comp == null) {
            if (intent.getSelector() != null) {
                intent = intent.getSelector();
                comp = intent.getComponent();
            }
        }
        if (comp != null) {
            final List<ResolveInfo> list = new ArrayList<>(1);
            final ActivityInfo ai = getReceiverInfo(comp, flags, userId);
            if (ai != null) {
                // When specifying an explicit component, we prevent the activity from being
                // used when either 1) the calling package is normal and the activity is within
                // an instant application or 2) the calling package is ephemeral and the
                // activity is not visible to instant applications.
                ResolveInfo ri = new ResolveInfo();
                ri.activityInfo = ai;
                list.add(ri);
            }
            return list;
        }

        // reader
        synchronized (mPackages) {
            String pkgName = intent.getPackage();
            final PackageParser.Package pkg = mPackages.get(pkgName);
            if (pkg != null) {
                return mComponentResolver.queryReceivers(
                        intent, resolvedType, flags, pkg.receivers, userId);
            }
            return Collections.emptyList();
        }
    }

    @Override
    public List<ProviderInfo> queryContentProviders(String processName, int uid, int flags, int userId) throws RemoteException {
        List<ProviderInfo> providers = new ArrayList<>();
        if (TextUtils.isEmpty(processName))
            return providers;
        providers.addAll(mComponentResolver.queryProviders(processName, null, flags, userId));
        return providers;
    }

    @Override
    public PackageInfo loadPackage(String file, int userId) throws RemoteException {
        try {
            PackageParser.Package filePkg = mFilePackages.get(file);
            if (filePkg != null && mSettings.mPackages.get(filePkg.packageName) != null) {
                return getPackageInfo(filePkg.packageName, 0, userId);
            }

            PackageParser parser = PackageParserCompat.createParser(new File(file));
            PackageParser.Package aPackage = PackageParserCompat.parsePackage(parser, new File(file), 0);
            PackageParserCompat.collectCertificates(parser, aPackage, 0);
            ApplicationInfo applicationInfo = PackageManagerCompat.generateApplicationInfo(aPackage, 0, userId);

            Reflector.with(aPackage)
                    .field("applicationInfo")
                    .set(applicationInfo);


            PackageSetting packageSetting = new PackageSetting();
            packageSetting.pkg = aPackage;
            aPackage.mExtras = packageSetting;
            mPackages.put(aPackage.packageName, aPackage);
            mComponentResolver.addAllComponents(aPackage, applicationInfo);
            mSettings.mPackages.put(aPackage.packageName, packageSetting);

            try {
                // copy virtual
                NativeUtils.copyNativeLib(new File(BlackBoxCore.getContext().getApplicationInfo().sourceDir), new File(applicationInfo.nativeLibraryDir));
                // copy dest apk
                NativeUtils.copyNativeLib(new File(applicationInfo.sourceDir), new File(applicationInfo.nativeLibraryDir));
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return getPackageInfo(aPackage.packageName, 0, userId);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    @Override
    public InstallResult installPackageAsUser(String file, int userId) {

        return null;
    }

    static String fixProcessName(String defProcessName, String processName) {
        if (processName == null) {
            return defProcessName;
        }
        return processName;
    }

    /**
     * Update given flags based on encryption status of current user.
     */
    private int updateFlags(int flags, int userId) {
        if ((flags & (PackageManager.MATCH_DIRECT_BOOT_UNAWARE
                | PackageManager.MATCH_DIRECT_BOOT_AWARE)) != 0) {
            // Caller expressed an explicit opinion about what encryption
            // aware/unaware components they want to see, so fall through and
            // give them what they want
        } else {
            // Caller expressed no opinion, so match based on user state
            flags |= PackageManager.MATCH_DIRECT_BOOT_AWARE | MATCH_DIRECT_BOOT_UNAWARE;
        }
        return flags;
    }
}