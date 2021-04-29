package top.niunaijun.blackboxa;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.niunaijun.aop_api.annotations.AsyncThread;
import top.niunaijun.aop_api.annotations.UIThread;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.server.user.BUserInfo;
import top.niunaijun.blackbox.utils.AbiUtils;
import top.niunaijun.blackboxa.adapter.AppsAdapter;
import top.niunaijun.blackboxa.bean.AppInfo;
import top.niunaijun.blackboxa.widget.BaseRecyclerAdapter;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @BindView(top.niunaijun.blackboxa.R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(top.niunaijun.blackboxa.R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    private AppsAdapter mAppsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(top.niunaijun.blackboxa.R.layout.activity_main);
        requestPermission();

        ButterKnife.bind(this);
        swipeLayout.setRefreshing(true);
        swipeLayout.setOnRefreshListener(this::loadData);
        loadData();
        List<BUserInfo> users = BlackBoxCore.get().getUsers();
        for (BUserInfo user : users) {
            Log.d(TAG, "BUserInfo: " + user.toString());
        }
    }

    @AsyncThread
    public void loadData() {
        List<AppInfo> apps = new ArrayList<>();

        List<ApplicationInfo> installedApplications = getPackageManager().getInstalledApplications(0);
        for (ApplicationInfo installedApplication : installedApplications) {
            if ((installedApplication.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                continue;
            AppInfo info = new AppInfo();
            info.setName(installedApplication.loadLabel(getPackageManager()).toString());
            info.setPackageName(installedApplication.packageName);
            info.setDrawable(installedApplication.loadIcon(getPackageManager()));
            info.setApplicationInfo(installedApplication);
            info.setSupport(AbiUtils.isSupport(new File(installedApplication.sourceDir)));
            apps.add(info);
        }
        update(apps);
    }

    @UIThread
    public void update(List<AppInfo> data) {
        swipeLayout.setRefreshing(false);
        if (mAppsAdapter == null) {
            mAppsAdapter = new AppsAdapter(this, BaseRecyclerAdapter.LAYOUT_TYPE_LINEAR);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(mAppsAdapter);
        }
        mAppsAdapter.setData(data, true);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 0);
    }
}
