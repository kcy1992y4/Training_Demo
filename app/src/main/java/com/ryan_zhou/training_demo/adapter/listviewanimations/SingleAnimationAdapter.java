package com.ryan_zhou.training_demo.adapter.listviewanimations;

import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/2 15:40
 * @copyright TCL-MIE
 */
public abstract class SingleAnimationAdapter extends AnimationAdapter {

    protected SingleAnimationAdapter(final BaseAdapter baseAdapter) {
        super(baseAdapter);
    }

    @Override
    public Animator[] getAnimators(final ViewGroup parent, final View view) {
        Animator animator = getAnimator(parent, view);
        return new Animator[]{animator};
    }

    protected abstract Animator getAnimator(final ViewGroup parent, final View view);
}
