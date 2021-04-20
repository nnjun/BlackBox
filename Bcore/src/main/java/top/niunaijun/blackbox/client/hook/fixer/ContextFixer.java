package top.niunaijun.blackbox.client.hook.fixer;

import android.content.Context;
import android.content.ContextWrapper;

import mirror.android.app.ContextImpl;
import mirror.android.app.ContextImplKitkat;
import mirror.android.content.ContentResolverJBMR2;
import top.niunaijun.blackbox.BlackBoxCore;

/**
 * Created by Milk on 3/31/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ContextFixer {
    public static final String TAG = "ContextFixer";

    public static void fix(Context context) {
        try {
            int deep = 0;
            while (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
                deep++;
                if (deep >= 10) {
                    return;
                }
            }
            ContextImpl.mPackageManager.set(context, null);
            try {
                context.getPackageManager();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            ContextImpl.mBasePackageName.set(context, BlackBoxCore.getHostPkg());
            ContextImplKitkat.mOpPackageName.set(context, BlackBoxCore.getHostPkg());

            ContentResolverJBMR2.mPackageName.set(context.getContentResolver(), BlackBoxCore.getHostPkg());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
