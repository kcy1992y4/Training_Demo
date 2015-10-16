package com.ryan_zhou.training_demo.service.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/14 15:29
 * @copyright TCL-MIE
 */
public class InteractService2 extends Service {

    private final static String TAG = "InteractService2";
    private Intent intent1;
    private ServiceConnection myServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected --> onServiceConnected");
            if(service instanceof LifeCycleService.LocalBinder) {
                LifeCycleService.LocalBinder localBinder = (LifeCycleService.LocalBinder)service;
                Log.d(TAG, "myServiceConnection --> onServiceConnected:::localBinder.getRandomCount() : " + localBinder.getRandomCount());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "-->onCreate()");
        intent1 = new Intent(this, LifeCycleService.class);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "-->onStartCommand()");
        bindService(intent1, myServiceConnection, BIND_AUTO_CREATE);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "-->onBind()");
        return new Binder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "-->onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "-->onRebind()");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "-->onDestroy()");
        super.onDestroy();
    }
}
