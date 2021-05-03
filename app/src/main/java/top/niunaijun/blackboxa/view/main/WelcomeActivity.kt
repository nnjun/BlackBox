package top.niunaijun.blackboxa.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.niunaijun.blackboxa.R
import kotlin.concurrent.thread

class WelcomeActivity : AppCompatActivity() {

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        jump()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jump()
    }

    private fun jump() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}