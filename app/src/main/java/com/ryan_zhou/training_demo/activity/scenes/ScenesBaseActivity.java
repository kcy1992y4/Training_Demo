package com.ryan_zhou.training_demo.activity.scenes;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.ryan_zhou.training_demo.utils.scenes.Log;
import com.ryan_zhou.training_demo.utils.scenes.LogWrapper;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/12/9 15:49
 * @copyright TCL-MIE
 */
public class ScenesBaseActivity extends FragmentActivity {

    public static final String TAG = "ScenesBaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeLogging();
    }

    public void initializeLogging() {
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        // Wraps Android's native log framework
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);
        Log.i(TAG, "Ready");
    }
}
