package top.niunaijun.blackbox.client.hook.env;

import android.content.ComponentName;

import java.util.ArrayList;
import java.util.List;

import top.niunaijun.blackbox.BlackBoxCore;

/**
 * Created by Milk on 4/21/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ClientSystemEnv {
    private static List<String> sSystemPackages = new ArrayList<>();
    private static final String XP_PACKAGE_NAME = "de.robv.android.xposed";

    static {
        sSystemPackages.add("android");
        sSystemPackages.add("com.google.android.webview");
        sSystemPackages.add("com.android.webview");
    }

    public static boolean isOpenPackage(String packageName) {
        return sSystemPackages.contains(packageName);
    }

    public static boolean isOpenPackage(ComponentName componentName) {
        return componentName != null && isOpenPackage(componentName.getPackageName());
    }

    public static boolean isFakePackage(String packageName) {
        if (XP_PACKAGE_NAME.equals(packageName) && BlackBoxCore.get().isXPEnable()) {
            return true;
        }

        return false;
    }
}
