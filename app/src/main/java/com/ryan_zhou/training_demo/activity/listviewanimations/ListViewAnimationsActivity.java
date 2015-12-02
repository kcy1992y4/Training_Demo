package com.ryan_zhou.training_demo.activity.listviewanimations;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ryan_zhou.training_demo.R;
import com.ryan_zhou.training_demo.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/28 13:25
 * @copyright TCL-MIE
 */
public class ListViewAnimationsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(CommonUtils.hasKitKat()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_listview_animations);
    }

    public void onGoogleCardsExampleClicked(View view) {
        Intent intent = new Intent(this, GoogleCardsActivity.class);
        startActivity(intent);
    }

    public void onGridViewExampleClicked(View view) {
//        Intent intent = new Intent(this, GridViewActivity.class);
//        startActivity(intent);
    }

    public void onAppearanceClicked(View view) {
//        Intent intent = new Intent(this, AppearanceExamplesActivity.class);
//        startActivity(intent);
    }

    public void onItemManipulationClicked(View view) {
        Intent intent = new Intent(this, ItemManipulationExamplesActivity.class);
        startActivity(intent);
    }

    public void onSLHClicked(View view) {
//        Intent intent = new Intent(this, StickyListHeadersActivity.class);
//        startActivity(intent);
    }
}
