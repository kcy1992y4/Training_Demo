package com.ryan_zhou.training_demo.activity.scenes;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ryan_zhou.training_demo.BuildConfig;
import com.ryan_zhou.training_demo.R;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/12/8 13:52
 * @copyright TCL-MIE
 */
public class ScenesMainActivity extends Activity {

    Scene mAScene;
    Scene mAnotherScene;

    ViewGroup mSceneRoot;

    Transition mFadeTransition;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scenes);
    }

    public void ButtonOnClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.button_1:
                intent = new Intent(this, ScenesFirstActivity.class);
                startActivity(intent);
                break;
            case R.id.button_2:
                intent = new Intent(this, ScenesSecondActivity.class);
                startActivity(intent);
                break;
            case R.id.button_3:
                intent = new Intent(this, ScenesThirdActivity.class);
                startActivity(intent);
                break;
            case R.id.button_4:
                intent = new Intent(this, ScenesFourthActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
