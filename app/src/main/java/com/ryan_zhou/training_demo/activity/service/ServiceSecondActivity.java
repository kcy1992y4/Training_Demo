package com.ryan_zhou.training_demo.activity.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.ryan_zhou.training_demo.R;
import com.ryan_zhou.training_demo.service.service.LifeCycleService;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/14 10:58
 * @copyright TCL-MIE
 */
public class ServiceSecondActivity extends Activity {

    private final static String TAG = "ServiceSecondActivity";

    private Intent intent;

    private ServiceConnection myServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "--> onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "--> onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_common);
        intent = new Intent(this, LifeCycleService.class);
    }

    public void buttonOnClicked(View view){
        int id = view.getId();
        switch (id) {
            case R.id.button_1:
                startService(intent);
                break;
            case R.id.button_2:
                bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);
                break;
            case R.id.button_3:
                stopService(intent);
                break;
            case R.id.button_4:
                unbindService(myServiceConnection);
                break;
            default:
                break;
        }
    }
}
