package top.niunaijun.blackboxa.view.setting

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.utils.compat.BuildCompat
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.util.toast
import top.niunaijun.blackboxa.view.xp.XpActivity

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/5/6 22:13
 */
class SettingFragment : PreferenceFragmentCompat() {

    private lateinit var xpEnable: SwitchPreferenceCompat

    private lateinit var xpModule: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)

        xpEnable = findPreference("xp_enable")!!
        xpEnable.isChecked = BlackBoxCore.get().isXPEnable

        xpEnable.setOnPreferenceChangeListener { _, newValue ->
            if (BuildCompat.isR()) {
                requireContext().toast("Android11及以上暂不支持开启Xpoesd框架")
                BlackBoxCore.get().isXPEnable = false
                xpEnable.isChecked = false
            } else {
                BlackBoxCore.get().isXPEnable = equals(newValue)
            }
            true
        }


        //xp模块跳转
        xpModule = findPreference("xp_module")!!
        xpModule.setOnPreferenceClickListener {
            val intent = Intent(requireActivity(), XpActivity::class.java)
            requireContext().startActivity(intent)
            true
        }
    }
}