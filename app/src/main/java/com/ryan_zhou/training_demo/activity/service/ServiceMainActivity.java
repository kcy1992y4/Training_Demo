package com.ryan_zhou.training_demo.activity.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.ryan_zhou.training_demo.R;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/14 9:43
 * @copyright TCL-MIE
 */
public class ServiceMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main_service);
    }

    public void buttonOnClicked(View view) {
        int id = view.getId();
        Intent intent1;
        switch (id) {
            case R.id.button_1:
                intent1 = new Intent(this, ServiceFirstActivity.class);
                this.startActivity(intent1);
                break;
            case R.id.button_2:
                intent1 = new Intent(this, ServiceSecondActivity.class);
                this.startActivity(intent1);
                break;
            case R.id.button_3:
                intent1 = new Intent(this, ServiceThirdActivity.class);
                this.startActivity(intent1);
                break;
            case R.id.button_4:
                intent1 = new Intent(this, ServiceFourthActivity.class);
                this.startActivity(intent1);
                break;
            case R.id.button_5:
                intent1 = new Intent(this, ServiceFifthActivity.class);
                this.startActivity(intent1);
                break;
            default:
                break;
        }
    }
}
