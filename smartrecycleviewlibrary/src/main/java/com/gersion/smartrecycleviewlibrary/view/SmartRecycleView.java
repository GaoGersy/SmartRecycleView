package com.gersion.smartrecycleviewlibrary.view;

import android.content.Context;
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
import com.gersion.smartrecycleviewlibrary.R;
import com.gersion.smartrecycleviewlibrary.view.ptr2.PullToRefreshLayout;

import java.util.List;


/**
 * @作者 Gaogersy
 * @版本
 * @包名 com.gersion.refreshrecycleview.view
 * @待完成
 * @创建时间 2017/3/4
 */
public abstract class SmartRecycleView<T> extends RelativeLayout {

    protected PullToRefreshLayout.OnRefreshListener mRefreshListener;
    private PullToRefreshLayout mPullRereshLayout;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private View mFailedView;
    private View mNoDataView;
    private View mLoadingView;
    private int mPageSize = 20;
    private boolean mIsLoadMore;
    private boolean mLoadMoreEnable;
    private LayoutManagerType mLayoutManagerType;
    private BaseQuickAdapter mAdapter;
    private int currentPage = 0;
    private int firstPage;//第一页的序号
    private boolean isFirstLoad = true;//第一次初始化加载

    public SmartRecycleView(Context context) {
        this(context, null);
    }

