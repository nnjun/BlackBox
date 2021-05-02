package top.niunaijun.blackboxa.data

import android.content.pm.ApplicationInfo
import android.net.Uri
import android.webkit.URLUtil
import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.BlackBoxCore.getPackageManager
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackbox.utils.AbiUtils
import java.io.File


/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 23:05
 */

class AppsRepository {

    fun getVmInstallList(userId: Int, appsLiveData: MutableLiveData<List<AppInfo>>) {
        val applicationList = BlackBoxCore.get().getInstalledApplications(0, userId)
        val appInfoList = mutableListOf<AppInfo>()
        applicationList.forEach {
            val info = AppInfo(
                    it.loadLabel(getPackageManager()).toString(),
                    it.loadIcon(getPackageManager()),
                    it.packageName,
                    it.sourceDir
            )

            appInfoList.add(info)
        }
        appsLiveData.postValue(appInfoList)
    }

    fun getInstallList(appsLiveData: MutableLiveData<List<AppInfo>>, onlyShowXp: Boolean) {
        val apps: MutableList<AppInfo> = ArrayList()

        val installedApplications: List<ApplicationInfo> = getPackageManager().getInstalledApplications(0)
        for (installedApplication in installedApplications) {
            val file = File(installedApplication.sourceDir)

            if (installedApplication.flags and ApplicationInfo.FLAG_SYSTEM !== 0) continue

            if (!AbiUtils.isSupport(file)) continue

            if (onlyShowXp) {
                if (!BlackBoxCore.get().isXPoesdModule(file)) {
                    continue
                }
            }

            val info = AppInfo(
                    installedApplication.loadLabel(getPackageManager()).toString(),
                    installedApplication.loadIcon(getPackageManager()),
                    installedApplication.packageName,
                    installedApplication.sourceDir
            )

            apps.add(info)
        }

        appsLiveData.postValue(apps)
    }

    fun installApk(source: String, userId: Int, resultLiveData: MutableLiveData<String>) {
        val blackBoxCore = BlackBoxCore.get()
        val installResult = if (URLUtil.isValidUrl(source)) {
            val uri = Uri.parse(source)
            blackBoxCore.installPackageAsUser(uri, userId)
        } else {
            //source == packageName
            blackBoxCore.installPackageAsUser(source, userId)
        }

        if(installResult.success){

            resultLiveData.postValue("安装成功")
        }else{
            resultLiveData.postValue("安装失败:"+installResult.msg)
        }
        println()
        scanUser()
    }

    fun launchApk(packageName: String, userId: Int, launchLiveData: MutableLiveData<Boolean>) {
        val result = BlackBoxCore.get().launchApk(packageName, userId)
        launchLiveData.postValue(result)
    }

    fun unInstall(packageName: String, userID: Int, resultLiveData: MutableLiveData<String>) {
        BlackBoxCore.get().uninstallPackageAsUser(packageName, userID)
        scanUser()
        resultLiveData.postValue("卸载成功")
    }

    private fun scanUser() {
        val blackBoxCore = BlackBoxCore.get()
        val userList = blackBoxCore.users

        if (userList.isEmpty()) {
            return
        }

        val id = userList.last().id

        if (blackBoxCore.getInstalledApplications(0, id).isEmpty()) {
            blackBoxCore.deleteUser(id)
            scanUser()
        }
    }

}