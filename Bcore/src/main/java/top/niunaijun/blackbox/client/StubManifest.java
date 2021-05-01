package top.niunaijun.blackbox.client;

import java.util.Locale;

import top.niunaijun.blackbox.BlackBoxCore;

/**
 * Created by Milk on 4/1/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class StubManifest {
    public static final int FREE_COUNT = 100;

    public static boolean isStub(String msg) {
        return getBindProvider().equals(msg) || msg.contains("stub_content_provider_");
    }

    public static String getBindProvider() {
        return BlackBoxCore.getHostPkg() + ".blackbox.BindProvider";
    }

    public static String getStubAuthorities(int index) {
        return String.format(Locale.CHINA, "%s.stub_content_provider_%d", BlackBoxCore.getHostPkg(), index);
    }

    public static String getStubActivity(int index) {
        return String.format(Locale.CHINA, "top.niunaijun.blackbox.client.stub.StubActivity$P%d", index);
    }

    public static String getStubService(int index) {
        return String.format(Locale.CHINA, "top.niunaijun.blackbox.client.stub.StubService$P%d", index);
    }

    public static String getStubJobService(int index) {
        return String.format(Locale.CHINA, "top.niunaijun.blackbox.client.stub.StubJobService$P%d", index);
    }

    public static String getStubFileProvider() {
        return BlackBoxCore.getHostPkg() + ".blackbox.FileProvider";
    }

    public static String getStubReceiver() {
        return BlackBoxCore.getHostPkg() + ".stub_receiver";
    }

    public static String getProcessName(int vpid) {
        return BlackBoxCore.getHostPkg() + ":p" + vpid;
    }
}
