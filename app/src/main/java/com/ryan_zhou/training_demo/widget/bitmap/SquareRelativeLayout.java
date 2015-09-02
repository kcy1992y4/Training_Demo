package com.ryan_zhou.training_demo.widget.bitmap;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/9/2 10:54
 * @copyright TCL-MIE
 */
public class SquareRelativeLayout extends RelativeLayout {
    public SquareRelativeLayout(Context context) {
        super(context);
    }

    public SquareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // isLayoutModeOptical 可能为true，需要计算光学边界
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        // 获取经计算的宽度
        int sideSize = getMeasuredWidth();
        // 将计算的宽度确定为Excatly
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(sideSize, MeasureSpec.EXACTLY);
        // 调用父类
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
