package top.niunaijun.blackboxa.view.main

import android.app.Application
import android.content.Context
import android.util.Log
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.client.ClientConfiguration
import top.niunaijun.blackbox.client.hook.AppLifecycleCallback
import top.niunaijun.blackboxa.App

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/5/6 23:38
 */
class BlackBoxLoadCallback {

    fun attachBaseContext(context: Context){

        val sharedPreferences = context.getSharedPreferences(context.packageName+"_preferences",Context.MODE_PRIVATE)

        val isHideRoot = sharedPreferences.getBoolean("root_hide",false)
        val isHideXp = sharedPreferences.getBoolean("xp_hide",false)

        BlackBoxCore.get().doAttachBaseContext(context, object : ClientConfiguration() {
            override fun getHostPackageName(): String {
                return context.packageName
            }

            override fun isHideRoot(): Boolean {
                return isHideRoot
            }

            override fun isHideXposed(): Boolean {
                return isHideXp
            }
        })
    }


    fun addLifecycleCallback(){
        BlackBoxCore.get().appLifecycleCallback = object : AppLifecycleCallback() {
            override fun beforeCreateApplication(packageName: String?, processName: String?, context: Context?) {
                Log.d(App.TAG, "beforeCreateApplication: pkg $packageName, processName $processName")
            }

            override fun beforeApplicationOnCreate(packageName: String?, processName: String?, application: Application?) {
                Log.d(App.TAG, "beforeApplicationOnCreate: pkg $packageName, processName $processName")
            }

            override fun afterApplicationOnCreate(packageName: String?, processName: String?, application: Application?) {
                Log.d(App.TAG, "afterApplicationOnCreate: pkg $packageName, processName $processName")
            }
        }

    }

}