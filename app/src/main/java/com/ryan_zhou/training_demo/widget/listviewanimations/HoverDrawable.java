package com.ryan_zhou.training_demo.widget.listviewanimations;

import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;

import com.ryan_zhou.training_demo.utils.listviewanimations.BitmapUtils;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/11 17:08
 * @copyright TCL-MIE
 */
public class HoverDrawable extends BitmapDrawable {

    /**
     * View的top的 Y 左边，原来位置的Y坐标，会根据onScroll改变
     */
    private float mOriginalY;

    /**
     * 点击的Y坐标
     */
    private float mDownY;

    /**
     * The distance the {@code ListView} has been scrolling while this {@code HoverDrawable} is alive.
     */
    private float mScrollDistance;

    public HoverDrawable(View view, MotionEvent ev) {
        this(view, ev.getY());
    }

    public HoverDrawable(View view, float downY) {
        super(view.getResources(), BitmapUtils.getBitmapFromView(view));
        mOriginalY = view.getTop();
        mDownY = downY;

        setBounds(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }

    public void handleMoveEvent(MotionEvent event) {
        int top = (int) (mOriginalY - mDownY + event.getY() + mScrollDistance);
        // int top = (int) (mOriginalY - mDownY + event.getY());
        setTop(top);
    }

    /**
     * 在滚动的过程中原始mobileView的位置改变了，需要修改DownY和OriginalY
     * @param mobileViewTopY
     */
    public void onScroll(float mobileViewTopY) {
        mScrollDistance += mOriginalY - mobileViewTopY;
        // TODO: mScrollDistence改为下面这样
//        mDownY += mobileViewTopY - mOriginalY;
        mOriginalY = mobileViewTopY;
    }

    public boolean isMovingUpwards() {
        return mOriginalY > getBounds().top;
    }

    /**
     * @return 获取原来位置跟偏移位置的差值，向上移动为负值
     */
    public int getDeltaY() {
        return (int) (getBounds().top - mOriginalY);
    }

    public int getTop() {
        return getBounds().top;
    }

    public void setTop(int top) {
        setBounds(getBounds().left, top, getBounds().left + getIntrinsicWidth(), top + getIntrinsicHeight());
    }

    /**
     * 位置移动了，所以要修改初始的位置，初始点击的位置和初始状态左顶端的Y坐标
     * @param height
     */
    public void shift(int height) {
        int shiftSize = isMovingUpwards() ? -height : height;
        mOriginalY += shiftSize;
        mDownY += shiftSize;
    }
}
