package top.niunaijun.blackbox.server.os;

import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import java.io.File;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.StubManifest;
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
public class VStorageManagerService extends IBStorageManagerService.Stub {
    private static VStorageManagerService sService = new VStorageManagerService();
    private StorageManager mStorageManager;

    public static VStorageManagerService get() {
        return sService;
    }

    public VStorageManagerService() {
        mStorageManager = (StorageManager) BlackBoxCore.getContext().getSystemService(Context.STORAGE_SERVICE);
    }

    @Override
    public StorageVolume[] getVolumeList(int uid, String packageName, int flags) throws RemoteException {
        try {
            StorageVolume[] storageVolumes = mirror.android.os.storage.StorageManager.getVolumeList.call(0, 0);
//            if (VirtualCore.getHostPkg().equals(packageName)) {
//                return storageVolumes;
//            }
            for (StorageVolume storageVolume : storageVolumes) {
                if (mirror.android.os.storage.StorageVolume.mPath.get(storageVolume).getAbsolutePath().contains(BlackBoxCore.getHostPkg())){
                    continue;
                }
                mirror.android.os.storage.StorageVolume.mPath.set(storageVolume,
                        new File(BlackBoxCore.getContext().getExternalFilesDir("virtual"),
                                mirror.android.os.storage.StorageVolume.mPath.get(storageVolume).getAbsolutePath()));

                if (BuildCompat.isPie()) {
                    mirror.android.os.storage.StorageVolume.mInternalPath.set(storageVolume,
                            new File(BlackBoxCore.getContext().getExternalFilesDir("virtual"),
                                    mirror.android.os.storage.StorageVolume.mInternalPath.get(storageVolume).getAbsolutePath()));
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
}
