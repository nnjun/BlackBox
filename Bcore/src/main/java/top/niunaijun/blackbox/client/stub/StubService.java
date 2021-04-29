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

    public static class P0 extends StubService {

    }

    public static class P1 extends StubService {

    }

    public static class P2 extends StubService {

    }

    public static class P3 extends StubService {

    }

    public static class P4 extends StubService {

    }

    public static class P5 extends StubService {

    }

    public static class P6 extends StubService {

    }

    public static class P7 extends StubService {

    }

    public static class P8 extends StubService {

    }

    public static class P9 extends StubService {

    }

    public static class P10 extends StubService {

    }

    public static class P11 extends StubService {

    }

    public static class P12 extends StubService {

    }

    public static class P13 extends StubService {

    }

    public static class P14 extends StubService {

    }

    public static class P15 extends StubService {

    }

    public static class P16 extends StubService {

    }

    public static class P17 extends StubService {

    }

    public static class P18 extends StubService {

    }

    public static class P19 extends StubService {

    }

    public static class P20 extends StubService {

    }

    public static class P21 extends StubService {

    }

    public static class P22 extends StubService {

    }

    public static class P23 extends StubService {

    }

    public static class P24 extends StubService {

    }

    public static class P25 extends StubService {

    }

    public static class P26 extends StubService {

    }

    public static class P27 extends StubService {

    }

    public static class P28 extends StubService {

    }

    public static class P29 extends StubService {

    }

    public static class P30 extends StubService {

    }

    public static class P31 extends StubService {

    }

    public static class P32 extends StubService {

    }

    public static class P33 extends StubService {

    }

    public static class P34 extends StubService {

    }

    public static class P35 extends StubService {

    }

    public static class P36 extends StubService {

    }

    public static class P37 extends StubService {

    }

    public static class P38 extends StubService {

    }

    public static class P39 extends StubService {

    }

    public static class P40 extends StubService {

    }

    public static class P41 extends StubService {

    }

    public static class P42 extends StubService {

    }

    public static class P43 extends StubService {

    }

    public static class P44 extends StubService {

    }

    public static class P45 extends StubService {

    }

    public static class P46 extends StubService {

    }

    public static class P47 extends StubService {

    }

    public static class P48 extends StubService {

    }

    public static class P49 extends StubService {

    }

    public static class P50 extends StubService {

    }

    public static class P51 extends StubService {

    }

    public static class P52 extends StubService {

    }

    public static class P53 extends StubService {

    }

    public static class P54 extends StubService {

    }

    public static class P55 extends StubService {

    }

    public static class P56 extends StubService {

    }

    public static class P57 extends StubService {

    }

    public static class P58 extends StubService {

    }

    public static class P59 extends StubService {

    }

    public static class P60 extends StubService {

    }

    public static class P61 extends StubService {

    }

    public static class P62 extends StubService {

    }

    public static class P63 extends StubService {

    }

    public static class P64 extends StubService {

    }

    public static class P65 extends StubService {

    }

    public static class P66 extends StubService {

    }

    public static class P67 extends StubService {

    }

    public static class P68 extends StubService {

    }

    public static class P69 extends StubService {

    }

    public static class P70 extends StubService {

    }

    public static class P71 extends StubService {

    }

    public static class P72 extends StubService {

    }

    public static class P73 extends StubService {

    }

    public static class P74 extends StubService {

    }

    public static class P75 extends StubService {

    }

    public static class P76 extends StubService {

    }

    public static class P77 extends StubService {

    }

    public static class P78 extends StubService {

    }

    public static class P79 extends StubService {

    }

    public static class P80 extends StubService {

    }

    public static class P81 extends StubService {

    }

    public static class P82 extends StubService {

    }

    public static class P83 extends StubService {

    }

    public static class P84 extends StubService {

    }

    public static class P85 extends StubService {

    }

    public static class P86 extends StubService {

    }

    public static class P87 extends StubService {

    }

    public static class P88 extends StubService {

    }

    public static class P89 extends StubService {

    }

    public static class P90 extends StubService {

    }

    public static class P91 extends StubService {

    }

    public static class P92 extends StubService {

    }

    public static class P93 extends StubService {

    }

    public static class P94 extends StubService {

    }

    public static class P95 extends StubService {

    }

    public static class P96 extends StubService {

    }

    public static class P97 extends StubService {

    }

    public static class P98 extends StubService {

    }

    public static class P99 extends StubService {

    }
}
