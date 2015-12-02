package com.ryan_zhou.training_demo.adapter.listviewanimations;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/2 15:42
 * @copyright TCL-MIE
 */
public class SwingBottomInAnimationAdapter extends SingleAnimationAdapter {

    private static final String TRANSLATION_Y = "translationY";

    public SwingBottomInAnimationAdapter(final BaseAdapter baseAdapter) {
        super(baseAdapter);
    }

    @Override
    protected Animator getAnimator(ViewGroup parent, View view) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, parent.getMeasuredHeight() >> 1, 0);
    }

}
