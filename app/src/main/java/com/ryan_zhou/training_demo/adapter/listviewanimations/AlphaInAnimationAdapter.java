package com.ryan_zhou.training_demo.adapter.listviewanimations;

import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/27 10:33
 * @copyright TCL-MIE
 */
public class AlphaInAnimationAdapter extends AnimationAdapter {

    public AlphaInAnimationAdapter(BaseAdapter baseAdapter) {
        super(baseAdapter);
    }

    @Override
    public Animator[] getAnimators(ViewGroup parent, View view) {
        return new Animator[0];
    }
}
