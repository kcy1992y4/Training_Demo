package com.ryan_zhou.training_demo.utils.bitmap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author chaohao.zhou
 * @Description: bitmap 常用工具类
 * @date 2015/9/1 11:14
 * @copyright TCL-MIE
 */
public class BitmapUtil {

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // 只要边界，获取图片的原宽高
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // 图片原宽高
        int width = options.outWidth;
        int height = options.outHeight;
        // 位移
        int shift = 0;
        // 计算位移量
        if (reqWidth < width || reqHeight < height) {
            int halfWidth = width >> 1;
            int halfHeight = height >> 1;

            while (halfWidth >> shift > reqWidth && halfHeight >> shift > reqHeight) {
                shift++;
            }
        }
        // 通过计算的位移量，设置压缩比例
        options.inSampleSize = 1 << shift;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);
    }
}
