package top.niunaijun.blackbox.util

import top.niunaijun.blackbox.data.AppsRepository
import top.niunaijun.blackbox.view.apps.AppsFactory
import top.niunaijun.blackbox.view.list.ListFactory

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 22:38
 */
object InjectionUtil {

    private val appsRepository = AppsRepository()

    fun getAppsFactory() : AppsFactory{
        return AppsFactory(appsRepository)
    }

    fun getListFactory(): ListFactory{
        return ListFactory(appsRepository)
    }
}