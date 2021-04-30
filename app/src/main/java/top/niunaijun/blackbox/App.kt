package top.niunaijun.blackbox

import android.app.Application
import android.content.Context

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
            context = base!!
            BlackBoxCore.get().doAttachBaseContext(base)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate() {
        super.onCreate()
        BlackBoxCore.get().doCreate()
    }
}