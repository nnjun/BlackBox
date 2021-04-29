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

    public static class P0 extends StubActivity {

    }

    public static class P1 extends StubActivity {

    }

    public static class P2 extends StubActivity {

    }

    public static class P3 extends StubActivity {

    }

    public static class P4 extends StubActivity {

    }

    public static class P5 extends StubActivity {

    }

    public static class P6 extends StubActivity {

    }

    public static class P7 extends StubActivity {

    }

    public static class P8 extends StubActivity {

    }

    public static class P9 extends StubActivity {

    }

    public static class P10 extends StubActivity {

    }

    public static class P11 extends StubActivity {

    }

    public static class P12 extends StubActivity {

    }

    public static class P13 extends StubActivity {

    }

    public static class P14 extends StubActivity {

    }

    public static class P15 extends StubActivity {

    }

    public static class P16 extends StubActivity {

    }

    public static class P17 extends StubActivity {

    }

    public static class P18 extends StubActivity {

    }

    public static class P19 extends StubActivity {

    }

    public static class P20 extends StubActivity {

    }

    public static class P21 extends StubActivity {

    }

    public static class P22 extends StubActivity {

    }

    public static class P23 extends StubActivity {

    }

    public static class P24 extends StubActivity {

    }

    public static class P25 extends StubActivity {

    }

    public static class P26 extends StubActivity {

    }

    public static class P27 extends StubActivity {

    }

    public static class P28 extends StubActivity {

    }

    public static class P29 extends StubActivity {

    }

    public static class P30 extends StubActivity {

    }

    public static class P31 extends StubActivity {

    }

    public static class P32 extends StubActivity {

    }

    public static class P33 extends StubActivity {

    }

    public static class P34 extends StubActivity {

    }

    public static class P35 extends StubActivity {

    }

    public static class P36 extends StubActivity {

    }

    public static class P37 extends StubActivity {

    }

    public static class P38 extends StubActivity {

    }

    public static class P39 extends StubActivity {

    }

    public static class P40 extends StubActivity {

    }

    public static class P41 extends StubActivity {

    }

    public static class P42 extends StubActivity {

    }

    public static class P43 extends StubActivity {

    }

    public static class P44 extends StubActivity {

    }

    public static class P45 extends StubActivity {

    }

    public static class P46 extends StubActivity {

    }

    public static class P47 extends StubActivity {

    }

    public static class P48 extends StubActivity {

    }

    public static class P49 extends StubActivity {

    }

    public static class P50 extends StubActivity {

    }

    public static class P51 extends StubActivity {

    }

    public static class P52 extends StubActivity {

    }

    public static class P53 extends StubActivity {

    }

    public static class P54 extends StubActivity {

    }

    public static class P55 extends StubActivity {

    }

    public static class P56 extends StubActivity {

    }

    public static class P57 extends StubActivity {

    }

    public static class P58 extends StubActivity {

    }

    public static class P59 extends StubActivity {

    }

    public static class P60 extends StubActivity {

    }

    public static class P61 extends StubActivity {

    }

    public static class P62 extends StubActivity {

    }

    public static class P63 extends StubActivity {

    }

    public static class P64 extends StubActivity {

    }

    public static class P65 extends StubActivity {

    }

    public static class P66 extends StubActivity {

    }

    public static class P67 extends StubActivity {

    }

    public static class P68 extends StubActivity {

    }

    public static class P69 extends StubActivity {

    }

    public static class P70 extends StubActivity {

    }

    public static class P71 extends StubActivity {

    }

    public static class P72 extends StubActivity {

    }

    public static class P73 extends StubActivity {

    }

    public static class P74 extends StubActivity {

    }

    public static class P75 extends StubActivity {

    }

    public static class P76 extends StubActivity {

    }

    public static class P77 extends StubActivity {

    }

    public static class P78 extends StubActivity {

    }

    public static class P79 extends StubActivity {

    }

    public static class P80 extends StubActivity {

    }

    public static class P81 extends StubActivity {

    }

    public static class P82 extends StubActivity {

    }

    public static class P83 extends StubActivity {

    }

    public static class P84 extends StubActivity {

    }

    public static class P85 extends StubActivity {

    }

    public static class P86 extends StubActivity {

    }

    public static class P87 extends StubActivity {

    }

    public static class P88 extends StubActivity {

    }

    public static class P89 extends StubActivity {

    }

    public static class P90 extends StubActivity {

    }

    public static class P91 extends StubActivity {

    }

    public static class P92 extends StubActivity {

    }

    public static class P93 extends StubActivity {

    }

    public static class P94 extends StubActivity {

    }

    public static class P95 extends StubActivity {

    }

    public static class P96 extends StubActivity {

    }

    public static class P97 extends StubActivity {

    }

    public static class P98 extends StubActivity {

    }

    public static class P99 extends StubActivity {

    }
}
