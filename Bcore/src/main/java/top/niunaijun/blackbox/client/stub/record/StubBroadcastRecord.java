package top.niunaijun.blackbox.client.stub.record;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import java.util.ArrayList;

/**
 * Created by Milk on 4/7/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class StubBroadcastRecord {
    public Intent mIntent;
    public ArrayList<ActivityInfo> mActivityInfo;
    public int mUserId;

    public StubBroadcastRecord(Intent intent, ArrayList<ActivityInfo> activityInfo, int userId) {
        mIntent = intent;
        mActivityInfo = activityInfo;
        mUserId = userId;
    }

    public static void saveStub(Intent shadow, Intent target, ArrayList<ActivityInfo> activityInfo, int userId) {
        shadow.putExtra("_VM_|_target_", target);
        shadow.putParcelableArrayListExtra("_VM_|_activity_info_", activityInfo);
        shadow.putExtra("_VM_|_user_id_", userId);
    }

    public static StubBroadcastRecord create(Intent intent) {
        Intent target = intent.getParcelableExtra("_VM_|_target_");
        ArrayList<ActivityInfo> activityInfo = intent.getParcelableArrayListExtra("_VM_|_activity_info_");
        int userId = intent.getIntExtra("_VM_|_user_id_", 0);
        return new StubBroadcastRecord(target, activityInfo, userId);
    }
}
