package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.view.View;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/12 10:23
 * @copyright TCL-MIE
 */
public interface DraggableManager {
    boolean isDraggable(View view, int position, float x, float y);
}
