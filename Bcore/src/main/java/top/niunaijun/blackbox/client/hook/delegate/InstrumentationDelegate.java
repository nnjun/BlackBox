package top.niunaijun.blackbox.client.hook.delegate;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Lody
 */
public class InstrumentationDelegate extends Instrumentation {

    protected Instrumentation base;
    protected Instrumentation root;

    @Override
    public void onCreate(Bundle arguments) {
        root.onCreate(arguments);
    }

    @Override
    public void start() {
        root.start();
    }

    @Override
    public void onStart() {
        root.onStart();
    }

    @Override
    public boolean onException(Object obj, Throwable e) {
        return root.onException(obj, e);
    }

    @Override
    public void sendStatus(int resultCode, Bundle results) {
        root.sendStatus(resultCode, results);
    }

    @Override
    public void finish(int resultCode, Bundle results) {
        root.finish(resultCode, results);
    }

    @Override
    public void setAutomaticPerformanceSnapshots() {
        root.setAutomaticPerformanceSnapshots();
    }

    @Override
    public void startPerformanceSnapshot() {
        root.startPerformanceSnapshot();
    }

    @Override
    public void endPerformanceSnapshot() {
        root.endPerformanceSnapshot();
    }

    @Override
    public void onDestroy() {
        root.onDestroy();
    }

    @Override
    public Context getContext() {
        return root.getContext();
    }

    @Override
    public ComponentName getComponentName() {
        return root.getComponentName();
    }

    @Override
    public Context getTargetContext() {
        return root.getTargetContext();
    }

    @Override
    public boolean isProfiling() {
        return root.isProfiling();
    }

    @Override
    public void startProfiling() {
        root.startProfiling();
    }

    @Override
    public void stopProfiling() {
        root.stopProfiling();
    }

    @Override
    public void setInTouchMode(boolean inTouch) {
        root.setInTouchMode(inTouch);
    }

    @Override
    public void waitForIdle(Runnable recipient) {
        root.waitForIdle(recipient);
    }

    @Override
    public void waitForIdleSync() {
        root.waitForIdleSync();
    }

    @Override
    public void runOnMainSync(Runnable runner) {
        root.runOnMainSync(runner);
    }

    @Override
    public Activity startActivitySync(Intent intent) {
        return root.startActivitySync(intent);
    }

    @Override
    public void addMonitor(ActivityMonitor monitor) {
        root.addMonitor(monitor);
    }

    @Override
    public ActivityMonitor addMonitor(IntentFilter filter, ActivityResult result, boolean block) {
        return root.addMonitor(filter, result, block);
    }

    @Override
    public ActivityMonitor addMonitor(String cls, ActivityResult result, boolean block) {
        return root.addMonitor(cls, result, block);
    }

    @Override
    public boolean checkMonitorHit(ActivityMonitor monitor, int minHits) {
        return root.checkMonitorHit(monitor, minHits);
    }

    @Override
    public Activity waitForMonitor(ActivityMonitor monitor) {
        return root.waitForMonitor(monitor);
    }

    @Override
    public Activity waitForMonitorWithTimeout(ActivityMonitor monitor, long timeOut) {
        return root.waitForMonitorWithTimeout(monitor, timeOut);
    }

    @Override
    public void removeMonitor(ActivityMonitor monitor) {
        root.removeMonitor(monitor);
    }

    @Override
    public boolean invokeMenuActionSync(Activity targetActivity, int id, int flag) {
        return root.invokeMenuActionSync(targetActivity, id, flag);
    }

    @Override
    public boolean invokeContextMenuAction(Activity targetActivity, int id, int flag) {
        return root.invokeContextMenuAction(targetActivity, id, flag);
    }

    @Override
    public void sendStringSync(String text) {
        root.sendStringSync(text);
    }

    @Override
    public void sendKeySync(KeyEvent event) {
        root.sendKeySync(event);
    }

    @Override
    public void sendKeyDownUpSync(int key) {
        root.sendKeyDownUpSync(key);
    }

    @Override
    public void sendCharacterSync(int keyCode) {
        root.sendCharacterSync(keyCode);
    }

    @Override
    public void sendPointerSync(MotionEvent event) {
        root.sendPointerSync(event);
    }

