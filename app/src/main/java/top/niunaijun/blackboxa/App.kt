package top.niunaijun.blackboxa

import android.app.Application
import android.content.Context
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.utils.compat.BuildCompat
import top.niunaijun.blackboxa.view.main.BlackBoxLoadCallback

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 21:21
 */
class App : Application() {

    companion object {
        private lateinit var context: Context

        fun getInstance(): Context {
            return context
        }

        const val TAG = "BApplication"
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        context = base!!

        try {

            val callback = BlackBoxLoadCallback()
            callback.attachBaseContext(context)
            callback.addLifecycleCallback()

            if(BuildCompat.isR()){
                BlackBoxCore.get().isXPEnable = false
            }
            //android 11 先屏蔽xp

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate() {
        super.onCreate()
//        UMConfigure.init(this,,BuildConfig.FLAVOR,0,String pushSecret);
        BlackBoxCore.get().doCreate()
    }
}