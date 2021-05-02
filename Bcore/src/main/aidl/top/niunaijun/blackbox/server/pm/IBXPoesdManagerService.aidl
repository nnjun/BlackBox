// IBXPoesdManagerService.aidl

package top.niunaijun.blackbox.server.pm;

import java.util.List;
import top.niunaijun.blackbox.entity.pm.InstalledModule;

interface IBXPoesdManagerService {
    boolean isXPEnable();
    void setXPEnable(boolean enable);
    boolean isModuleEnable(String packageName);
    void setModuleEnable(String packageName, boolean enable);
    List<InstalledModule> getInstalledModules();
}