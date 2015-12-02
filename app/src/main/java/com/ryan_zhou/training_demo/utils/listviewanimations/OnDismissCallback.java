package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.view.ViewGroup;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/2 16:07
 * @copyright TCL-MIE
 */
public interface OnDismissCallback {

    void onDismiss(ViewGroup listView, int[] reverseSortedPositions);
}
