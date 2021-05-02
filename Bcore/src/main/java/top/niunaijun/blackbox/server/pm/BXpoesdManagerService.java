package top.niunaijun.blackbox.server.pm;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;

import androidx.core.util.AtomicFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.niunaijun.blackbox.BEnvironment;
import top.niunaijun.blackbox.entity.pm.InstalledModule;
import top.niunaijun.blackbox.entity.pm.XPoesdConfig;
import top.niunaijun.blackbox.server.ISystemService;
import top.niunaijun.blackbox.server.user.BUserHandle;
import top.niunaijun.blackbox.utils.CloseUtils;
import top.niunaijun.blackbox.utils.FileUtils;
import top.niunaijun.blackbox.utils.compat.XPoesdParserCompat;

/**
 * Created by Milk on 5/2/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class BXpoesdManagerService extends IBXPoesdManagerService.Stub implements ISystemService, PackageMonitor {
    private static BXpoesdManagerService sService = new BXpoesdManagerService();

    private XPoesdConfig mXPoesdConfig;
    private final Object mLock = new Object();
    private BPackageManagerService mPms;
    private final Map<String, InstalledModule> mCacheModule = new HashMap<>();

    public static BXpoesdManagerService get() {
        return sService;
    }

    public BXpoesdManagerService() {
        mPms = BPackageManagerService.get();
        mPms.addPackageMonitor(this);
    }

    @Override
    public void systemReady() {
        synchronized (mLock) {
            loadModuleStateLr();
        }
    }

    @Override
    public boolean isXPEnable() {
        synchronized (mLock) {
            return mXPoesdConfig.enable;
        }
    }

    @Override
    public void setXPEnable(boolean enable) {
        synchronized (mLock) {
            mXPoesdConfig.enable = enable;
            saveModuleStateLw();
        }
    }

    @Override
    public boolean isModuleEnable(String packageName) {
        synchronized (mLock) {
            Boolean enable = mXPoesdConfig.moduleState.get(packageName);
            return enable == null ? false : enable;
        }
    }

    @Override
    public void setModuleEnable(String packageName, boolean enable) {
        synchronized (mLock) {
            if (!mPms.isInstalled(packageName, BUserHandle.USER_XPOESD)) {
                return;
            }
            mXPoesdConfig.moduleState.put(packageName, enable);
            saveModuleStateLw();
        }
    }

    @Override
    public List<InstalledModule> getInstalledModules() {
        synchronized (mLock) {
            List<ApplicationInfo> installedApplications = mPms.getInstalledApplications(PackageManager.GET_META_DATA, BUserHandle.USER_XPOESD);
            for (ApplicationInfo installedApplication : installedApplications) {
                if (mCacheModule.containsKey(installedApplication.packageName))
                    continue;
                InstalledModule installedModule = XPoesdParserCompat.parseModule(installedApplication);
                if (installedModule != null) {
                    mCacheModule.put(installedApplication.packageName, installedModule);
                }
            }
            ArrayList<InstalledModule> installedModules = new ArrayList<>(mCacheModule.values());
            for (InstalledModule installedModule : installedModules) {
                installedModule.enable = isModuleEnable(installedModule.packageName);
            }
            return installedModules;
        }
    }

    private void loadModuleStateLr() {
        File xpModuleConf = BEnvironment.getXPModuleConf();
        if (!xpModuleConf.exists()) {
            mXPoesdConfig = new XPoesdConfig();
            saveModuleStateLw();
            return;
        }
        Parcel parcel = null;
        try {
            parcel = FileUtils.readToParcel(xpModuleConf);
            mXPoesdConfig = new XPoesdConfig(parcel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (parcel != null) {
                parcel.recycle();
            }
        }
    }

    private void saveModuleStateLw() {
        Parcel parcel = Parcel.obtain();
        AtomicFile atomicFile = new AtomicFile(BEnvironment.getXPModuleConf());
        FileOutputStream fileOutputStream = null;
        try {
            mXPoesdConfig.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            fileOutputStream = atomicFile.startWrite();
            FileUtils.writeParcelToOutput(parcel, fileOutputStream);
            atomicFile.finishWrite(fileOutputStream);
        } catch (Exception ignored) {
            atomicFile.failWrite(fileOutputStream);
        } finally {
            parcel.recycle();
            CloseUtils.close(fileOutputStream);
        }
    }

    @Override
    public void onPackageUninstalled(String packageName, int userId) {
        if (userId != BUserHandle.USER_XPOESD && userId != BUserHandle.USER_ALL) {
            return;
        }
        synchronized (mLock) {
            mCacheModule.remove(packageName);
            mXPoesdConfig.moduleState.remove(packageName);
            saveModuleStateLw();
        }
    }

    @Override
    public void onPackageInstalled(String packageName, int userId) {
        if (userId != BUserHandle.USER_XPOESD && userId != BUserHandle.USER_ALL) {
            return;
        }
        synchronized (mLock) {
            mCacheModule.remove(packageName);
            mXPoesdConfig.moduleState.put(packageName, false);
            saveModuleStateLw();
        }
    }
}
