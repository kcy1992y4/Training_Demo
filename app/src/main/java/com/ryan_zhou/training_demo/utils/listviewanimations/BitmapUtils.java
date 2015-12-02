package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/11 17:14
 * @copyright TCL-MIE
 */
public class BitmapUtils {

    private BitmapUtils() {}

    public static Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
