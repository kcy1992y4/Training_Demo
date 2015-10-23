package com.ryan_zhou.training_demo.fragment.bitmap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ryan_zhou.training_demo.R;
import com.ryan_zhou.training_demo.activity.bitmap.ImageDetailActivity;
import com.ryan_zhou.training_demo.utils.bitmap.CommonUtils;
import com.ryan_zhou.training_demo.utils.bitmap.ImageFetcher;
import com.ryan_zhou.training_demo.utils.bitmap.ImageWorker;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/21 16:50
 * @copyright TCL-MIE
 */
public class ImageDetailFragment extends Fragment {

    private final static String TAG = "ImageDetailFragment";

    private final static String IMAGE_DATA_EXTRA = "image_data_extra";

    private String mImageUrl;
    private ImageView mImageView;
    private ImageFetcher mImageFetcher;

    public ImageDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (ImageDetailActivity.class.isInstance(getActivity())) {
            mImageFetcher = ((ImageDetailActivity)getActivity()).getImageFetcher();
            mImageFetcher.loadImage(mImageUrl, mImageView);
        }
        if (View.OnClickListener.class.isInstance(getActivity()) && CommonUtils.hasHoneycomb()) {
            mImageView.setOnClickListener((View.OnClickListener)getActivity());
        }
    }

    public static ImageDetailFragment newInstance(String imageUrl) {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imageUrl);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            ImageWorker.cancelWork(mImageView);
            mImageView.setImageDrawable(null);
        }
    }
}
