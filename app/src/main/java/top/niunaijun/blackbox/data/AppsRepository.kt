package top.niunaijun.blackbox.data

import android.content.pm.ApplicationInfo
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.App
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.BlackBoxCore.getPackageManager
import top.niunaijun.blackbox.bean.AppInfo
import top.niunaijun.blackbox.utils.AbiUtils
import top.niunaijun.blackbox.utils.FileUtils
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

    fun getInstallList(appsLiveData: MutableLiveData<List<AppInfo>>) {
        val apps: MutableList<AppInfo> = ArrayList()

        val installedApplications: List<ApplicationInfo> = getPackageManager().getInstalledApplications(0)
        for (installedApplication in installedApplications) {
            if (installedApplication.flags and ApplicationInfo.FLAG_SYSTEM !== 0) continue

            if (!AbiUtils.isSupport(File(installedApplication.sourceDir))) continue

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

    fun copyFile(uri: Uri, copyFileLiveData: MutableLiveData<String>) {
        val inputStream = App.getInstance().contentResolver.openInputStream(uri)
        if (inputStream != null) {
            val fileName = File(App.getInstance().externalCacheDir, "/tmp_" + System.nanoTime() + ".apk")
            FileUtils.writeToFile(inputStream, fileName)
            inputStream.close()
            copyFileLiveData.postValue(fileName.toString())
        } else {
            copyFileLiveData.postValue(null)
        }
    }


    fun installApk(apk: File, userId: Int, resultLiveData: MutableLiveData<Boolean>) {
        val result = BlackBoxCore.get().installPackageAsUser(apk, userId)
        apk.delete()
        scanUser()
        resultLiveData.postValue(result.success)
    }

    fun launchApk(packageName: String, userId: Int, launchLiveData: MutableLiveData<Boolean>) {
        val result = BlackBoxCore.get().launchApk(packageName, userId)
        launchLiveData.postValue(result)
    }

    fun unInstall(packageName: String, userID: Int, resultLiveData: MutableLiveData<Boolean>) {
        BlackBoxCore.get().uninstallPackagesAsUser(packageName, userID)
        scanUser()
        resultLiveData.postValue(true)
    }

    private fun scanUser() {
        val blackBoxCore = BlackBoxCore.get()
        val id = blackBoxCore.users.last().id
        if (blackBoxCore.getInstalledApplications(0, id).isEmpty()) {
            blackBoxCore.deleteUser(id)
            scanUser()
        }
    }

}