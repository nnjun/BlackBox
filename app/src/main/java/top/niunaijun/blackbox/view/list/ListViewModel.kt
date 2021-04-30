package top.niunaijun.blackbox.view.list

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.bean.AppInfo
import top.niunaijun.blackbox.data.AppsRepository
import top.niunaijun.blackbox.view.base.BaseViewModel
import java.io.InputStream

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 22:36
 */
class ListViewModel(private val repo: AppsRepository) : BaseViewModel() {

    val appsLiveData = MutableLiveData<List<AppInfo>>()

    val copyFileLiveData = MutableLiveData<String>()

    fun getInstalledApps() {
        launchOnUI {
            repo.getInstallList(appsLiveData)
        }
    }

    fun copyFile(uri: Uri){
        launchOnUI {
            repo.copyFile(uri,copyFileLiveData)
        }
    }
}