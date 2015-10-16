package com.ryan_zhou.training_demo.service.service;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.util.Log;

import java.util.Random;

public class LifeCycleService extends Service {

    private final static String TAG = "LifeCycleService";
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            long endTime = System.currentTimeMillis() + 5*1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                        Log.d(TAG, "--> 执行完毕");
                    } catch (Exception e) {
                    }
                }
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    public LifeCycleService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "-->onCreate()");
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "-->onStartCommand()");

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    public class LocalBinder extends Binder {

        private int RandomCount;

        private LocalBinder() {
            super();
            RandomCount = new Random().nextInt(10);
        }

        public int getRandomCount() {
            return RandomCount;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "-->onBind()");

        return new LocalBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "-->onUnbind()");
        return true;
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
