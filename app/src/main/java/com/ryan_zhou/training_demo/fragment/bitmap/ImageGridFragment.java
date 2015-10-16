package com.ryan_zhou.training_demo.fragment.bitmap;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.ryan_zhou.training_demo.provider.bitmap.Images;
import com.ryan_zhou.training_demo.utils.bitmap.ImageFetcher;
import com.ryan_zhou.training_demo.widget.bitmap.RecyclingImageView;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/9/25 17:15
 * @copyright TCL-MIE
 */
public class ImageGridFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CHCHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageFetcher mImageFetcher;


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private int mActionBarHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams.MATCH_PARENT);

            TypedValue typedValue = new TypedValue();
            if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
                mActionBarHeight = TypedValue.complexToDimensionPixelOffset(typedValue.data, context.getResources()
                        .getDisplayMetrics());
            }
        }

        @Override
        public int getCount() {
            if (mNumColumns == 0) {
                return 0;
            }
            return Images.imageThumbUrls.length + mNumColumns;
        }

        @Override
        public Object getItem(int position) {
            return position < mNumColumns ? null : Images.imageThumbUrls[position - mNumColumns];
        }

        @Override
        public long getItemId(int position) {
            return position < mNumColumns ? 0 : position - mNumColumns;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (position < mNumColumns) ? 1 : 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position < mNumColumns) {
                if (convertView == null) {
                    convertView = new View(mContext);
                }
                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                return convertView;
            }

            ImageView imageView;
            if (convertView == null) {
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
            } else {
                imageView = (ImageView) convertView;
            }
            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(mImageViewLayoutParams);
            }
            mImageFetcher.loadImage(Images.imageThumbUrls[position], imageView);
            return convertView;
        }

        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams.MATCH_PARENT);
            mImageFetcher.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }
}
