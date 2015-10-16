package com.ryan_zhou.training_demo.service.service;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/14 10:17
 * @copyright TCL-MIE
 */
public class LifeCycleIntentService extends IntentService {

    private final static String TAG = "LifeCycleIntentService";

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

    public LifeCycleIntentService() {
        super("LifeCycleIntentService");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "-->onCreate()");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "-->onStart()");
        super.onStart(intent, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "-->onHandleIntent()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "-->onBind()");
        return new Binder();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "-->onDestroy()");
        super.onDestroy();
    }
}
