package top.niunaijun.blackbox.server.pm.installer;


import java.io.File;
import java.io.IOException;

import top.niunaijun.blackbox.BEnvironment;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.server.pm.BPackageSettings;
import top.niunaijun.blackbox.utils.FileUtils;
import top.niunaijun.blackbox.utils.NativeUtils;

/**
 * Created by Milk on 4/24/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 * 拷贝文件相关
 */
public class CopyExecutor implements Executor {

    @Override
    public int exec(BPackageSettings ps, InstallOption option, int userId) {
        try {
            NativeUtils.copyNativeLib(new File(ps.pkg.baseCodePath), BEnvironment.getAppLibDir(ps.pkg.packageName));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        if ((option.flags & InstallOption.FLAG_STORAGE) != 0) {
            // 外部安装
            try {
                FileUtils.copyFile(new File(ps.pkg.baseCodePath), BEnvironment.getBaseApkDir(ps.pkg.packageName));
                // update baseCodePath
                ps.pkg.baseCodePath = BEnvironment.getBaseApkDir(ps.pkg.packageName).getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        } else if ((option.flags & InstallOption.FLAG_SYSTEM) != 0) {
            // 系统安装
        }
        return 0;
    }
}
