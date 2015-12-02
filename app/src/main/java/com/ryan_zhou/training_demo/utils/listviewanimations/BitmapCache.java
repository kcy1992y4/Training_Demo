package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

import com.ryan_zhou.training_demo.utils.CommonUtils;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/28 19:08
 * @copyright TCL-MIE
 */
public class BitmapCache extends LruCache<Integer, Bitmap> {

    private static final int MEMORY_FACTOR = 2;

    public BitmapCache() {
        super((int) (Runtime.getRuntime().maxMemory()) / MEMORY_FACTOR);
    }

    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        final int bitmapSize = getBitmapSize(value);
        return bitmapSize == 0 ? 1 : bitmapSize;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private int getBitmapSize(Bitmap bitmap) {
        if (CommonUtils.hasKitKat()) {
           return bitmap.getAllocationByteCount();
        }
        if (CommonUtils.hasHoneycombMR1()) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
