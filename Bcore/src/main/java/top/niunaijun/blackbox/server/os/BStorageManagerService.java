package top.niunaijun.blackbox.server.os;

import android.content.Context;
import android.net.Uri;
import android.os.Process;
import android.os.RemoteException;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import java.io.File;

import top.niunaijun.blackbox.BEnvironment;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.StubManifest;
import top.niunaijun.blackbox.server.ISystemService;
import top.niunaijun.blackbox.server.user.BUserHandle;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.client.hook.provider.FileProvider;

/**
 * Created by Milk on 4/10/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class BStorageManagerService extends IBStorageManagerService.Stub implements ISystemService {
    private static BStorageManagerService sService = new BStorageManagerService();
    private StorageManager mStorageManager;

    public static BStorageManagerService get() {
        return sService;
    }

    public BStorageManagerService() {
        mStorageManager = (StorageManager) BlackBoxCore.getContext().getSystemService(Context.STORAGE_SERVICE);
    }

    @Override
    public StorageVolume[] getVolumeList(int uid, String packageName, int flags, int userId) throws RemoteException {
        try {
            StorageVolume[] storageVolumes = mirror.android.os.storage.StorageManager.getVolumeList.call(BUserHandle.getUserId(Process.myUid()), 0);
            for (StorageVolume storageVolume : storageVolumes) {
                mirror.android.os.storage.StorageVolume.mPath.set(storageVolume, BEnvironment.getExternalUserDir(userId));

                if (BuildCompat.isPie()) {
                    mirror.android.os.storage.StorageVolume.mInternalPath.set(storageVolume, BEnvironment.getExternalUserDir(userId));
                }
            }
            return storageVolumes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new StorageVolume[]{};
    }

    @Override
    public Uri getUriForFile(String file) throws RemoteException {
        return FileProvider.getUriForFile(BlackBoxCore.getContext(), StubManifest.getStubFileProvider(), new File(file));
    }

    @Override
    public void systemReady() {

    }
}
