package com.gersion.SmartRecycleview.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gersion.SmartRecycleview.R;
import com.gersion.SmartRecycleview.interfaces.OnRefreshListener;
import java.util.List;

import static com.gersion.SmartRecycleview.view.SmartRecycleView.LayoutManagerType.STAGGER_LAYOUT;

/**
 * @作者 Gaogersy
 * @版本
 * @包名 com.gersion.refreshrecycleview.view
 * @待完成
 * @创建时间 2017/3/4
 */
public abstract class SmartRecycleView extends RelativeLayout {

  protected OnRefreshListener mRefreshListener;
  private SwipeRefreshLayout mSwipeRefreshLayout;
  private Context mContext;
  private RecyclerView mRecyclerView;
  private View mFailedView;
  private View mNoDataView;
  private View mLoadingView;
  private int mPageSize = 20;
  private boolean mIsLoadMore;
  private boolean mLoadMoreEnable;
  private BaseQuickAdapter mAapter;
  private LayoutManagerType mLayoutManagerType;
  private BaseQuickAdapter.RequestLoadMoreListener mLoadMoreListener;

  public SmartRecycleView(Context context) {
    this(context, null);
  }

  public SmartRecycleView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    initView();
  }

  private void initView() {
    mSwipeRefreshLayout = new SwipeRefreshLayout(mContext);
    mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.GREEN,Color.RED,Color.YELLOW);
    addView(mSwipeRefreshLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
    mSwipeRefreshLayout.setColorSchemeColors(0xffff2500);
    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

      @Override public void onRefresh() {
        mIsLoadMore = false;
        if (mRefreshListener != null) {
          mRefreshListener.onRefresh();
        }
      }
    });
    mRecyclerView = new RecyclerView(mContext);
    mRecyclerView.setHasFixedSize(true);
    mSwipeRefreshLayout.addView(mRecyclerView,
        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
  }

  public SmartRecycleView init() {
    mFailedView = setFailedView();
    if (mFailedView == null) {
      mFailedView = LayoutInflater.from(mContext).inflate(R.layout.view_falied, null);
      mFailedView.setVisibility(View.GONE);
      mFailedView.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View v) {
          mFailedView.setVisibility(View.GONE);
          onRefresh();
        }
      });
    }
    addView(mFailedView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));

    mNoDataView = setNoDataView();
    if (mNoDataView == null) {
      mNoDataView = LayoutInflater.from(mContext).inflate(R.layout.view_no_data, null);
    }
    addView(mNoDataView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
    mNoDataView.setVisibility(View.GONE);

    if (mLoadingView == null) {
      mLoadingView = LayoutInflater.from(mContext).inflate(R.layout.view_loading, null);
    }
    addView(mLoadingView);
    mLoadingView.setVisibility(View.GONE);
    return this;
  }

  public SmartRecycleView setPageSize(int size) {
    this.mPageSize = size;
    return this;
  }

  public BaseQuickAdapter getAdapter() {
    return this.mAapter;
  }

  public List getList() {
    return this.mAapter.getData();
  }

  //设置适配器
  public SmartRecycleView setAdapter(BaseQuickAdapter adapter) {
    this.mAapter = adapter;
    mRecyclerView.setAdapter(mAapter);
    return this;
  }

  public void onRefresh() {
    mSwipeRefreshLayout.setRefreshing(true);
    if (mRefreshListener != null) {
      mRefreshListener.onRefresh();
      setViewStatus(ViewStatus.LOADING);
    }
  }

  //刷新完成添加列表数据
  public void onRefreshComplete(List list) {
    mSwipeRefreshLayout.setRefreshing(false);
    if (list == null) {
      setViewStatus(ViewStatus.FAILED);
    } else {
      if (list.size() == mPageSize) {
        loadMoreEnable(true);
        setViewStatus(ViewStatus.SUCCESS);
        mAapter.setNewData(list);
      } else {
        loadMoreEnable(false);
        if (list.size() == 0) {
          setViewStatus(ViewStatus.NO_DATA);
        } else {
          mAapter.setNewData(list);
          setViewStatus(ViewStatus.SUCCESS);
        }
      }
    }
  }

  //加载更多完成后添加数据
  public void onLoadMoreComplete(List list) {
    setViewStatus(ViewStatus.SUCCESS);
    if (list == null) {
      return;
    }
    if (list.size() == mPageSize) {
      loadMoreEnable(true);
    } else {
      loadMoreEnable(false);
    }
    mAapter.addData(list);

  }

  private void setViewStatus(ViewStatus status) {
    try {
      if (status == ViewStatus.LOADING) {
        mLoadingView.setVisibility(VISIBLE);
        mNoDataView.setVisibility(GONE);
        mFailedView.setVisibility(GONE);
        mRecyclerView.setVisibility(GONE);
      } else if (status == ViewStatus.FAILED) {
        mLoadingView.setVisibility(GONE);
        mNoDataView.setVisibility(GONE);
        mFailedView.setVisibility(VISIBLE);
        mRecyclerView.setVisibility(GONE);
      } else if (status == ViewStatus.NO_DATA) {
        mLoadingView.setVisibility(GONE);
        mNoDataView.setVisibility(VISIBLE);
        mFailedView.setVisibility(GONE);
        mRecyclerView.setVisibility(GONE);
      } else if (status == ViewStatus.SUCCESS) {
        mLoadingView.setVisibility(GONE);
        mNoDataView.setVisibility(GONE);
        mFailedView.setVisibility(GONE);
        mRecyclerView.setVisibility(VISIBLE);
      }
    } catch (NullPointerException e) {

    }
  }

  //数据请求失败调用
  public void onLoadFailure() {
    mSwipeRefreshLayout.setRefreshing(false);
    if (!mIsLoadMore) {
      setViewStatus(ViewStatus.FAILED);
    } else {
      mLoadMoreEnable = false;
      mIsLoadMore = false;
      //mLoadMoreView.onRefreshFailure();
    }
  }

  public abstract View setFailedView();

  public abstract View setNoDataView();

  public abstract View setLoadingView();

  public SmartRecycleView loadMoreEnable(boolean enable) {
    mLoadMoreEnable = enable;
    mAapter.setEnableLoadMore(enable);
    return this;
  }

  public SmartRecycleView refreshEnable(boolean enable) {
    mSwipeRefreshLayout.setEnabled(enable);
    return this;
  }

  public SmartRecycleView setLayoutManger(LayoutManagerType layoutManagerType){
    setLayoutManger(layoutManagerType,LinearLayoutManager.VERTICAL);
    return this;
  }

  public SmartRecycleView setLayoutManger(LayoutManagerType layoutManagerType,int orientation){
    setLayoutManger(layoutManagerType,orientation,2);
    return this;
  }

  public SmartRecycleView setLayoutManger(LayoutManagerType layoutManagerType,int orientation,int spanCout){
    RecyclerView.LayoutManager layoutManager = null;
    if (layoutManagerType==LayoutManagerType.LINEAR_LAYOUT) {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
      linearLayoutManager.setOrientation(orientation);
      layoutManager = linearLayoutManager;
    }else if (layoutManagerType==LayoutManagerType.GRID_LAYOUT){
      GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,spanCout);
      gridLayoutManager.setOrientation(orientation);
      layoutManager = gridLayoutManager;
    }else if (layoutManagerType== STAGGER_LAYOUT){
      StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(spanCout,orientation);
      layoutManager = staggeredGridLayoutManager;
    }
    mRecyclerView.setLayoutManager(layoutManager);
    return this;
  }

  public SmartRecycleView addFooterView() {
    View loadMoreView = LayoutInflater
        .from(mContext)
        .inflate(R.layout.view_footer, mRecyclerView, false);
    mAapter.addFooterView(loadMoreView);
    return this;
  }

  public SmartRecycleView setRefreshListener(OnRefreshListener listener){
    mRefreshListener = listener;
    return this;
  }

  public SmartRecycleView setLoadMoreListener(BaseQuickAdapter.RequestLoadMoreListener listener){
    mLoadMoreListener = listener;
    mAapter.setOnLoadMoreListener(listener);
    return this;
  }


  protected enum ViewStatus {
    LOADING, NO_DATA, FAILED, SUCCESS
  }

  public enum LayoutManagerType {
    LINEAR_LAYOUT,
    GRID_LAYOUT,
    STAGGER_LAYOUT,

    //BANNER_LAYOUT,
    //FIX_LAYOUT,
    //SINGLE_LAYOUT,
    //FLOAT_LAYOUT,
    //ONEN_LAYOUT,
    //COLUMN_LAYOUT,
    //STICKY_LAYOUT,
  }
}
