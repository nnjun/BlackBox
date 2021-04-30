package top.niunaijun.blackbox.view.list

import android.view.ViewGroup
import top.niunaijun.blackbox.bean.AppInfo
import top.niunaijun.blackbox.databinding.ItemPackageBinding
import top.niunaijun.blackbox.util.newBindingViewHolder
import top.niunaijun.blackbox.view.base.BaseAdapter

/**
 *
 * @Description: 软件显示界面适配器
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 21:52
 */

class ListAdapter : BaseAdapter<ItemPackageBinding, AppInfo>() {
    override fun getViewBinding(parent: ViewGroup): ItemPackageBinding {
        return newBindingViewHolder(parent,false)

    }

    override fun initView(binding: ItemPackageBinding, position: Int, data: AppInfo) {
        binding.icon.setImageDrawable(data.icon)
        binding.name.text = data.name
        binding.packageName.text = data.packageName

    }
}