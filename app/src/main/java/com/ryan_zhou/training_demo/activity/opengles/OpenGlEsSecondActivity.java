package com.ryan_zhou.training_demo.activity.opengles;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.ryan_zhou.training_demo.widget.opengles.FirstGLSurfaceView;
import com.ryan_zhou.training_demo.widget.opengles.SecondGLSurfaceView;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/26 14:08
 * @copyright TCL-MIE
 */
public class OpenGlEsSecondActivity extends Activity {
    private GLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLView = new SecondGLSurfaceView(this);
        setContentView(mGLView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }
}
