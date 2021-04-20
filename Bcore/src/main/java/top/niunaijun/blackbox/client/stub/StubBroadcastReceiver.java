package top.niunaijun.blackbox.client.stub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.client.stub.record.StubBroadcastRecord;

/**
 * Created by Milk on 4/7/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class StubBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "StubBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        StubBroadcastRecord record = StubBroadcastRecord.create(intent);
        Log.d(TAG, "onReceive: " + intent);

        BlackBoxCore.getContext().sendBroadcast(record.mIntent);
    }
}
