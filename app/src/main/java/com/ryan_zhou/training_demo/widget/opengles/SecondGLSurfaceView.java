package com.ryan_zhou.training_demo.widget.opengles;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/26 16:56
 * @copyright TCL-MIE
 */
public class SecondGLSurfaceView extends GLSurfaceView {

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;

    private final SecondGLRenderer mRenderer;

    private float mPreviousX;
    private float mPreviousY;

    public SecondGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        mRenderer = new SecondGLRenderer();
        setRenderer(mRenderer);

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
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
                    dx = dx * -1;
                }

                if (x > getWidth() / 2) {
                    dy = dy * -1;
                }

                mRenderer.setAngle(mRenderer.getAngle() + ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender();
        }
        mPreviousX = x;
        mPreviousY = y;

        return true;
    }
}
