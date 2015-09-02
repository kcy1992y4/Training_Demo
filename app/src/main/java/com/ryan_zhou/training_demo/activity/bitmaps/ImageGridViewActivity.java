package com.ryan_zhou.training_demo.activity.bitmaps;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.ryan_zhou.training_demo.R;
import com.ryan_zhou.training_demo.utils.bitmap.BitmapWorkerTask;
import com.ryan_zhou.training_demo.widget.bitmap.AsyncDrawable;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/8/31 19:35
 * @copyright TCL-MIE
 */
public class ImageGridViewActivity extends Activity {

    private GridView mGridView;

    private ImageGridViewAdapter mImageGridViewAdapter;

    private Bitmap mPalceHolderBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.bitmaps_layout_image_grid_view);

        // 初始化控件
        mGridView = (GridView) this.findViewById(R.id.gridView);

        mImageGridViewAdapter = new ImageGridViewAdapter(this);
        mGridView.setAdapter(mImageGridViewAdapter);

        mPalceHolderBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
    }


    class ImageGridViewAdapter extends BaseAdapter {

        private int[] drawableIds;

        private void initDrawableIds() {
            drawableIds = new int[34];
            for (int i = 0; i < 34; i++) {
                drawableIds[i] = ImageGridViewActivity.this.getResources().getIdentifier("drawable" + i, "drawable", ImageGridViewActivity.this.getPackageName());
            }
        }

        public ImageGridViewAdapter(Context context) {
            initDrawableIds();
        }

        @Override
        public int getCount() {
            return drawableIds.length;
        }

        @Override
        public Object getItem(int position) {
            return drawableIds[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ImageGridViewActivity.this).inflate(R.layout.bitmaps_item_image_grid_view, parent, false);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);

            int reqWidth = 150;
            int reqHeight = 150;

            if (BitmapWorkerTask.cancelPotentialWork(drawableIds[position], imageView)) {
                final BitmapWorkerTask task = new BitmapWorkerTask(ImageGridViewActivity.this, imageView);
                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(getResources(), mPalceHolderBitmap, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(drawableIds[position], reqWidth, reqHeight);
            }
            return convertView;
        }
    }

}
