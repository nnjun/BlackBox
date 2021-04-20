package mirror.android.os.storage;

import android.os.storage.StorageVolume;

import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

/**
 * Created by Milk on 4/10/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class StorageManager {
    public static Class<?> TYPE = RefClass.load(StorageManager.class, "android.os.storage.StorageManager");
    @MethodParams({int.class, int.class})
    public static RefStaticMethod<StorageVolume[]> getVolumeList;
}
