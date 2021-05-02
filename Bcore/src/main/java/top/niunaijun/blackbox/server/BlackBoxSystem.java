package top.niunaijun.blackbox.server;

import top.niunaijun.blackbox.BEnvironment;
import top.niunaijun.blackbox.server.am.BActivityManagerService;
import top.niunaijun.blackbox.server.am.BJobManagerService;
import top.niunaijun.blackbox.server.os.BStorageManagerService;
import top.niunaijun.blackbox.server.pm.BPackageInstallerService;
import top.niunaijun.blackbox.server.pm.BPackageManagerService;
import top.niunaijun.blackbox.server.pm.BXpoesdManagerService;
import top.niunaijun.blackbox.server.user.BUserManagerService;

/**
 * Created by Milk on 4/22/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class BlackBoxSystem {
    private static BlackBoxSystem sBlackBoxSystem;

    public static BlackBoxSystem getSystem() {
        if (sBlackBoxSystem == null) {
            synchronized (BlackBoxSystem.class) {
                if (sBlackBoxSystem == null) {
                    sBlackBoxSystem = new BlackBoxSystem();
                }
            }
        }
        return sBlackBoxSystem;
    }

    public void startup() {
        BEnvironment.load();

        BPackageManagerService.get().systemReady();
        BUserManagerService.get().systemReady();
        BActivityManagerService.get().systemReady();
        BJobManagerService.get().systemReady();
        BStorageManagerService.get().systemReady();
        BPackageInstallerService.get().systemReady();
        BXpoesdManagerService.get().systemReady();
    }
}
