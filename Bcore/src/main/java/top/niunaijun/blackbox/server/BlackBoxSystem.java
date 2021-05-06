package top.niunaijun.blackbox.server;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

import top.niunaijun.blackbox.BEnvironment;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.hook.env.ClientSystemEnv;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.entity.pm.InstallResult;
import top.niunaijun.blackbox.server.am.BActivityManagerService;
import top.niunaijun.blackbox.server.am.BJobManagerService;
import top.niunaijun.blackbox.server.os.BStorageManagerService;
import top.niunaijun.blackbox.server.pm.BPackageInstallerService;
import top.niunaijun.blackbox.server.pm.BPackageManagerService;
import top.niunaijun.blackbox.server.pm.BXposedManagerService;
import top.niunaijun.blackbox.server.user.BUserHandle;
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
        BXposedManagerService.get().systemReady();

        List<String> preInstallPackages = ClientSystemEnv.getPreInstallPackages();
        for (String preInstallPackage : preInstallPackages) {
            try {
                PackageInfo packageInfo = BlackBoxCore.getPackageManager().getPackageInfo(preInstallPackage, 0);
                BPackageManagerService.get().installPackageAsUser(packageInfo.applicationInfo.sourceDir, InstallOption.installBySystem(), BUserHandle.USER_ALL);
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
    }
}
