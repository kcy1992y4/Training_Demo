package com.ryan_zhou.training_demo.activity.listviewanimations;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ryan_zhou.training_demo.R;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/11 14:34
 * @copyright TCL-MIE
 */
public class ItemManipulationExamplesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examples_itemmanipulations);
    }

    public void onDynamicListViewClicked(View view) {
        Intent intent = new Intent(this, DynamicListViewActivity.class);
        startActivity(intent);
    }

    public void onExpandListItemAdapterClicked(View view) {
//        Intent intent = new Intent(this, ExpandableListItemActivity.class);
//        startActivity(intent);
    }
}
