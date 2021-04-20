package top.niunaijun.blackboxa.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import top.niunaijun.blackboxa.utils.FP;
import top.niunaijun.blackboxa.utils.Preconditions;


public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context mContext;
    private List<T> mData = new ArrayList<>();
    protected OnItemClickListener<T> mItemClickListener;
    private OnItemLongClickListener<T> mItemLongClickListener;
    protected View mHeaderView, mFooterView;

    public BaseRecyclerAdapter(Context context, int layoutType) {
        mContext = context;
        mLayoutType = layoutType;
    }

    public BaseRecyclerAdapter(Context context, @NonNull List<T> data, int layoutType) {
        mContext = context;
        mData = Preconditions.checkNotNull(data);
        mLayoutType = layoutType;
    }

    public void setItemClickListener(OnItemClickListener<T> itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(OnItemLongClickListener<T> itemLongClickListener) {
        mItemLongClickListener = itemLongClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == ITEM_TYPE_HEADER)
            return new ButterKnifeHolder(mHeaderView);
        if (mFooterView != null && viewType == ITEM_TYPE_FOOTER)
            return new ButterKnifeHolder(mFooterView);
        return onCreate(parent, viewType);
    }

    public int getDataSize() {
        return FP.size(mData);
    }

    public boolean isEmpty() {
        return FP.empty(mData) && mHeaderView == null;
    }

    public List<T> getDatas() {
        return new ArrayList<>(mData);
    }

    protected List<T> datas() {
        return mData;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == ITEM_TYPE_HEADER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p =
                    (StaggeredGridLayoutManager.LayoutParams) lp;
            int position = holder.getLayoutPosition();
            if ((position == 0 && getItemViewType(position) == ITEM_TYPE_HEADER)
                    || getItemViewType(position) == ITEM_TYPE_FOOTER) {
                p.setFullSpan(true);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null && mFooterView == null) {
            return ITEM_TYPE_NORMAL;
        } else if (mHeaderView != null && mFooterView == null) {
            if (position == 0) return ITEM_TYPE_HEADER;
            else return ITEM_TYPE_NORMAL;
        } else if (mHeaderView == null && mFooterView != null) {
            if (position == FP.size(mData))
                return ITEM_TYPE_FOOTER;
            else
                return ITEM_TYPE_NORMAL;
        } else {
            if (position == 0)
                return ITEM_TYPE_HEADER;
            else if (position == FP.size(mData) + 1)
                return ITEM_TYPE_FOOTER;
            else
                return ITEM_TYPE_NORMAL;
        }
    }


//    protected int getRealPosition(RecyclerView.ViewHolder holder) {
//        int position = holder.getLayoutPosition();
//        return mHeaderView == null ? position : position - 1;
//    }

    protected int getRealPosition(int position) {
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null && mFooterView == null) {
            return FP.size(mData);
        } else if (mHeaderView != null && mFooterView == null) {
            return FP.size(mData) + 1;
        } else if (mHeaderView == null && mFooterView != null) {
            return FP.size(mData) + 1;
        } else {
            return FP.size(mData) + 2;
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == ITEM_TYPE_HEADER) {
            onBindHeader(mHeaderView, position);
            return;
        }
        if (getItemViewType(position) == ITEM_TYPE_FOOTER) {
            onBindFooter(mFooterView, position);
            return;
        }

        final int pos = getRealPosition(position);
        if (pos >= FP.size(mData)) {
            return;
        }
        final T data = mData.get(pos);
        onBind(viewHolder, pos, data);
        if (viewHolder instanceof BaseHolder) {
            ((BaseHolder) viewHolder).onUpdate(data, pos);
        }
        if (mItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(viewHolder.itemView, pos, data);
                }
            });
        }

        if (mItemLongClickListener != null)
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mItemLongClickListener.onLongClick(viewHolder.itemView, pos, data);
                }
            });
    }

    protected void onBindHeader(View headerView, int position) {

    }

    protected void onBindFooter(View footerView, int position) {

    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    public void setData(@NonNull List<T> data, boolean clear) {
        if (clear)
            mData.clear();
        mData.addAll(Preconditions.checkNotNull(data));
        notifyDataSetChanged();
    }

    public void setDataOnly(@NonNull List<T> data) {
        mData.clear();
        mData.addAll(Preconditions.checkNotNull(data));
    }

    public void removeData(@NonNull T data) {
        Iterator<T> iterator = mData.iterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (FP.eq(next, data)) {
                iterator.remove();
                break;
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 不同的类型设置item不同的高度
     *
     * @param type
     */
    private int mLayoutType = 0;
    public static final int LAYOUT_TYPE_STAGGERED = 0;//瀑布流
    public static final int LAYOUT_TYPE_GRID = 1;//格子
    public static final int LAYOUT_TYPE_LINEAR = 2;//线性

    protected static final int ITEM_TYPE_HEADER = 0;
    protected static final int ITEM_TYPE_FOOTER = 1;
    protected static final int ITEM_TYPE_NORMAL = 2;

    public boolean hasHeaderView() {
        return mHeaderView != null;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyDataSetChanged();
    }

    public boolean hasFooterView() {
        return mFooterView != null;
    }

    public void setFooterView(View headerView) {
        mFooterView = headerView;
        notifyDataSetChanged();
    }


    public static class ButterKnifeHolder<T> extends BaseHolder<T> {
        public ButterKnifeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class BaseHolder<T> extends RecyclerView.ViewHolder {
        public BaseHolder(View itemView) {
            super(itemView);
        }

        protected void onUpdate(T data, int position) {

        }
    }

    public abstract RecyclerView.ViewHolder onCreate(ViewGroup parent, final int viewType);

    public abstract void onBind(RecyclerView.ViewHolder viewHolder, int position, T data);

    public interface OnItemClickListener<T> {
        void onItemClick(View view, int position, T data);
    }

    public interface OnItemLongClickListener<T> {
        boolean onLongClick(View view, int position, T data);
    }
}
