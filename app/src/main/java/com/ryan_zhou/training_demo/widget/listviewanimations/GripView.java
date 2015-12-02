package com.ryan_zhou.training_demo.widget.listviewanimations;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * TODO 具有这个类的功能还是不太清楚
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/25 13:50
 * @copyright TCL-MIE
 */
public class GripView extends View {

    public static final int DEFAULT_DOT_COLOR = android.R.color.darker_gray;
    public static final float DEFAULT_DOT_SIZE_RADIUS_DP = 2;
    public static final int DEFAULT_COLUMN_COUNT = 2;

    private static final int[] ATTRS = {android.R.attr.color};

    private final Paint mDotPaint;

    private float mDotSizeRadiusPx;

    private float mPaddingTop;

    private int mColumnCount = DEFAULT_COLUMN_COUNT;

    private int mRowCount;

    public GripView(Context context) {
        this(context, null);
    }

    public GripView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GripView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int color = getResources().getColor(DEFAULT_DOT_COLOR);
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
            color = a.getColor(0, color);
            a.recycle();
        }
        mDotPaint.setColor(color);
        Resources r = context.getResources();
        mDotSizeRadiusPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_DOT_SIZE_RADIUS_DP, r
                .getDisplayMetrics());
    }

    public void setColor(final int colorResId) {
        mDotPaint.setColor(getResources().getColor(colorResId));
    }

    public void setDotSizeRadiusPx(int dotSizeRadiusPx) {
        mDotSizeRadiusPx = dotSizeRadiusPx;
    }

    public void setColumnCount(int columnCount) {
        mColumnCount = columnCount;
        requestLayout();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        mRowCount = (int) ((height - getPaddingTop() - getPaddingBottom()) / (mDotSizeRadiusPx * 4));
        mPaddingTop = (height - mRowCount * mDotSizeRadiusPx * 2 - (mRowCount - 1) * mDotSizeRadiusPx * 2) / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.makeMeasureSpec(getPaddingLeft() + getPaddingRight() + (int) (mColumnCount * (mDotSizeRadiusPx * 4 - 2)), MeasureSpec.EXACTLY);
        super.onMeasure(width, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i =0; i < mColumnCount; i++) {
            float x = getPaddingLeft() + i * 2 * mDotSizeRadiusPx * 2;
            for (int j = 0; j < mRowCount; j++) {
                float y = mPaddingTop + j * 2 * mDotSizeRadiusPx * 2;
                canvas.drawCircle(x + mDotSizeRadiusPx, y + mDotSizeRadiusPx, mDotSizeRadiusPx, mDotPaint);
            }
        }
    }
}
