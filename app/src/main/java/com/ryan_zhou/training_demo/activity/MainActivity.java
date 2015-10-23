package com.ryan_zhou.training_demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ryan_zhou.training_demo.R;
import com.ryan_zhou.training_demo.activity.bitmap.ImageDetailActivity;
import com.ryan_zhou.training_demo.activity.bitmap.ImageGridActivity;
import com.ryan_zhou.training_demo.activity.service.ServiceMainActivity;


public class MainActivity extends Activity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonOnClicked(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_bitmap_activity: {
                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.button_service_activity: {
                Intent intent = new Intent(this, ServiceMainActivity.class);
                startActivity(intent);
                break;
            }
            default:
        }
    }
}
