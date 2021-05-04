package top.niunaijun.blackbox.client.hook.delegate;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.util.Log;

import com.swift.sandhook.xposedcompat.XposedCompat;

import java.lang.reflect.Field;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import mirror.android.app.ActivityThread;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.BClient;
import top.niunaijun.blackbox.client.hook.HookManager;
import top.niunaijun.blackbox.client.hook.IInjectHook;
import top.niunaijun.blackbox.client.hook.fixer.ActivityFixer;
import top.niunaijun.blackbox.client.hook.fixer.ContextFixer;
import top.niunaijun.blackbox.client.hook.proxies.app.HCallbackStub;
import top.niunaijun.blackbox.utils.HackAppUtils;

/**
 * @author Lody
 */
public final class AppInstrumentation extends InstrumentationDelegate implements IInjectHook {

    private static final String TAG = AppInstrumentation.class.getSimpleName();

    private static AppInstrumentation gDefault;

    public static AppInstrumentation get() {
        if (gDefault == null) {
            synchronized (AppInstrumentation.class) {
                if (gDefault == null) {
                    gDefault = new AppInstrumentation();
                }
            }
        }
        return gDefault;
    }

    public AppInstrumentation() {
    }

    @Override
    public void injectHook() {
        try {
            Instrumentation mInstrumentation = getCurrInstrumentation();
            if (mInstrumentation == this || checkInstrumentation(mInstrumentation))
                return;
            base = (Instrumentation) mInstrumentation;
            root = (Instrumentation) mInstrumentation;
            ActivityThread.mInstrumentation.set(BlackBoxCore.mainThread(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Instrumentation getCurrInstrumentation() {
        Object currentActivityThread = BlackBoxCore.mainThread();
        return ActivityThread.mInstrumentation.get(currentActivityThread);
    }

    @Override
    public boolean isBadEnv() {
        return !checkInstrumentation(getCurrInstrumentation());
    }

    private boolean checkInstrumentation(Instrumentation instrumentation) {
        if (instrumentation instanceof AppInstrumentation) {
            return true;
        }
        Class<?> clazz = instrumentation.getClass();
        if (Instrumentation.class.equals(clazz)) {
            return false;
        }
        do {
            Field[] fields = clazz.getDeclaredFields();
            if (fields != null) {
                for (Field field : fields) {
                    if (Instrumentation.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        Object obj;
                        try {
                            obj = field.get(instrumentation);
                        } catch (IllegalAccessException e) {
                            return false;
                        }
                        if ((obj instanceof AppInstrumentation)) {
                            return true;
                        }
                    }
                }
            }
            clazz = clazz.getSuperclass();
        } while (!Instrumentation.class.equals(clazz));
        return false;
    }

    private void checkActivityCallback() {
        HookManager.get().checkEnv(HCallbackStub.class);
    }

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        ContextFixer.fix(context);
        BClient.getClient().loadXposed(context);
        return super.newApplication(cl, className, context);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        HackAppUtils.enableQQLogOutput(activity.getPackageName(), activity.getClassLoader());
        checkActivityCallback();
        Log.d(TAG, "callActivityOnCreate: " + activity.getClass().getName());
        ActivityInfo info = mirror.android.app.Activity.mActivityInfo.get(activity);
        ContextFixer.fix(activity);
        ActivityFixer.fix(activity);
        if (info.theme != 0) {
            activity.setTheme(info.theme);
        }
        activity.setRequestedOrientation(info.screenOrientation);
        super.callActivityOnCreate(activity, icicle);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        super.callActivityOnResume(activity);
    }

    private boolean isOrientationLandscape(int requestedOrientation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                    || (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
                    || (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
                    || (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        } else {
            return (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                    || (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
                    || (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        super.callActivityOnDestroy(activity);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        super.callActivityOnPause(activity);
    }


    @Override
    public void callApplicationOnCreate(Application app) {
        checkActivityCallback();
        super.callApplicationOnCreate(app);
    }

    @Override
    public ActivityResult execStartActivity(Context context, IBinder iBinder, IBinder iBinder2, Activity activity, Intent intent, int i, Bundle bundle) throws Throwable {
        return super.execStartActivity(context, iBinder, iBinder2, activity, intent, i, bundle);
    }

    @Override
    public ActivityResult execStartActivity(Context context, IBinder iBinder, IBinder iBinder2, String str, Intent intent, int i, Bundle bundle) throws Throwable {
        return super.execStartActivity(context, iBinder, iBinder2, str, intent, i, bundle);
    }

    @Override
    public ActivityResult execStartActivity(Context context, IBinder iBinder, IBinder iBinder2, Fragment fragment, Intent intent, int i) throws Throwable {
        return super.execStartActivity(context, iBinder, iBinder2, fragment, intent, i);
    }

    @Override
    public ActivityResult execStartActivity(Context context, IBinder iBinder, IBinder iBinder2, Activity activity, Intent intent, int i) throws Throwable {
        return super.execStartActivity(context, iBinder, iBinder2, activity, intent, i);
    }

    @Override
    public ActivityResult execStartActivity(Context context, IBinder iBinder, IBinder iBinder2, Fragment fragment, Intent intent, int i, Bundle bundle) throws Throwable {
        return super.execStartActivity(context, iBinder, iBinder2, fragment, intent, i, bundle);
    }

    @Override
    public ActivityResult execStartActivity(Context context, IBinder iBinder, IBinder iBinder2, Activity activity, Intent intent, int i, Bundle bundle, UserHandle userHandle) throws Throwable {
        return super.execStartActivity(context, iBinder, iBinder2, activity, intent, i, bundle, userHandle);
    }

    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            return super.newActivity(cl, className, intent);
        } catch (ClassNotFoundException e) {
            return root.newActivity(cl, className, intent);
        }
    }

    public void setupXposed(Context context, ClassLoader classLoader, String processName) {
        if (processName == null)
            return;
        XposedCompat.context = context;
        XposedCompat.classLoader = classLoader;
        XposedCompat.isFirstApplication = processName.equals(context.getPackageName());
        XC_LoadPackage.LoadPackageParam packageParam = new XC_LoadPackage.LoadPackageParam(new XposedBridge.CopyOnWriteSortedSet<XC_LoadPackage>());
        packageParam.appInfo = context.getApplicationInfo();
        packageParam.classLoader = classLoader;
        packageParam.packageName = context.getPackageName();
        packageParam.isFirstApplication = processName.equals(context.getPackageName());
        packageParam.processName = processName;

        try {
//            XposedHelpers.findAndHookMethod(classLoader.loadClass("android.provider.Settings$NameValueCache"), "getStringForUser" , ContentResolver.class,
//                    String.class,
//                    int.class,
//                    new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            super.beforeHookedMethod(param);
//                        }
//
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            super.afterHookedMethod(param);
//                        }
//                    });
//            XposedHelpers.findAndHookMethod(context.getClassLoader().loadClass("com.netease.framework.log.NTLog"), "d", String.class, String.class,
//                    new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            super.beforeHookedMethod(param);
//                            String s = (String) param.args[1];
//                            if ("initModuleFactory".equals(s)) {
//                                Log.d(TAG, "beforeHookedMethod: ");
//                            }
//                            Log.d((String) param.args[0], (String) param.args[1]);
//                        }
//
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            super.afterHookedMethod(param);
//                            String s = (String) param.args[1];
//                            if ("initModuleFactory".equals(s)) {
//                                Log.d(TAG, "beforeHookedMethod: ");
//                            }
//                        }
//                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
