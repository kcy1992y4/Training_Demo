package com.ryan_zhou.training_demo.activity.scenes;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ryan_zhou.training_demo.R;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/12/8 15:09
 * @copyright TCL-MIE
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class ScenesSecondActivity extends Activity {

    private Button mButton;
    private ViewGroup mRootView;
    private Fade mFade;

    private boolean canAdd = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_scenes);

        mRootView = (ViewGroup) findViewById(R.id.mainLayout);
        mFade = new Fade(Fade.IN);
        mFade.addListener(new MyTransitionListener());

        mButton = (Button) this.findViewById(R.id.button_1);
        mButton.setOnClickListener(new ButtonOnClickListener(this));
    }

    private class ButtonOnClickListener implements View.OnClickListener {

        private Context mContext;
        private int i = 1;

        public ButtonOnClickListener(Context context) {
            mContext = context;
        }

        @Override
        public void onClick(View v) {
            if (canAdd) {
                // TODO 由于下面方法是记录执行该方法时mRootView的状态，所以在快速点击的时候，会引起记录了还在渐变的mRootView，引起在使用渐变效果的时候要注意一下
                TransitionManager.beginDelayedTransition(mRootView, mFade);
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout
                        .scene_linear_layout, mRootView, false);

                linearLayout.findViewById(R.id.button_1).setOnClickListener(new ButtonOnClickListener(mContext));
                mRootView.addView(linearLayout);
            }
        }
    }

    private class MyTransitionListener implements Transition.TransitionListener {

        @Override
        public void onTransitionStart(Transition transition) {
            canAdd = false;
        }

        @Override
        public void onTransitionEnd(Transition transition) {
            canAdd = true;
        }

        @Override
        public void onTransitionCancel(Transition transition) {
            canAdd = true;
        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    }
}
