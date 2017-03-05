package com.gersion.SmartRecycleview;

import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import com.gersion.SmartRecycleview.interfaces.Converter;
import com.gersion.SmartRecycleview.interfaces.Holder;
import com.gersion.SmartRecycleview.interfaces.OnLoadMoreListener;
import com.gersion.SmartRecycleview.interfaces.OnRefreshListener;
import com.gersion.SmartRecycleview.view.SmartRecycleView;
import com.gersion.SmartRecycleview.view.SimpleRefreshRecycleView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements OnRefreshListener, OnLoadMoreListener, Converter<String> {

  private SimpleRefreshRecycleView mSimpleRefreshView;
  private ArrayList<String> mData;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mSimpleRefreshView = (SimpleRefreshRecycleView) findViewById(R.id.simpleRefreshView);
    mData = new ArrayList();
    View view = LayoutInflater.from(this).inflate(R.layout.view_footer, null);
    mSimpleRefreshView
        .init()
        .setLayoutManger(SmartRecycleView.LayoutManagerType.LINEAR_LAYOUT)
        .setAdapter(new RBAdapter<String>(this, null, android.R.layout.simple_list_item_1)
        .bindViewData(this))
        .refreshEnable(true)
        .loadMoreEnable(true)
        .addFooterView()
        .setPageSize(20)
        .setRefreshListener(this)
        .setLoadMoreListener(this)
        .onRefresh();
  }

  public void start(View view) {
    mSimpleRefreshView.onRefresh();
  }

  public void success(View view) {
    mSimpleRefreshView.onRefreshComplete(mData);
  }

  public void failed(View view) {
    mSimpleRefreshView.onLoadFailure();
  }

  public void noData(View view) {
    mData.clear();
    mSimpleRefreshView.onRefreshComplete(mData);
  }

  public void loadMore(View view) {
    mSimpleRefreshView.onLoadMoreComplete(mData);
  }

  @Override public void onRefresh() {
    load(true);
  }

  @Override public void onLoadMore() {
    load(false);
  }

  private void load(final boolean isrefresh) {
    new Thread(new Runnable() {
      @Override public void run() {
        Looper.prepare();
        try {
          if (isrefresh) {
            Thread.sleep(2000);
          } else {
            Thread.sleep(500);
          }
        } catch (Exception e) {

        }
        runOnUiThread(new Runnable() {
          @Override public void run() {
            List<String> list = new ArrayList();
            for (int i = 0; i < 20; i++) {
              list.add("           " + i);
            }
            mData.clear();
            mData.addAll(list);
            if (isrefresh) {
              mSimpleRefreshView.onRefreshComplete(mData);
            } else {
              mSimpleRefreshView.onLoadMoreComplete(mData);
            }
            //请求失败调用 refresh.loadFailure();
          }
        });
      }
    }).start();
  }

  @Override public void convert(Holder holder, String item) {
    holder.setText(android.R.id.text1, item);
  }
}
