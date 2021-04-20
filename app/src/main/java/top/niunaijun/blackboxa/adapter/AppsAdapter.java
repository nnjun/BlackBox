package top.niunaijun.blackboxa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;

import butterknife.BindView;
import top.niunaijun.aop_api.annotations.AsyncThread;
import top.niunaijun.aop_api.annotations.UIThread;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackboxa.R;
import top.niunaijun.blackboxa.bean.AppInfo;
import top.niunaijun.blackboxa.widget.BaseRecyclerAdapter;

import static top.niunaijun.blackbox.BlackBoxCore.getContext;

/**
 * Created by Milk on 3/23/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class AppsAdapter extends BaseRecyclerAdapter<AppInfo> {

    public AppsAdapter(Context context, int layoutType) {
        super(context, layoutType);
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        return new ModuleItemView(LayoutInflater.from(mContext).inflate(R.layout.item_app, parent, false));
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int position, AppInfo data) {

    }

    public class ModuleItemView extends ButterKnifeHolder<AppInfo> {
        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_desc)
        TextView tvDesc;
        @BindView(R.id.ck_select)
        Button ckSelect;
        @BindView(R.id.item_root)
        CardView itemRoot;

        public ModuleItemView(View itemView) {
            super(itemView);
        }

        @Override
        protected void onUpdate(final AppInfo data, int position) {
            super.onUpdate(data, position);
            tvTitle.setText(data.getName());
            tvDesc.setText(data.getPackageName());
            Glide.with(getContext())
                    .load(data.getDrawable())
                    .into(ivIcon);
            ckSelect.setText(data.isSupport() ? "运行" : "不支持架构");
            ckSelect.setOnClickListener(v -> {
                if (!data.isSupport()) {
                    return;
                }
                doRun(data);
                Toast.makeText(mContext, "启动中", Toast.LENGTH_SHORT).show();
            });
        }

        @AsyncThread
        private void doRun(AppInfo data) {
            try {
                BlackBoxCore.get().launchApk(new File(data.getApplicationInfo().sourceDir));
            } catch (RuntimeException e) {
                e.printStackTrace();
                // 当前架构不支持运行此APP
                toast("当前架构不支持运行此APP");
            }
        }

        @UIThread
        private void toast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }
}
