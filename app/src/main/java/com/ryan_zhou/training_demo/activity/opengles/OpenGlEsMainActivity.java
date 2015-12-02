package com.ryan_zhou.training_demo.activity.opengles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ryan_zhou.training_demo.R;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/23 14:18
 * @copyright TCL-MIE
 */
public class OpenGlEsMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main_open_gl_es);
    }

    public void buttonOnClicked(View view) {
        int id = view.getId();
        Intent intent;
        switch (id) {
            case R.id.button_1:
                intent = new Intent(this, OpenGlEsFirstActivity.class);
                startActivity(intent);
                break;
            case R.id.button_2:
                intent = new Intent(this, OpenGlEsSecondActivity.class);
                startActivity(intent);
                break;
            case R.id.button_3:
                intent = new Intent(this, OpenGlEsThirdActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
