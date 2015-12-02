package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.view.View;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/20 20:22
 * @copyright TCL-MIE
 */
public interface UndoCallback extends OnDismissCallback {

    View getPrimaryView(View view);

    View getUndoView(View view);

    void onUndoShow(View view, int position);

    void onUndo(View view, int position);

    void onDismiss(View view, int position);
}
