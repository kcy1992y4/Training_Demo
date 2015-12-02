package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/29 14:00
 * @copyright TCL-MIE
 */
public interface ListViewWrapper {

    ViewGroup getListView();

    View getChildAt(int index);

    int getFirstVisiblePosition();

    int getLastVisiblePosition();

    int getCount();

    int getChildCount();

    int getHeaderViewsCount();

    int getPositionForView(View view);

    ListAdapter getAdapter();

    void smoothScrollBy(int distance, int duration);
}
