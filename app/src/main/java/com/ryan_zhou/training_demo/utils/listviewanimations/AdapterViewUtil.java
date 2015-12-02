package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/2 19:24
 * @copyright TCL-MIE
 */
public class AdapterViewUtil {

    private AdapterViewUtil() {}

    public static int getPositionForView(final ListViewWrapper listViewWrapper, final View view) {
        return listViewWrapper.getPositionForView(view) - listViewWrapper.getHeaderViewsCount();
    }

    public static int getPositionForView(final AbsListView absListView, final View view) {
        int position = absListView.getPositionForView(view);
        if (absListView instanceof ListView) {
            position -= ((ListView) absListView).getHeaderViewsCount();
        }
        return position;
    }

    public static View getViewForPosition(final ListViewWrapper listViewWrapper, final int position) {
        int childCount = listViewWrapper.getChildCount();
        View downView = null;
        for (int i = 0; i < childCount && downView == null; i++) {
            View child = listViewWrapper.getChildAt(i);
            if (child != null && getPositionForView(listViewWrapper, child) == position) {
                downView = child;
            }
        }
        return downView;
    }

    public static View getViewForPosition(final AbsListView absListView, final int position) {
        int childCount = absListView.getChildCount();
        View downView = null;
        for (int i = 0; i < childCount && downView == null; i++) {
            View child = absListView.getChildAt(i);
            if (child != null && getPositionForView(absListView, absListView) == position) {
                downView = child;
            }
        }
        return downView;
    }
}
