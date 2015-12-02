package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;

import com.ryan_zhou.training_demo.widget.listviewanimations.DynamicListView;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/12 10:54
 * @copyright TCL-MIE
 */
public class DynamicListViewWrapper implements DragAndDroplistViewWrapper {

    private final DynamicListView mDynamicListView;

    public DynamicListViewWrapper(final DynamicListView dynamicListView) {
        mDynamicListView = dynamicListView;
    }

    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        mDynamicListView.setOnScrollListener(onScrollListener);
    }

    @Override
    public int pointToPosition(int x, int y) {
        return mDynamicListView.pointToPosition(x, y);
    }

    @Override
    public int computeVerticalScrollOffset() {
        return mDynamicListView.computeVerticalScrollOffset();
    }

    @Override
    public int computeVerticalScrollExtent() {
        return mDynamicListView.computeVerticalScrollExtent();
    }

    @Override
    public int computeVerticalScrollRange() {
        return mDynamicListView.computeHorizontalScrollRange();
    }

    @Override
    public DynamicListView getListView() {
        return mDynamicListView;
    }

    @Override
    public View getChildAt(int index) {
        return mDynamicListView.getChildAt(index);
    }

    @Override
    public int getFirstVisiblePosition() {
        return mDynamicListView.getFirstVisiblePosition();
    }

    @Override
    public int getLastVisiblePosition() {
        return mDynamicListView.getLastVisiblePosition();
    }

    @Override
    public int getCount() {
        return mDynamicListView.getCount();
    }

    @Override
    public int getChildCount() {
        return mDynamicListView.getChildCount();
    }

    @Override
    public int getHeaderViewsCount() {
        return mDynamicListView.getHeaderViewsCount();
    }

    @Override
    public int getPositionForView(View view) {
        return mDynamicListView.getPositionForView(view);
    }

    @Override
    public ListAdapter getAdapter() {
        return mDynamicListView.getAdapter();
    }

    @Override
    public void smoothScrollBy(int distance, int duration) {
        mDynamicListView.smoothScrollBy(distance, duration);
    }
}
