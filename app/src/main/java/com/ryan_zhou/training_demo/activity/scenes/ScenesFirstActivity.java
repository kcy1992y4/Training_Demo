package com.ryan_zhou.training_demo.activity.scenes;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;

import com.ryan_zhou.training_demo.R;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/12/8 15:06
 * @copyright TCL-MIE
 */
public class ScenesFirstActivity extends Activity {

    Scene mAScene;
    Scene mAnotherScene;

    ViewGroup mSceneRoot;

    Transition mFadeTransition;

    Transition mFadeTransitionSet;

    boolean flag = true;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_scenes);

        mSceneRoot = (ViewGroup) this.findViewById(R.id.scene_root);
        mAScene = Scene.getSceneForLayout(mSceneRoot, R.layout.scene_a, this);
        mAnotherScene = Scene.getSceneForLayout(mSceneRoot, R.layout.scene_another, this);

        mFadeTransition = TransitionInflater.from(this).inflateTransition(R.transition.fade_transition);
        mFadeTransitionSet = TransitionInflater.from(this).inflateTransition(R.transition.fade_transitions);

        this.findViewById(R.id.button_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    TransitionManager.go(mAnotherScene, mFadeTransitionSet);
                } else {
                    TransitionManager.go(mAScene, mFadeTransitionSet);
                }
                flag = !flag;
            }
        });
    }
}
