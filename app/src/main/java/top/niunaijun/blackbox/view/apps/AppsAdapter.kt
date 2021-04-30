package top.niunaijun.blackbox.view.apps

import android.view.ViewGroup
import top.niunaijun.blackbox.bean.AppInfo
import top.niunaijun.blackbox.databinding.ItemAppBinding
import top.niunaijun.blackbox.util.inflateBinding
import top.niunaijun.blackbox.view.base.BaseAdapter

/**
 *
 * @Description: 软件显示界面适配器
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 21:52
 */

class AppsAdapter : BaseAdapter<ItemAppBinding, AppInfo>() {
    override fun getViewBinding(parent: ViewGroup): ItemAppBinding {
        return inflateBinding(getLayoutInflater(parent))

    }

    override fun initView(binding: ItemAppBinding, position: Int, data: AppInfo) {
        binding.icon.setImageDrawable(data.icon)
        binding.name.text = data.name

    }
}