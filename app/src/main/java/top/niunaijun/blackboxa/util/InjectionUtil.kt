package top.niunaijun.blackboxa.util

import top.niunaijun.blackboxa.data.AppsRepository
import top.niunaijun.blackboxa.view.apps.AppsFactory
import top.niunaijun.blackboxa.view.list.ListFactory

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 22:38
 */
object InjectionUtil {

    private val appsRepository = AppsRepository()

    fun getAppsFactory() : AppsFactory {
        return AppsFactory(appsRepository)
    }

    fun getListFactory(): ListFactory {
        return ListFactory(appsRepository)
    }
}