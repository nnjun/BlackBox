package top.niunaijun.blackbox.client.stub;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import androidx.annotation.Nullable;

import top.niunaijun.blackbox.server.ClientServiceManager;

/**
 * Created by Milk on 3/30/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class StubService extends Service {
    public static final String TAG = "StubService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return ClientServiceManager.get().onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ClientServiceManager.get().onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ClientServiceManager.get().onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ClientServiceManager.get().onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ClientServiceManager.get().onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        ClientServiceManager.get().onTrimMemory(level);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        ClientServiceManager.get().onUnbind(intent);
        return false;
    }

    public static class StubServiceP0 extends StubService {

    }

    public static class StubServiceP1 extends StubService {

    }

    public static class StubServiceP2 extends StubService {

    }

    public static class StubServiceP3 extends StubService {

    }

    public static class StubServiceP4 extends StubService {

    }

    public static class StubServiceP5 extends StubService {

    }

    public static class StubServiceP6 extends StubService {

    }

    public static class StubServiceP7 extends StubService {

    }

    public static class StubServiceP8 extends StubService {

    }

    public static class StubServiceP9 extends StubService {

    }

    public static class StubServiceP10 extends StubService {

    }

    public static class StubServiceP11 extends StubService {

    }

    public static class StubServiceP12 extends StubService {

    }

    public static class StubServiceP13 extends StubService {

    }

    public static class StubServiceP14 extends StubService {

    }

    public static class StubServiceP15 extends StubService {

    }

    public static class StubServiceP16 extends StubService {

    }

    public static class StubServiceP17 extends StubService {

    }

    public static class StubServiceP18 extends StubService {

    }

    public static class StubServiceP19 extends StubService {

    }

    public static class StubServiceP20 extends StubService {

    }

    public static class StubServiceP21 extends StubService {

    }

    public static class StubServiceP22 extends StubService {

    }

    public static class StubServiceP23 extends StubService {

    }

    public static class StubServiceP24 extends StubService {

    }

    public static class StubServiceP25 extends StubService {

    }

    public static class StubServiceP26 extends StubService {

    }

    public static class StubServiceP27 extends StubService {

    }

    public static class StubServiceP28 extends StubService {

    }

    public static class StubServiceP29 extends StubService {

    }

    public static class StubServiceP30 extends StubService {

    }

    public static class StubServiceP31 extends StubService {

    }

    public static class StubServiceP32 extends StubService {

    }

    public static class StubServiceP33 extends StubService {

    }

    public static class StubServiceP34 extends StubService {

    }

    public static class StubServiceP35 extends StubService {

    }

    public static class StubServiceP36 extends StubService {

    }

    public static class StubServiceP37 extends StubService {

    }

    public static class StubServiceP38 extends StubService {

    }

    public static class StubServiceP39 extends StubService {

    }

    public static class StubServiceP40 extends StubService {

    }

    public static class StubServiceP41 extends StubService {

    }

    public static class StubServiceP42 extends StubService {

    }

    public static class StubServiceP43 extends StubService {

    }

    public static class StubServiceP44 extends StubService {

    }

    public static class StubServiceP45 extends StubService {

    }

    public static class StubServiceP46 extends StubService {

    }

    public static class StubServiceP47 extends StubService {

    }

    public static class StubServiceP48 extends StubService {

    }

    public static class StubServiceP49 extends StubService {

    }

    public static class StubServiceP50 extends StubService {

    }

    public static class StubServiceP51 extends StubService {

    }

    public static class StubServiceP52 extends StubService {

    }

    public static class StubServiceP53 extends StubService {

    }

    public static class StubServiceP54 extends StubService {

    }

    public static class StubServiceP55 extends StubService {

    }

    public static class StubServiceP56 extends StubService {

    }

    public static class StubServiceP57 extends StubService {

    }

    public static class StubServiceP58 extends StubService {

    }

    public static class StubServiceP59 extends StubService {

    }

    public static class StubServiceP60 extends StubService {

    }

    public static class StubServiceP61 extends StubService {

    }

    public static class StubServiceP62 extends StubService {

    }

    public static class StubServiceP63 extends StubService {

    }

    public static class StubServiceP64 extends StubService {

    }

    public static class StubServiceP65 extends StubService {

    }

    public static class StubServiceP66 extends StubService {

    }

    public static class StubServiceP67 extends StubService {

    }

    public static class StubServiceP68 extends StubService {

    }

    public static class StubServiceP69 extends StubService {

    }

    public static class StubServiceP70 extends StubService {

    }

    public static class StubServiceP71 extends StubService {

    }

    public static class StubServiceP72 extends StubService {

    }

    public static class StubServiceP73 extends StubService {

    }

    public static class StubServiceP74 extends StubService {

    }

    public static class StubServiceP75 extends StubService {

    }

    public static class StubServiceP76 extends StubService {

    }

    public static class StubServiceP77 extends StubService {

    }

    public static class StubServiceP78 extends StubService {

    }

    public static class StubServiceP79 extends StubService {

    }

    public static class StubServiceP80 extends StubService {

    }

    public static class StubServiceP81 extends StubService {

    }

    public static class StubServiceP82 extends StubService {

    }

    public static class StubServiceP83 extends StubService {

    }

    public static class StubServiceP84 extends StubService {

    }

    public static class StubServiceP85 extends StubService {

    }

    public static class StubServiceP86 extends StubService {

    }

    public static class StubServiceP87 extends StubService {

    }

    public static class StubServiceP88 extends StubService {

    }

    public static class StubServiceP89 extends StubService {

    }

    public static class StubServiceP90 extends StubService {

    }

    public static class StubServiceP91 extends StubService {

    }

    public static class StubServiceP92 extends StubService {

    }

    public static class StubServiceP93 extends StubService {

    }

    public static class StubServiceP94 extends StubService {

    }

    public static class StubServiceP95 extends StubService {

    }

    public static class StubServiceP96 extends StubService {

    }

    public static class StubServiceP97 extends StubService {

    }

    public static class StubServiceP98 extends StubService {

    }

    public static class StubServiceP99 extends StubService {

    }
}
