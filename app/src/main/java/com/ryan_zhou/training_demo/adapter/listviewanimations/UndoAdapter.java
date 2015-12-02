package com.ryan_zhou.training_demo.adapter.listviewanimations;

import android.view.View;
import android.view.ViewGroup;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/11 15:12
 * @copyright TCL-MIE
 */
public interface UndoAdapter {

    View getUndoView(final int position, final View convertView, final ViewGroup parent);

    View getUndoClickView(final View view);
}
