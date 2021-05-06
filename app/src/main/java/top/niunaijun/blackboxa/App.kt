package top.niunaijun.blackboxa

import android.app.Application
import android.content.Context
import android.util.Log
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.client.ClientConfiguration
import top.niunaijun.blackbox.client.hook.AppLifecycleCallback

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

        const val TAG = "BApplication"
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
            BlackBoxCore.get().appLifecycleCallback = object : AppLifecycleCallback() {
                override fun beforeCreateApplication(packageName: String?, processName: String?, context: Context?) {
                    Log.d(Companion.TAG, "beforeCreateApplication: pkg $packageName, processName $processName")
                }

                override fun beforeApplicationOnCreate(packageName: String?, processName: String?, application: Application?) {
                    Log.d(Companion.TAG, "beforeApplicationOnCreate: pkg $packageName, processName $processName")
                }

                override fun afterApplicationOnCreate(packageName: String?, processName: String?, application: Application?) {
                    Log.d(Companion.TAG, "afterApplicationOnCreate: pkg $packageName, processName $processName")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate() {
        super.onCreate()
        BlackBoxCore.get().doCreate()
        context = this
    }
}