package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/29 14:12
 * @copyright TCL-MIE
 */
public class AbsListViewWrapper implements ListViewWrapper {

    private final AbsListView mAbsListView;

    public AbsListViewWrapper(final AbsListView absListView) {
        mAbsListView = absListView;
    }

    @Override
    public ViewGroup getListView() {
        return mAbsListView;
    }

    @Override
    public View getChildAt(int index) {
        return mAbsListView.getChildAt(index);
    }

    @Override
    public int getFirstVisiblePosition() {
        return mAbsListView.getFirstVisiblePosition();
    }

    @Override
    public int getLastVisiblePosition() {
        return mAbsListView.getLastVisiblePosition();
    }

    @Override
    public int getCount() {
        return mAbsListView.getCount();
    }

    @Override
    public int getChildCount() {
        return mAbsListView.getChildCount();
    }

    @Override
    public int getHeaderViewsCount() {
        int result = 0;
        if (mAbsListView instanceof ListView) {
            return ((ListView) mAbsListView).getHeaderViewsCount();
        }
        return result;
    }

    @Override
    public int getPositionForView(View view) {
        return mAbsListView.getPositionForView(view);
    }

    @Override
    public ListAdapter getAdapter() {
        return mAbsListView.getAdapter();
    }

    @Override
    public void smoothScrollBy(int distance, int duration) {
        mAbsListView.smoothScrollBy(distance, duration);
    }

}