    @Override
    public void sendTrackballEventSync(MotionEvent event) {
        root.sendTrackballEventSync(event);
    }

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return base.newApplication(cl, className, context);
    }

    @Override
    public void callApplicationOnCreate(Application app) {
        base.callApplicationOnCreate(app);
    }

    @Override
    public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application, Intent intent,
                                ActivityInfo info, CharSequence title, Activity parent, String id, Object lastNonConfigurationInstance)
            throws InstantiationException, IllegalAccessException {
        return base.newActivity(clazz, context, token, application, intent, info, title, parent, id,
                lastNonConfigurationInstance);
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return base.newActivity(cl, className, intent);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        base.callActivityOnCreate(activity, icicle);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        base.callActivityOnCreate(activity, icicle, persistentState);
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        base.callActivityOnDestroy(activity);
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        base.callActivityOnRestoreInstanceState(activity, savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState,
                                                   PersistableBundle persistentState) {
        base.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState);
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle) {
        base.callActivityOnPostCreate(activity, icicle);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        base.callActivityOnPostCreate(activity, icicle, persistentState);
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        base.callActivityOnNewIntent(activity, intent);
    }


    @Override
    public void callActivityOnStart(Activity activity) {
        base.callActivityOnStart(activity);
    }

    @Override
    public void callActivityOnRestart(Activity activity) {
        base.callActivityOnRestart(activity);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
         base.callActivityOnResume(activity);
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        base.callActivityOnStop(activity);
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
        base.callActivityOnSaveInstanceState(activity, outState);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState,
                                                PersistableBundle outPersistentState) {
        base.callActivityOnSaveInstanceState(activity, outState, outPersistentState);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        base.callActivityOnPause(activity);
    }

    @Override
    public void callActivityOnUserLeaving(Activity activity) {
        base.callActivityOnUserLeaving(activity);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public UiAutomation getUiAutomation() {
        return root.getUiAutomation();
    }

    public ActivityResult execStartActivity(Context context, IBinder iBinder, IBinder iBinder2, Activity activity, Intent intent, int i, Bundle bundle) throws Throwable {
        return (ActivityResult) findDeclaredMethod(base, "execStartActivity", Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, Integer.TYPE, Bundle.class).invoke(base, new Object[]{context, iBinder, iBinder2, activity, intent, i, bundle});
    }

    public ActivityResult execStartActivity(Context context, IBinder iBinder, IBinder iBinder2, String str, Intent intent, int i, Bundle bundle) throws Throwable {
         return (ActivityResult) findDeclaredMethod(base, "execStartActivity", Context.class, IBinder.class, IBinder.class, String.class, Intent.class, Integer.TYPE, Bundle.class).invoke(base, new Object[]{context, iBinder, iBinder2, str, intent, i, bundle});
    }

    public ActivityResult execStartActivity(Context context, IBinder iBinder, IBinder iBinder2, Fragment fragment, Intent intent, int i) throws Throwable {
        return (ActivityResult) findDeclaredMethod(base, "execStartActivity", Context.class, IBinder.class, IBinder.class, Fragment.class, Intent.class, Integer.TYPE).invoke(base, new Object[]{context, iBinder, iBinder2, fragment, intent, i});
    }

    public ActivityResult execStartActivity(Context context, IBinder iBinder, IBinder iBinder2, Activity activity, Intent intent, int i) throws Throwable {
        return (ActivityResult) findDeclaredMethod(base, "execStartActivity", Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, Integer.TYPE).invoke(base, new Object[]{context, iBinder, iBinder2, activity, intent, i});
    }

    public ActivityResult execStartActivity(Context context, IBinder iBinder, IBinder iBinder2, Fragment fragment, Intent intent, int i, Bundle bundle) throws Throwable {
        return (ActivityResult) findDeclaredMethod(base, "execStartActivity", Context.class, IBinder.class, IBinder.class, Fragment.class, Intent.class, Integer.TYPE, Bundle.class).invoke(base, new Object[]{context, iBinder, iBinder2, fragment, intent, i, bundle});
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public ActivityResult execStartActivity(Context context, IBinder iBinder, IBinder iBinder2, Activity activity, Intent intent, int i, Bundle bundle, UserHandle userHandle) throws Throwable {
        return (ActivityResult) findDeclaredMethod(base, "execStartActivity", Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, Integer.TYPE, Bundle.class, UserHandle.class).invoke(base, new Object[]{context, iBinder, iBinder2, activity, intent, i, bundle, userHandle});
    }

    private static Method findDeclaredMethod(Object obj, String name, Class<?>... args) throws NoSuchMethodException {
        Class<?> cls = obj.getClass();
        while (cls != null) {
            try {
                Method method = cls.getDeclaredMethod(name, args);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                return method;
            } catch (NoSuchMethodException e) {
                cls = cls.getSuperclass();
            }
        }
        throw new NoSuchMethodException("Method " + name + " with parameters " + Arrays.asList(args) + " not found in " + obj.getClass());
    }
}
