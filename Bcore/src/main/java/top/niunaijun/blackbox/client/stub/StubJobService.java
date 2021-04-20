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

    public static class StubJobServiceP0 extends StubJobService {

    }

    public static class StubJobServiceP1 extends StubJobService {

    }

    public static class StubJobServiceP2 extends StubJobService {

    }

    public static class StubJobServiceP3 extends StubJobService {

    }

    public static class StubJobServiceP4 extends StubJobService {

    }

    public static class StubJobServiceP5 extends StubJobService {

    }

    public static class StubJobServiceP6 extends StubJobService {

    }

    public static class StubJobServiceP7 extends StubJobService {

    }

    public static class StubJobServiceP8 extends StubJobService {

    }

    public static class StubJobServiceP9 extends StubJobService {

    }

    public static class StubJobServiceP10 extends StubJobService {

    }

    public static class StubJobServiceP11 extends StubJobService {

    }

    public static class StubJobServiceP12 extends StubJobService {

    }

    public static class StubJobServiceP13 extends StubJobService {

    }

    public static class StubJobServiceP14 extends StubJobService {

    }

    public static class StubJobServiceP15 extends StubJobService {

    }

    public static class StubJobServiceP16 extends StubJobService {

    }

    public static class StubJobServiceP17 extends StubJobService {

    }

    public static class StubJobServiceP18 extends StubJobService {

    }

    public static class StubJobServiceP19 extends StubJobService {

    }

    public static class StubJobServiceP20 extends StubJobService {

    }

    public static class StubJobServiceP21 extends StubJobService {

    }

    public static class StubJobServiceP22 extends StubJobService {

    }

    public static class StubJobServiceP23 extends StubJobService {

    }

    public static class StubJobServiceP24 extends StubJobService {

    }

    public static class StubJobServiceP25 extends StubJobService {

    }

    public static class StubJobServiceP26 extends StubJobService {

    }

    public static class StubJobServiceP27 extends StubJobService {

    }

    public static class StubJobServiceP28 extends StubJobService {

    }

    public static class StubJobServiceP29 extends StubJobService {

    }

    public static class StubJobServiceP30 extends StubJobService {

    }

    public static class StubJobServiceP31 extends StubJobService {

    }

    public static class StubJobServiceP32 extends StubJobService {

    }

    public static class StubJobServiceP33 extends StubJobService {

    }

    public static class StubJobServiceP34 extends StubJobService {

    }

    public static class StubJobServiceP35 extends StubJobService {

    }

    public static class StubJobServiceP36 extends StubJobService {

    }

    public static class StubJobServiceP37 extends StubJobService {

    }

    public static class StubJobServiceP38 extends StubJobService {

    }

    public static class StubJobServiceP39 extends StubJobService {

    }

    public static class StubJobServiceP40 extends StubJobService {

    }

    public static class StubJobServiceP41 extends StubJobService {

    }

    public static class StubJobServiceP42 extends StubJobService {

    }

    public static class StubJobServiceP43 extends StubJobService {

    }

    public static class StubJobServiceP44 extends StubJobService {

    }

    public static class StubJobServiceP45 extends StubJobService {

    }

    public static class StubJobServiceP46 extends StubJobService {

    }

    public static class StubJobServiceP47 extends StubJobService {

    }

    public static class StubJobServiceP48 extends StubJobService {

    }

    public static class StubJobServiceP49 extends StubJobService {

    }

    public static class StubJobServiceP50 extends StubJobService {

    }

    public static class StubJobServiceP51 extends StubJobService {

    }

    public static class StubJobServiceP52 extends StubJobService {

    }

    public static class StubJobServiceP53 extends StubJobService {

    }

    public static class StubJobServiceP54 extends StubJobService {

    }

    public static class StubJobServiceP55 extends StubJobService {

    }

    public static class StubJobServiceP56 extends StubJobService {

    }

    public static class StubJobServiceP57 extends StubJobService {

    }

    public static class StubJobServiceP58 extends StubJobService {

    }

    public static class StubJobServiceP59 extends StubJobService {

    }

    public static class StubJobServiceP60 extends StubJobService {

    }

    public static class StubJobServiceP61 extends StubJobService {

    }

    public static class StubJobServiceP62 extends StubJobService {

    }

    public static class StubJobServiceP63 extends StubJobService {

    }

    public static class StubJobServiceP64 extends StubJobService {

    }

    public static class StubJobServiceP65 extends StubJobService {

    }

    public static class StubJobServiceP66 extends StubJobService {

    }

    public static class StubJobServiceP67 extends StubJobService {

    }

    public static class StubJobServiceP68 extends StubJobService {

    }

    public static class StubJobServiceP69 extends StubJobService {

    }

    public static class StubJobServiceP70 extends StubJobService {

    }

    public static class StubJobServiceP71 extends StubJobService {

    }

    public static class StubJobServiceP72 extends StubJobService {

    }

    public static class StubJobServiceP73 extends StubJobService {

    }

    public static class StubJobServiceP74 extends StubJobService {

    }

    public static class StubJobServiceP75 extends StubJobService {

    }

    public static class StubJobServiceP76 extends StubJobService {

    }

    public static class StubJobServiceP77 extends StubJobService {

    }

    public static class StubJobServiceP78 extends StubJobService {

    }

    public static class StubJobServiceP79 extends StubJobService {

    }

    public static class StubJobServiceP80 extends StubJobService {

    }

    public static class StubJobServiceP81 extends StubJobService {

    }

    public static class StubJobServiceP82 extends StubJobService {

    }

    public static class StubJobServiceP83 extends StubJobService {

    }

    public static class StubJobServiceP84 extends StubJobService {

    }

    public static class StubJobServiceP85 extends StubJobService {

    }

    public static class StubJobServiceP86 extends StubJobService {

    }

    public static class StubJobServiceP87 extends StubJobService {

    }

    public static class StubJobServiceP88 extends StubJobService {

    }

    public static class StubJobServiceP89 extends StubJobService {

    }

    public static class StubJobServiceP90 extends StubJobService {

    }

    public static class StubJobServiceP91 extends StubJobService {

    }

    public static class StubJobServiceP92 extends StubJobService {

    }

    public static class StubJobServiceP93 extends StubJobService {

    }

    public static class StubJobServiceP94 extends StubJobService {

    }

    public static class StubJobServiceP95 extends StubJobService {

    }

    public static class StubJobServiceP96 extends StubJobService {

    }

    public static class StubJobServiceP97 extends StubJobService {

    }

    public static class StubJobServiceP98 extends StubJobService {

    }

    public static class StubJobServiceP99 extends StubJobService {

    }
}
