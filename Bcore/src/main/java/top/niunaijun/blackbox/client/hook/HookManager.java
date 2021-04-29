package top.niunaijun.blackbox.client.hook;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import top.niunaijun.blackbox.client.hook.proxies.accessibility.AccessibilityManagerStub;
import top.niunaijun.blackbox.client.hook.proxies.am.ActivityManagerStub;
import top.niunaijun.blackbox.client.hook.proxies.am.ActivityTaskManagerStub;
import top.niunaijun.blackbox.client.hook.proxies.app.AlarmManagerStub;
import top.niunaijun.blackbox.client.hook.proxies.app.HCallbackStub;
import top.niunaijun.blackbox.client.hook.proxies.app.JobServiceStub;
import top.niunaijun.blackbox.client.hook.proxies.app.NotificationManagerStub;
import top.niunaijun.blackbox.client.hook.proxies.appos.AppOpsManagerStub;
import top.niunaijun.blackbox.client.hook.proxies.context.ContentServiceStub;
import top.niunaijun.blackbox.client.hook.proxies.context.RestrictionsManagerStub;
import top.niunaijun.blackbox.client.hook.proxies.display.DisplayManagerStub;
import top.niunaijun.blackbox.client.hook.proxies.os.DeviceIdentifiersPolicyStub;
import top.niunaijun.blackbox.client.hook.proxies.os.UserManagerStub;
import top.niunaijun.blackbox.client.hook.proxies.os.storage.StorageManagerStub;
import top.niunaijun.blackbox.client.hook.proxies.pm.LauncherAppsStub;
import top.niunaijun.blackbox.client.hook.proxies.pm.PackageManagerStub;
import top.niunaijun.blackbox.client.hook.proxies.pm.ShortcutManagerStub;
import top.niunaijun.blackbox.client.hook.proxies.view.AutofillManagerStub;
import top.niunaijun.blackbox.client.hook.proxies.view.GraphicsStatsStub;
import top.niunaijun.blackbox.client.hook.proxies.vm.WindowManagerStub;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.hook.delegate.AppInstrumentation;
import top.niunaijun.blackbox.client.hook.proxies.appwidget.AppWidgetManagerStub;
import top.niunaijun.blackbox.client.hook.proxies.libcore.OsStub;
import top.niunaijun.blackbox.client.hook.proxies.location.LocationManagerStub;
import top.niunaijun.blackbox.client.hook.proxies.session.MediaSessionManagerStub;
import top.niunaijun.blackbox.client.hook.proxies.telephony.TelephonyManagerStub;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Created by Milk on 3/30/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class HookManager {
    public static final String TAG = "HookManager";

    private static HookManager sHookManager = new HookManager();

    private Map<Class<?>, IInjectHook> mInjectors = new HashMap<>();

    public static HookManager get() {
        return sHookManager;
    }

    public void init() {
        if (BlackBoxCore.get().isVirtualProcess()) {
            addInjector(new DisplayManagerStub());
            addInjector(new OsStub());
            addInjector(new ActivityManagerStub());
            addInjector(new PackageManagerStub());
            addInjector(new TelephonyManagerStub());
            addInjector(new HCallbackStub());
            addInjector(new AppOpsManagerStub());
            addInjector(new NotificationManagerStub());
            addInjector(new AlarmManagerStub());
            addInjector(new AppWidgetManagerStub());
            addInjector(new ContentServiceStub());
            addInjector(new WindowManagerStub());
            addInjector(new UserManagerStub());
            addInjector(new RestrictionsManagerStub());
            addInjector(new MediaSessionManagerStub());
            addInjector(new LocationManagerStub());
            addInjector(new StorageManagerStub());
            addInjector(new LauncherAppsStub());
            addInjector(new JobServiceStub());
            addInjector(new AccessibilityManagerStub());

            addInjector(AppInstrumentation.get());

            // 11.0
            if (BuildCompat.isR()) {
                addInjector(new ActivityTaskManagerStub());
            }
            // 10.0
            if (BuildCompat.isQ()) {
                addInjector(new ActivityTaskManagerStub());
            }
            // 9.0
            if (BuildCompat.isPie()) {
            }
            // 8.0
            if (BuildCompat.isOreo()) {
                addInjector(new AutofillManagerStub());
                addInjector(new DeviceIdentifiersPolicyStub());
            }
            // 7.1
            if (BuildCompat.isN_MR1()) {
                addInjector(new ShortcutManagerStub());
            }
            // 7.0
            if (BuildCompat.isN()) {
            }
            // 6.0
            if (BuildCompat.isM()) {
                addInjector(new GraphicsStatsStub());
            }
            // 5.0
            if (BuildCompat.isL()) {
                addInjector(new JobServiceStub());
            }
        }
        injectAll();
    }

    public void checkEnv(Class<?> clazz) {
        IInjectHook iInjectHook = mInjectors.get(clazz);
        if (iInjectHook != null && iInjectHook.isBadEnv()) {
            Log.d(TAG, "checkEnv: " + clazz.getSimpleName() + " is bad env");
            iInjectHook.injectHook();
        }
    }

    void addInjector(IInjectHook injectHook) {
        mInjectors.put(injectHook.getClass(), injectHook);
    }

    void injectAll() {
        for (IInjectHook value : mInjectors.values()) {
            value.injectHook();
        }
    }
}
