package top.niunaijun.blackbox.client;

import java.io.File;

import top.niunaijun.blackbox.BlackBoxCore;

/**
 * Created by Milk on 4/9/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class VMCore {

    static {
        new File("");
        if (BlackBoxCore.is64Bit()) {
            System.loadLibrary("vm64");
        } else {
            System.loadLibrary("vm");
        }
    }

    public static native void init(int apiLevel);

    public static native void addIORule(String targetPath, String relocatePath);
}
