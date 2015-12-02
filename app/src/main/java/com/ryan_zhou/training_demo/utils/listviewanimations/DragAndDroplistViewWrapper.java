package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.widget.AbsListView;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/11 16:21
 * @copyright TCL-MIE
 */
public interface DragAndDroplistViewWrapper extends ListViewWrapper {

    void setOnScrollListener(AbsListView.OnScrollListener onScrollListener);

    int pointToPosition(int x, int y);

    int computeVerticalScrollOffset();

    int computeVerticalScrollExtent();

    int computeVerticalScrollRange();

}