    public SmartRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        mPullRereshLayout = new PullToRefreshLayout(mContext);
        addView(mPullRereshLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mRecyclerView = new RecyclerView(mContext);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOverScrollMode(SCROLL_AXIS_NONE);
        mPullRereshLayout.addView(mRecyclerView,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

        init();
    }

    private void init() {
        mFailedView = setFailedView();
        if (mFailedView == null) {
            mFailedView = LayoutInflater.from(mContext).inflate(R.layout.view_falied, null);
            mFailedView.setVisibility(View.GONE);
            mFailedView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFailedView.setVisibility(View.GONE);
                    mRefreshListener.onRefresh(firstPage);
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
        addView(mLoadingView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mLoadingView.setVisibility(View.GONE);
    }

    /*
    * ~~ 时间：2017/5/6 15:38 ~~
    * 下拉刷新成功后数据处理
    **/
    public void onRefresh(List<T> data) {
        if (data == null) {
            if (isFirstLoad) {
                isFirstLoad = false;
                setViewStatus(ViewStatus.FAILED);
            } else {
                mPullRereshLayout.onRefreshErr();
            }
        } else {
            if (isFirstLoad) {
                isFirstLoad = false;
                setViewStatus(ViewStatus.SUCCESS);
            }
            if (data.size() == 0) {
                setViewStatus(ViewStatus.NO_DATA);
            } else {
                currentPage = firstPage;
                mAdapter.setNewData(data);
                if (data.size() >= mPageSize) {
                    mPullRereshLayout.onRefreshSuccess();
                } else {
                    mPullRereshLayout.setNoMoreData(true);
                }
            }
        }
        mPullRereshLayout.setCurrentPage(currentPage);
    }

    /*
    * ~~ 时间：2017/5/6 15:38 ~~
    * 加载更多数据成功后的数据处理
    **/
    public void onLoadMore(List<T> data) {
        if (data == null) {
            mPullRereshLayout.onLoadMoreErr();
        } else {
            currentPage++;
            mAdapter.addData(data);
            if (data.size() >= mPageSize) {
                mPullRereshLayout.onLoadMoreSuccess();
            } else {
                mPullRereshLayout.setNoMoreData(true);
            }
        }
        mPullRereshLayout.setCurrentPage(currentPage);
    }

    public void onLoadMoreErr() {
        mPullRereshLayout.onLoadMoreErr();
    }

    public void onRefreshErr() {
        mPullRereshLayout.onLoadMoreErr();
    }

    /*
    * ~~ 时间：2017/5/6 17:02 ~~
    * 设置是否自动加载数据
    **/
    public SmartRecycleView<T> setAutoRefresh(boolean autoRefresh) {
        setViewStatus(ViewStatus.LOADING);
        if (autoRefresh) {
            mPullRereshLayout.autoRefresh();
        }
        return this;
    }

    /*
    * ~~ 时间：2017/5/6 15:25 ~~
    * 设置第一页的page的值
    **/
    public SmartRecycleView setFirstPage(int page) {
        this.currentPage = page;
        this.firstPage = page;
        return this;
    }

    /*
    * ~~ 时间：2017/5/6 15:25 ~~
    * 设置每页面条目个数
    **/
    public SmartRecycleView setPageSize(int pageSize) {
        this.mPageSize = pageSize;
        return this;
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public SmartRecycleView setAdapter(BaseQuickAdapter adapter) {
        mAdapter = adapter;
        if (adapter == null) {
            throw new NullPointerException("adapter不能为空");
        }
        mRecyclerView.setAdapter(adapter);
        return this;
    }

    public List getList() {
        return mAdapter.getData();
    }

//    public void onRefresh() {
//        mPullRereshLayout.setRefreshing(true);
//        if (mRefreshListener != null) {
//            mRefreshListener.onRefresh();
//            setViewStatus(ViewStatus.LOADING);
//        }
//    }

    //刷新完成添加列表数据
    public void onRefreshComplete(List<T> list) {
        mPullRereshLayout.setRefreshing(false);
        if (list == null) {
            setViewStatus(ViewStatus.FAILED);
        } else {
            if (list.size() == mPageSize) {
                loadMoreEnable(true);
                setViewStatus(ViewStatus.SUCCESS);
                mAdapter.setNewData(list);
            } else {
                loadMoreEnable(false);
                if (list.size() == 0) {
                    setViewStatus(ViewStatus.NO_DATA);
                } else {
                    mAdapter.setNewData(list);
                    setViewStatus(ViewStatus.SUCCESS);
                }
            }
        }
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
        mPullRereshLayout.setRefreshing(false);
        if (!mIsLoadMore) {
            setViewStatus(ViewStatus.FAILED);
        } else {
            mLoadMoreEnable = false;
            mIsLoadMore = false;
        }
    }

    public abstract View setFailedView();

    public abstract View setNoDataView();

    public abstract View setLoadingView();

    public SmartRecycleView refreshEnable(boolean enable) {
        mPullRereshLayout.setRefreshEnable(enable);
        return this;
    }

    public SmartRecycleView loadMoreEnable(boolean enable) {
        mPullRereshLayout.setLoadMoreEnable(enable);
        return this;
    }

    public SmartRecycleView setLayoutManger(LayoutManagerType layoutManagerType) {
        setLayoutManger(layoutManagerType, LinearLayoutManager.VERTICAL);
        return this;
    }

    public SmartRecycleView setLayoutManger(LayoutManagerType layoutManagerType, int orientation) {
        setLayoutManger(layoutManagerType, orientation, 2);
        return this;
    }

    public SmartRecycleView setLayoutManger(LayoutManagerType layoutManagerType, int orientation, int spanCout) {
        RecyclerView.LayoutManager layoutManager = null;
        if (layoutManagerType == LayoutManagerType.LINEAR_LAYOUT) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setOrientation(orientation);
            layoutManager = linearLayoutManager;
        } else if (layoutManagerType == LayoutManagerType.GRID_LAYOUT) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, spanCout);
            gridLayoutManager.setOrientation(orientation);
            layoutManager = gridLayoutManager;
        } else if (layoutManagerType == LayoutManagerType.STAGGER_LAYOUT) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(spanCout, orientation);
            layoutManager = staggeredGridLayoutManager;
        }
        mRecyclerView.setLayoutManager(layoutManager);
        return this;
    }

    public SmartRecycleView setRefreshListener(PullToRefreshLayout.OnRefreshListener listener) {
        mRefreshListener = listener;
        mPullRereshLayout.setOnRefreshListener(mRefreshListener);
        mPullRereshLayout.setOnRertyListener(mRefreshListener);
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
