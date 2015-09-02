package com.ryan_zhou.training_demo.utils.bitmap;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.ryan_zhou.training_demo.widget.bitmap.AsyncDrawable;

import java.lang.ref.WeakReference;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/9/2 14:59
 * @copyright TCL-MIE
 */
public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

    private Context mContext;
    private final WeakReference<ImageView> mImageViewWeakReference;

    // 加载的resId
    private int resId;

    public BitmapWorkerTask(Context context, ImageView imageView) {
        mContext = context;
        mImageViewWeakReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
        // params[0] resId   params[1] reqWidth   params[2] reqHeight
        resId = params[0];
        return BitmapUtil.decodeSampledBitmapFromResource(mContext.getResources(), params[0], params[1], params[2]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (mImageViewWeakReference != null && bitmap != null) {
            final ImageView imageView = mImageViewWeakReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public int getResId() {
        return resId;
    }

    public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = BitmapWorkerTask.getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.getResId();
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == 0 || bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }
}
