package com.ryan_zhou.training_demo.utils.bitmap;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import com.ryan_zhou.training_demo.BuildConfig;

import java.io.FileDescriptor;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/9/15 16:42
 * @copyright TCL-MIE
 */
public class ImageResizer extends ImageWorker {

    private static final String TAG = "ImageResizer";
    protected int mImageWidth;
    protected int mImageHeight;

    public ImageResizer(Context context, int imageWidth, int imageHeight) {
        super(context);
        setImageSize(imageWidth, imageHeight);
    }

    public ImageResizer(Context context, int imageSize) {
        super(context);
        setImageSize(imageSize);
    }

    @Override
    protected Bitmap processBitmap(Object data) {
        return processBitmap(Integer.parseInt(String.valueOf(data)));
    }

    private Bitmap processBitmap(int resId) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "processBitmap - " + resId);
        }
        return decodeSampleBitmapFromResource(mResources, resId, mImageWidth, mImageHeight, getImageCache());
    }

    public void setImageSize(int size) {
        setImageSize(size, size);
    }

    public void setImageSize(int width, int height) {
        mImageWidth = width;
        mImageHeight = height;
    }

    public static Bitmap decodeSampleBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight,
                                                        ImageCache cache) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        if (CommonUtils.hasHoneycomb()) {
            addInBitmapOptions(options, cache);
        }

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeSampleBitmapFromFile(String fileName, int reqWidth, int reqHeight, ImageCache
            imageCache) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        if (CommonUtils.hasHoneycomb()) {
            addInBitmapOptions(options, imageCache);
        }

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(fileName, options);
    }

    public static Bitmap decodeSampleBitmapFromDescriptor(FileDescriptor fileDescriptor, int reqWidth, int reqHeight,
                                                          ImageCache imageCache) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        if (CommonUtils.hasHoneycomb()) {
            addInBitmapOptions(options, imageCache);
        }

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void addInBitmapOptions(BitmapFactory.Options options, ImageCache imageCache) {
        options.inMutable = true;

        if (imageCache != null) {
            Bitmap inBitmap = imageCache.getBitmapFromReusableSet(options);

            if (inBitmap != null) {
                options.inBitmap = inBitmap;
            }
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int shift = 0;

        if (width > reqWidth || height > reqHeight) {
            final int halfWidth = width >> 1;
            final int halfHeight = height >> 1;

            while ((halfWidth >> shift) > reqWidth && (halfHeight >> shift > reqHeight)) {
                ++shift;
            }

            long totalPixels = width * height >> shift;

            final long totalReqPixelsCap = reqWidth * reqHeight << 1;
            while (totalPixels > totalReqPixelsCap) {
                ++shift;
                totalPixels >>= 1;
            }
        }
        return 1 << shift;
    }

}
