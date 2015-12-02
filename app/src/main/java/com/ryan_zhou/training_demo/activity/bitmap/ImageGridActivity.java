package com.ryan_zhou.training_demo.activity.bitmap;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.ryan_zhou.training_demo.BuildConfig;
import com.ryan_zhou.training_demo.fragment.bitmap.ImageGridFragment;
import com.ryan_zhou.training_demo.utils.CommonUtils;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/9/25 15:35
 * @copyright TCL-MIE
 */
public class ImageGridActivity extends FragmentActivity {
    private static final String TAG = "ImageGridActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (BuildConfig.DEBUG) {
//            CommonUtils.enableStrictMode();
//        }
        super.onCreate(savedInstanceState);
        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(android.R.id.content, new ImageGridFragment(), TAG);
            fragmentTransaction.commit();
        }
    }
}
