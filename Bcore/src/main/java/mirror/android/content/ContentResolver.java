package mirror.android.content;

import android.os.IInterface;

import mirror.RefClass;
import mirror.RefObject;
import mirror.RefStaticObject;

/**
 * @author Lody
 */

public class ContentResolver {
    public static Class<?> TYPE = RefClass.load(ContentResolver.class, android.content.ContentResolver.class);
    public static RefStaticObject<IInterface> sContentService;
    public static RefObject<String> mPackageName;
}
