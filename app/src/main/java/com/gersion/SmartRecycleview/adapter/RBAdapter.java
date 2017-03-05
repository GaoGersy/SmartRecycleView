package com.sunshine.adapterlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sunshine.adapterlibrary.interfaces.BAdapter;
import com.sunshine.adapterlibrary.interfaces.Converter;
import com.sunshine.adapterlibrary.viewholder.RViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * gengqiquan2016年6月3日16:58:44
 */
public class RBAdapter<T> extends Adapter<RViewHolder> implements BAdapter<Adapter<RViewHolder>> {
    private Context mContext;
    private List<? super T> list;
    private int mItemLayoutId;
    Converter<? super T> converter;

    public RBAdapter(Context context) {
        this(context, null);
    }

    public RBAdapter(Context context, List list) {
        this(context, list, 0);
    }

    public RBAdapter(Context context, List list, int itemLayoutId) {
        this.mContext = context;

        if (itemLayoutId == 0)
            itemLayoutId = new LinearLayout(context).getId();
        this.mItemLayoutId = itemLayoutId;

        if (list == null) {
            list = new ArrayList<>();
        }
        this.list = list;

    }

    public RBAdapter<T> list(List list) {
        this.list = list;
        return this;
    }

    public RBAdapter<T> layout(int itemLayoutId) {
        this.mItemLayoutId = itemLayoutId;
        return this;
    }

    public RBAdapter<T> bindViewData(Converter<? super T> converter) {
        this.converter = converter;
        return this;
    }

    @Override
    public List<? super T> getList() {
        return this.list;
    }

    @Override
    public void appendList(List list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public Adapter<RViewHolder> getAdapter() {
        return this;
    }

    @Override
    public void notifyDataChanged() {
        notifyDataSetChanged();
    }

    @Override
    public void addList(List list2) {
        this.list.addAll(list2);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    boolean hasHeader = false;
    boolean hasFooter = false;
    View headerView;
    View footerView;

    public RBAdapter<T> addHeaderView(View headerView) {
        hasHeader = true;
        this.headerView = headerView;
        return this;
    }

    public RBAdapter<T> addFooterView(View footerView) {
        hasFooter = true;
        this.footerView = footerView;
        return this;
    }

    public View getHeaderView() {
        return headerView;
    }

    public View getFooterView() {
        return footerView;
    }

    @Override
    public void onBindViewHolder(RViewHolder holder, int position) {
        if (hasHeader && position == 0) {
            return;
        } else if (hasFooter && position == (list.size() + (hasHeader ? 1 : 0))) {
            return;
        } else
            converter.convert(holder, (T) list.get(position));
    }

    @Override
    public RViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        if (hasHeader && position == 0) {
            return new RViewHolder(headerView);
        } else if (hasFooter && position == (list.size() + (hasHeader ? 1 : 0))) {
            return new RViewHolder(footerView);
        } else
            return RViewHolder.get(mContext, parent, mItemLayoutId, position);

    }
}
