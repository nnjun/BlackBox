package top.niunaijun.blackboxa

import android.app.Application
import android.content.Context
import com.umeng.commonsdk.UMConfigure
import com.umeng.commonsdk.statistics.common.DeviceTypeEnum
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.client.ClientConfiguration

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 21:21
 */
class App : Application() {

    companion object{
        private lateinit var context: Context

        fun getInstance(): Context {
            return context
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        try {
            BlackBoxCore.get().doAttachBaseContext(base, object : ClientConfiguration() {
                override fun getHostPackageName(): String {
                    return base!!.packageName
                }

                override fun isHideRoot(): Boolean {
                    return true
                }

                override fun isHideXposed(): Boolean {
                    return true
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate() {
        super.onCreate()
//        UMConfigure.init(this, "60914c9b53b6726499e973ac", "default", UMConfigure.DEVICE_TYPE_PHONE, null)
        BlackBoxCore.get().doCreate()
        context = this
    }
}