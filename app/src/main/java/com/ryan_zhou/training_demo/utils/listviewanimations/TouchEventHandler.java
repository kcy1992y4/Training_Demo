package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.view.MotionEvent;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/2 18:55
 * @copyright TCL-MIE
 */
public interface TouchEventHandler {

    boolean onTouchEvent(MotionEvent event);

    boolean isInteracting();

}
