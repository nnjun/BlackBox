// IBJobManagerService.aidl
package top.niunaijun.blackbox.server.am;

import android.content.Intent;
import android.content.ComponentName;
import android.os.IBinder;
import java.lang.String;
import android.app.job.JobInfo;
import top.niunaijun.blackbox.server.JobRecord;

// Declare any non-default types here with import statements

interface IBJobManagerService {
    JobInfo schedule(in JobInfo info);
    JobRecord queryJobRecord(String processName, int jobId);
    void cancelAll(String processName);
    int cancel(String processName, int jobId);

}
