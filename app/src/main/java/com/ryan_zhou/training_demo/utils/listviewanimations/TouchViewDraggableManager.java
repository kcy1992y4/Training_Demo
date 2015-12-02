package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.view.View;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/27 10:38
 * @copyright TCL-MIE
 */
public class TouchViewDraggableManager implements DraggableManager {

    private final int mTouchViewResId;

    public TouchViewDraggableManager(int touchViewResId) {
        mTouchViewResId = touchViewResId;
    }

    @Override
    public boolean isDraggable(View view, int position, float x, float y) {
        View touchView = view.findViewById(mTouchViewResId);
        if (touchView != null) {
            boolean xHit = touchView.getLeft() <= x && touchView.getRight() >= x;
            boolean yHit = touchView.getTop() <= y && touchView.getBottom() >= y;
            return xHit && yHit;
        } else {
            return false;
        }
    }
}
