package com.gersion.SmartRecycleview.interfaces;

import java.util.List;

/**
 * Created by 耿 on 2016/9/23.
 */
public interface BAdapter<T> {
    public List getList();

    public void appendData(List list);
    public T getAdapter();
    //刷新数据
    public void notifyDataChanged();

    public void setData(List list);

}
