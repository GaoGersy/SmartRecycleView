package com.gersion.SmartRecycleview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * @作者 a3266
 * @版本
 * @包名 com.gersion.refreshrecycleview.view
 * @待完成
 * @创建时间 2017/3/5
 * @功能描述 TODO
 * @更新人 $
 * @更新时间 $
 * @更新版本 $
 */
public class SimpleRefreshRecycleView extends SmartRecycleView {

  public SimpleRefreshRecycleView(Context context) {
    this(context,null);
  }

  public SimpleRefreshRecycleView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public View setFailedView() {
    return null;
  }

  @Override public View setNoDataView() {
    return null;
  }

  @Override public View setLoadingView() {
    return null;
  }
}
