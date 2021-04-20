package top.niunaijun.blackbox.client.stub;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import top.niunaijun.blackbox.client.BClient;
import top.niunaijun.blackbox.client.hook.HookManager;
import top.niunaijun.blackbox.client.hook.proxies.app.HCallbackStub;
import top.niunaijun.blackbox.client.stub.record.StubActivityRecord;

/**
 * Created by Milk on 3/28/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class StubActivity extends Activity {
    public static final String TAG = "StubActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        finish();

        HookManager.get().checkEnv(HCallbackStub.class);
//        HookManager.get().checkEnv(AppInstrumentation.class);

        StubActivityRecord record = StubActivityRecord.create(getIntent());
        if (record.mTarget != null) {
            record.mTarget.setExtrasClassLoader(BClient.getApplication().getClassLoader());
            startActivity(record.mTarget);
        }
    }

    public static class StubActivityP0 extends StubActivity {

    }

    public static class StubActivityP1 extends StubActivity {

    }

    public static class StubActivityP2 extends StubActivity {

    }

    public static class StubActivityP3 extends StubActivity {

    }

    public static class StubActivityP4 extends StubActivity {

    }

    public static class StubActivityP5 extends StubActivity {

    }

    public static class StubActivityP6 extends StubActivity {

    }

    public static class StubActivityP7 extends StubActivity {

    }

    public static class StubActivityP8 extends StubActivity {

    }

    public static class StubActivityP9 extends StubActivity {

    }

    public static class StubActivityP10 extends StubActivity {

    }

    public static class StubActivityP11 extends StubActivity {

    }

    public static class StubActivityP12 extends StubActivity {

    }

    public static class StubActivityP13 extends StubActivity {

    }

    public static class StubActivityP14 extends StubActivity {

    }

    public static class StubActivityP15 extends StubActivity {

    }

    public static class StubActivityP16 extends StubActivity {

    }

    public static class StubActivityP17 extends StubActivity {

    }

    public static class StubActivityP18 extends StubActivity {

    }

    public static class StubActivityP19 extends StubActivity {

    }

    public static class StubActivityP20 extends StubActivity {

    }

    public static class StubActivityP21 extends StubActivity {

    }

    public static class StubActivityP22 extends StubActivity {

    }

    public static class StubActivityP23 extends StubActivity {

    }

    public static class StubActivityP24 extends StubActivity {

    }

    public static class StubActivityP25 extends StubActivity {

    }

    public static class StubActivityP26 extends StubActivity {

    }

    public static class StubActivityP27 extends StubActivity {

    }

    public static class StubActivityP28 extends StubActivity {

    }

    public static class StubActivityP29 extends StubActivity {

    }

    public static class StubActivityP30 extends StubActivity {

    }

    public static class StubActivityP31 extends StubActivity {

    }

    public static class StubActivityP32 extends StubActivity {

    }

    public static class StubActivityP33 extends StubActivity {

    }

    public static class StubActivityP34 extends StubActivity {

    }

    public static class StubActivityP35 extends StubActivity {

    }

    public static class StubActivityP36 extends StubActivity {

    }

    public static class StubActivityP37 extends StubActivity {

    }

    public static class StubActivityP38 extends StubActivity {

    }

    public static class StubActivityP39 extends StubActivity {

    }

    public static class StubActivityP40 extends StubActivity {

    }

    public static class StubActivityP41 extends StubActivity {

    }

    public static class StubActivityP42 extends StubActivity {

    }

    public static class StubActivityP43 extends StubActivity {

    }

    public static class StubActivityP44 extends StubActivity {

    }

    public static class StubActivityP45 extends StubActivity {

    }

    public static class StubActivityP46 extends StubActivity {

    }

    public static class StubActivityP47 extends StubActivity {

    }

    public static class StubActivityP48 extends StubActivity {

    }

    public static class StubActivityP49 extends StubActivity {

    }

    public static class StubActivityP50 extends StubActivity {

    }

    public static class StubActivityP51 extends StubActivity {

    }

    public static class StubActivityP52 extends StubActivity {

    }

    public static class StubActivityP53 extends StubActivity {

    }

    public static class StubActivityP54 extends StubActivity {

    }

    public static class StubActivityP55 extends StubActivity {

    }

    public static class StubActivityP56 extends StubActivity {

    }

    public static class StubActivityP57 extends StubActivity {

    }

    public static class StubActivityP58 extends StubActivity {

    }

    public static class StubActivityP59 extends StubActivity {

    }

    public static class StubActivityP60 extends StubActivity {

    }

    public static class StubActivityP61 extends StubActivity {

    }

    public static class StubActivityP62 extends StubActivity {

    }

    public static class StubActivityP63 extends StubActivity {

    }

    public static class StubActivityP64 extends StubActivity {

    }

    public static class StubActivityP65 extends StubActivity {

    }

    public static class StubActivityP66 extends StubActivity {

    }

    public static class StubActivityP67 extends StubActivity {

    }

    public static class StubActivityP68 extends StubActivity {

    }

    public static class StubActivityP69 extends StubActivity {

    }

    public static class StubActivityP70 extends StubActivity {

    }

    public static class StubActivityP71 extends StubActivity {

    }

    public static class StubActivityP72 extends StubActivity {

    }

    public static class StubActivityP73 extends StubActivity {

    }

    public static class StubActivityP74 extends StubActivity {

    }

    public static class StubActivityP75 extends StubActivity {

    }

    public static class StubActivityP76 extends StubActivity {

    }

    public static class StubActivityP77 extends StubActivity {

    }

    public static class StubActivityP78 extends StubActivity {

    }

    public static class StubActivityP79 extends StubActivity {

    }

    public static class StubActivityP80 extends StubActivity {

    }

    public static class StubActivityP81 extends StubActivity {

    }

    public static class StubActivityP82 extends StubActivity {

    }

    public static class StubActivityP83 extends StubActivity {

    }

    public static class StubActivityP84 extends StubActivity {

    }

    public static class StubActivityP85 extends StubActivity {

    }

    public static class StubActivityP86 extends StubActivity {

    }

    public static class StubActivityP87 extends StubActivity {

    }

    public static class StubActivityP88 extends StubActivity {

    }

    public static class StubActivityP89 extends StubActivity {

    }

    public static class StubActivityP90 extends StubActivity {

    }

    public static class StubActivityP91 extends StubActivity {

    }

    public static class StubActivityP92 extends StubActivity {

    }

    public static class StubActivityP93 extends StubActivity {

    }

    public static class StubActivityP94 extends StubActivity {

    }

    public static class StubActivityP95 extends StubActivity {

    }

    public static class StubActivityP96 extends StubActivity {

    }

    public static class StubActivityP97 extends StubActivity {

    }

    public static class StubActivityP98 extends StubActivity {

    }

    public static class StubActivityP99 extends StubActivity {

    }
}
