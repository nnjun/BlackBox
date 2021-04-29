package top.niunaijun.blackbox.client.stub;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.res.Configuration;

import top.niunaijun.blackbox.server.ClientJobServiceManager;

/**
 * Created by Milk on 4/2/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class StubJobService extends JobService {
    public static final String TAG = "StubJobService";

    @Override
    public boolean onStartJob(JobParameters params) {
        return ClientJobServiceManager.get().onStartJob(params);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return ClientJobServiceManager.get().onStopJob(params);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ClientJobServiceManager.get().onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ClientJobServiceManager.get().onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ClientJobServiceManager.get().onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        ClientJobServiceManager.get().onTrimMemory(level);
    }

    public static class P0 extends StubJobService {

    }

    public static class P1 extends StubJobService {

    }

    public static class P2 extends StubJobService {

    }

    public static class P3 extends StubJobService {

    }

    public static class P4 extends StubJobService {

    }

    public static class P5 extends StubJobService {

    }

    public static class P6 extends StubJobService {

    }

    public static class P7 extends StubJobService {

    }

    public static class P8 extends StubJobService {

    }

    public static class P9 extends StubJobService {

    }

    public static class P10 extends StubJobService {

    }

    public static class P11 extends StubJobService {

    }

    public static class P12 extends StubJobService {

    }

    public static class P13 extends StubJobService {

    }

    public static class P14 extends StubJobService {

    }

    public static class P15 extends StubJobService {

    }

    public static class P16 extends StubJobService {

    }

    public static class P17 extends StubJobService {

    }

    public static class P18 extends StubJobService {

    }

    public static class P19 extends StubJobService {

    }

    public static class P20 extends StubJobService {

    }

    public static class P21 extends StubJobService {

    }

    public static class P22 extends StubJobService {

    }

    public static class P23 extends StubJobService {

    }

    public static class P24 extends StubJobService {

    }

    public static class P25 extends StubJobService {

    }

    public static class P26 extends StubJobService {

    }

    public static class P27 extends StubJobService {

    }

    public static class P28 extends StubJobService {

    }

    public static class P29 extends StubJobService {

    }

    public static class P30 extends StubJobService {

    }

    public static class P31 extends StubJobService {

    }

    public static class P32 extends StubJobService {

    }

    public static class P33 extends StubJobService {

    }

    public static class P34 extends StubJobService {

    }

    public static class P35 extends StubJobService {

    }

    public static class P36 extends StubJobService {

    }

    public static class P37 extends StubJobService {

    }

    public static class P38 extends StubJobService {

    }

    public static class P39 extends StubJobService {

    }

    public static class P40 extends StubJobService {

    }

    public static class P41 extends StubJobService {

    }

    public static class P42 extends StubJobService {

    }

    public static class P43 extends StubJobService {

    }

    public static class P44 extends StubJobService {

    }

    public static class P45 extends StubJobService {

    }

    public static class P46 extends StubJobService {

    }

    public static class P47 extends StubJobService {

    }

    public static class P48 extends StubJobService {

    }

    public static class P49 extends StubJobService {

    }

    public static class P50 extends StubJobService {

    }

    public static class P51 extends StubJobService {

    }

    public static class P52 extends StubJobService {

    }

    public static class P53 extends StubJobService {

    }

    public static class P54 extends StubJobService {

    }

    public static class P55 extends StubJobService {

    }

    public static class P56 extends StubJobService {

    }

    public static class P57 extends StubJobService {

    }

    public static class P58 extends StubJobService {

    }

    public static class P59 extends StubJobService {

    }

    public static class P60 extends StubJobService {

    }

    public static class P61 extends StubJobService {

    }

    public static class P62 extends StubJobService {

    }

    public static class P63 extends StubJobService {

    }

    public static class P64 extends StubJobService {

    }

    public static class P65 extends StubJobService {

    }

    public static class P66 extends StubJobService {

    }

    public static class P67 extends StubJobService {

    }

    public static class P68 extends StubJobService {

    }

    public static class P69 extends StubJobService {

    }

    public static class P70 extends StubJobService {

    }

    public static class P71 extends StubJobService {

    }

    public static class P72 extends StubJobService {

    }

    public static class P73 extends StubJobService {

    }

    public static class P74 extends StubJobService {

    }

    public static class P75 extends StubJobService {

    }

    public static class P76 extends StubJobService {

    }

    public static class P77 extends StubJobService {

    }

    public static class P78 extends StubJobService {

    }

    public static class P79 extends StubJobService {

    }

    public static class P80 extends StubJobService {

    }

    public static class P81 extends StubJobService {

    }

    public static class P82 extends StubJobService {

    }

    public static class P83 extends StubJobService {

    }

    public static class P84 extends StubJobService {

    }

    public static class P85 extends StubJobService {

    }

    public static class P86 extends StubJobService {

    }

    public static class P87 extends StubJobService {

    }

    public static class P88 extends StubJobService {

    }

    public static class P89 extends StubJobService {

    }

    public static class P90 extends StubJobService {

    }

    public static class P91 extends StubJobService {

    }

    public static class P92 extends StubJobService {

    }

    public static class P93 extends StubJobService {

    }

    public static class P94 extends StubJobService {

    }

    public static class P95 extends StubJobService {

    }

    public static class P96 extends StubJobService {

    }

    public static class P97 extends StubJobService {

    }

    public static class P98 extends StubJobService {

    }

    public static class P99 extends StubJobService {

    }
}
