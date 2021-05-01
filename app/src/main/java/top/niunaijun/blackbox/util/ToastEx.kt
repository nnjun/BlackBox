package top.niunaijun.blackbox.util

import android.content.Context
import android.widget.Toast

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/5/2 0:13
 */

fun Context.toast(msg:String){
    Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
}