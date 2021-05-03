package top.niunaijun.blackboxa.view.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.data.AppsRepository

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 22:36
 */
class ListViewModel(private val repo: AppsRepository,context: Application) : AndroidViewModel(context) {

    val appsLiveData = MutableLiveData<List<AppInfo>>()

    fun previewInstalledList() {
        launchOnUI{
            repo.previewInstallList()
        }
    }

    fun getInstallAppList(){
        launchOnUI {
            repo.getInstalledAppList(appsLiveData)
        }
    }

    fun getInstalledModules(){
        launchOnUI {
            repo.getInstalledModuleList(appsLiveData)
        }
    }


    fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    block()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }

            }
        }
    }
}