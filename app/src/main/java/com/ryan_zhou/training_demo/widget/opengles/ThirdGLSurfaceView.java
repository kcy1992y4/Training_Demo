package com.ryan_zhou.training_demo.widget.opengles;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/26 16:57
 * @copyright TCL-MIE
 */
public class ThirdGLSurfaceView extends GLSurfaceView {

    private final ThirdGLRenderer mRenderer;
    private static final float TOUCH_SCALE_FACTOR = 180.0f / 360;

    private float mPreviousX;
    private float mPreviousY;

    public ThirdGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        mRenderer = new ThirdGLRenderer();
        setRenderer(mRenderer);
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                if (y < getHeight() / 2) {
                    dx *= -1;
                }

                if (x > getWidth() / 2) {
                    dy *= -1;
                }

                mRenderer.setAngle(mRenderer.getAngle() + ((dx + dy) * TOUCH_SCALE_FACTOR));
        }
        mPreviousX = x;
        mPreviousY = y;

        return true;
    }
}
