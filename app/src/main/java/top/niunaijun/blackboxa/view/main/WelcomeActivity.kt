package top.niunaijun.blackboxa.view.main

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.niunaijun.blackboxa.App
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.util.InjectionUtil
import top.niunaijun.blackboxa.view.list.ListViewModel
import kotlin.concurrent.thread

class WelcomeActivity : AppCompatActivity() {

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        jump()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previewInstalledAppList()
        jump()
    }

    private fun jump() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun previewInstalledAppList(){
        val viewModel = ViewModelProvider(this,InjectionUtil.getListFactory()).get(ListViewModel::class.java)
        viewModel.previewInstalledList()
    }
}