package com.gersion.smartrecycleview;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by a3266 on 2017/5/6.
 */

public class PullAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public PullAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, String s) {
        baseViewHolder.setText(R.id.tv_content,s);
    }
}
