package com.ryan_zhou.training_demo.activity.bitmap;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.ryan_zhou.training_demo.R;
import com.ryan_zhou.training_demo.fragment.bitmap.ImageDetailFragment;
import com.ryan_zhou.training_demo.provider.bitmap.Images;
import com.ryan_zhou.training_demo.utils.CommonUtils;
import com.ryan_zhou.training_demo.utils.bitmap.ImageCache;
import com.ryan_zhou.training_demo.utils.bitmap.ImageFetcher;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/9/25 17:06
 * @copyright TCL-MIE
 */
public class ImageDetailActivity extends FragmentActivity implements View.OnClickListener {

    private final static String TAG = "ImageDetailActivity";
    private final static String IMAGE_CACHE_DIR = "images";

    public final static String EXTRA_IMAGE = "extra_image";

    private ImageFetcher mImageFetcher;
    private ImagePagerAdapter mAdapter;
    private ViewPager mPager;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (BuildConfig.DEBUG) {
//            CommonUtils.enableStrictMode();
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_pager);

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        final int longest = (height > width ? height : width) / 2;

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f);

        mImageFetcher = new ImageFetcher(this, longest);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);

        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), Images.imageUrls.length);
        mPager = (ViewPager) this.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setPageMargin((int) getResources().getDimension(R.dimen.horizontal_page_margin));
        mPager.setOffscreenPageLimit(2);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (CommonUtils.hasHoneycomb()) {
            final ActionBar actionBar = getActionBar();

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);

            mPager.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                        actionBar.hide();
                    } else {
                        actionBar.show();
                    }
                }
            });

            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            actionBar.hide();

            final int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE, -1);
            if (extraCurrentItem != -1) {
                mPager.setCurrentItem(extraCurrentItem);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bitmap_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.clear_cache:
                mImageFetcher.clearCache();
                Toast.makeText(this, R.string.clear_cache_complete_toast, Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

    @Override
    public void onClick(View v) {
        final int vis = mPager.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        private final int mSize;

        public ImagePagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
        }

        @Override
        public Fragment getItem(int position) {
            return ImageDetailFragment.newInstance(Images.imageUrls[position]);
        }

        @Override
        public int getCount() {
            return mSize;
        }
    }
}
