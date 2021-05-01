package top.niunaijun.blackbox.view.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.databinding.ActivityMainBinding
import top.niunaijun.blackbox.util.inflate
import top.niunaijun.blackbox.view.apps.AppsFragment
import top.niunaijun.blackbox.view.list.ListActivity


class MainActivity : AppCompatActivity() {

    private val viewBinding: ActivityMainBinding by inflate()

    private lateinit var mViewPagerAdapter: ViewPagerAdapter

    private val fragmentList = mutableListOf<AppsFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        initViewPager()
        initFab()
    }

    private fun initViewPager() {
        val userList = BlackBoxCore.get().users
        userList.forEach {
            fragmentList.add(AppsFragment(it.id))
        }
        fragmentList.add(AppsFragment(userList.size))

        mViewPagerAdapter = ViewPagerAdapter(this)
        mViewPagerAdapter.replaceData(fragmentList)
        viewBinding.viewPager.adapter = mViewPagerAdapter
        viewBinding.dotsIndicator.setViewPager2(viewBinding.viewPager)

    }

    private fun initFab() {
        viewBinding.fab.setOnClickListener {
            val userId = viewBinding.viewPager.currentItem
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("userId", userId)
            apkPathResult.launch(intent)
        }
    }

    fun scanUser() {
        val userList = BlackBoxCore.get().users

        if (fragmentList.size == userList.size) {
            fragmentList.add(AppsFragment(fragmentList.size))
        } else if (fragmentList.size > userList.size + 1) {
            fragmentList.removeLast()

        }

        mViewPagerAdapter.notifyDataSetChanged()

    }

    private val apkPathResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            it.data?.let { data ->
                val userId = data.getIntExtra("userId", 0)
                val packageName = data.getStringExtra("packageName")
                val apkPath = data.getStringExtra("apkPath")

                if (packageName != null) {
                    fragmentList[userId].installExistApk(packageName)
                } else if (apkPath != null) {
                    fragmentList[userId].installApk(apkPath)
                }
            }
        }

    }

}