package com.ryan_zhou.training_demo.widget.bitmap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.ryan_zhou.training_demo.utils.bitmap.BitmapWorkerTask;

import java.lang.ref.WeakReference;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/9/2 15:44
 * @copyright TCL-MIE
 */
public class AsyncDrawable extends BitmapDrawable {

    private final WeakReference<BitmapWorkerTask> mBitmapWorkerTaskWeakReference;

    public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
        super(res, bitmap);
        mBitmapWorkerTaskWeakReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
    }

    public BitmapWorkerTask getBitmapWorkerTask() {
        return mBitmapWorkerTaskWeakReference.get();
    }
}
