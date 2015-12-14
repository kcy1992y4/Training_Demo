package com.ryan_zhou.training_demo.fragment.scenes;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ryan_zhou.training_demo.R;
import com.ryan_zhou.training_demo.utils.scenes.Log;
import com.ryan_zhou.training_demo.widget.scenes.ChangeColor;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/12/9 14:55
 * @copyright TCL-MIE
 */
public class CustomTransitionFragment extends Fragment implements View.OnClickListener {


    private static final String TAG = "CustomTransitionFragment";

    private static final String STATE_CURRENT_SCENE = "current_scene";

    private Scene[] mScenes;

    private int mCurrentScene;

    private Transition mTransition;

    public CustomTransitionFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom_transition, container, false);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Context context = getActivity();
        FrameLayout container = (FrameLayout) view.findViewById(R.id.container);
        view.findViewById(R.id.show_next_scene).setOnClickListener(this);
        if (null != savedInstanceState) {
            mCurrentScene = savedInstanceState.getInt(STATE_CURRENT_SCENE);
        }

        mScenes = new Scene[]{Scene.getSceneForLayout(container, R.layout.scene1, context), Scene.getSceneForLayout
                (container, R.layout.scene2, context), Scene.getSceneForLayout(container, R.layout.scene3, context)};

        mTransition = new ChangeColor();

        TransitionManager.go(mScenes[mCurrentScene % mScenes.length]);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_SCENE, mCurrentScene);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_next_scene: {
                Log.i(TAG, "Transitioning to scene #" + mCurrentScene);
                mCurrentScene = (mCurrentScene + 1) % mScenes.length;
                mTransition.setDuration(2000);
                TransitionManager.go(mScenes[mCurrentScene], mTransition);
            }
        }
    }
}
