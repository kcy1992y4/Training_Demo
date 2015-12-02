package com.ryan_zhou.training_demo.widget.opengles;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/23 14:33
 * @copyright TCL-MIE
 */
public class FirstGLSurfaceView extends GLSurfaceView {

    private final FirstGLRenderer mRenderer;

    public FirstGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        mRenderer = new FirstGLRenderer();
        setRenderer(mRenderer);
    }
}
