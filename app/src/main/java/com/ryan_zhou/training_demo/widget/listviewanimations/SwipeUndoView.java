package com.ryan_zhou.training_demo.widget.listviewanimations;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/26 20:09
 * @copyright TCL-MIE
 */
public class SwipeUndoView extends FrameLayout {

    private View mPrimaryView;

    private View mUndoView;

    public SwipeUndoView(final Context context) {
        super(context);
    }

    public void setPrimaryView(View primaryView) {
        if (mPrimaryView != null) {
            removeView(mPrimaryView);
        }
        mPrimaryView = primaryView;
        addView(mPrimaryView);
    }

    public void setUndoView(View undoView) {
        if (mUndoView != null) {
            removeView(mUndoView);
        }
        mUndoView = undoView;
        mUndoView.setVisibility(GONE);
        addView(mUndoView);
    }

    public View getPrimaryView() {
        return mPrimaryView;
    }

    public View getUndoView() {
        return mUndoView;
    }
}
