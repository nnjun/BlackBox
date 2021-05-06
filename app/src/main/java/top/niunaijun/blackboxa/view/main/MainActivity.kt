package top.niunaijun.blackboxa.view.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.databinding.ActivityMainBinding
import top.niunaijun.blackboxa.util.inflate
import top.niunaijun.blackboxa.view.apps.AppsFragment
import top.niunaijun.blackboxa.view.base.BaseActivity
import top.niunaijun.blackboxa.view.list.ListActivity
import top.niunaijun.blackboxa.view.setting.SettingActivity
import top.niunaijun.blackboxa.view.xp.XpActivity


class MainActivity : BaseActivity() {

    private val viewBinding: ActivityMainBinding by inflate()

    private lateinit var mViewPagerAdapter: ViewPagerAdapter

    private val fragmentList = mutableListOf<AppsFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        initToolbar(viewBinding.toolbarLayout.toolbar,R.string.app_name)
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
                val source = data.getStringExtra("source")
                if (source != null) {
                    fragmentList[userId].installApk(source)
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.main_git -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nnjun/BlackBox"))
                startActivity(intent)
            }

            R.id.main_setting->{
                val intent = Intent(this,SettingActivity::class.java)
                startActivity(intent)
            }

            R.id.main_qq -> {
                val intent = Intent()
                intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3DGZQZdUkdM3yv2-VqK-XW8eB6U0Wd5fBX")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return true
    }

}