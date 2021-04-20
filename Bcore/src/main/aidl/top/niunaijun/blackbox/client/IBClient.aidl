// IVClient.aidl
package top.niunaijun.blackbox.client;

// Declare any non-default types here with import statements

import android.os.IBinder;
import android.content.ComponentName;
import android.content.Intent;
import java.util.List;
import android.content.pm.ResolveInfo;

interface IBClient {
    IBinder getActivityThread();
    void stopService(in ComponentName componentName);
    void restartJobService(String selfId);
    IBinder acquireContentProviderClient(in ProviderInfo providerInfo);
//    void registerReceiver(in Intent intent, in ActivityInfo activityList);

    IBinder peekService(in Intent intent);
    void finishActivity(IBinder token);
    void handleNewIntent(IBinder token, in Intent intent);
}
