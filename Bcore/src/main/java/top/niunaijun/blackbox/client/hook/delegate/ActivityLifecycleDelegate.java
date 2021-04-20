package top.niunaijun.blackbox.client.hook.delegate;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.IBinder;

import top.niunaijun.blackbox.BlackBoxCore;

/**
 * Created by Milk on 4/11/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ActivityLifecycleDelegate implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    private IBinder toToken(Activity activity) {
        return mirror.android.app.Activity.mToken.get(activity);
    }
}
