package top.niunaijun.blackboxa;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import top.niunaijun.blackbox.BlackBoxCore;


/**
 * Created by Milk on 3/28/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class App extends Application {
    public static final String TAG = "Application";
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            BlackBoxCore.get().doAttachBaseContext(base);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BlackBoxCore.get().doCreate();
        BlackBoxCore.get().setExceptionHandler((t, e) -> Log.d(TAG, "uncaughtException :" + Log.getStackTraceString(e)));
    }
}
