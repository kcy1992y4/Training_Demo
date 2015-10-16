package com.ryan_zhou.training_demo.service.service;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.util.Log;

/**
 * @author chaohao.zhou
 * @Description: 不是线程安全的
 * @date 2015/10/15 16:58
 * @copyright TCL-MIE
 */
public class LocalService extends Service {

    private final static String TAG = "LocalService";
    private Looper mLocalLooper;
    private ServiceHandler mServiceHandler;
    private String message = "没有执行过";
    private boolean mAlive = false;
    private int count = 0;

    private final IBinder mBinder = new LocalBinder();

    private final class ServiceHandler extends Handler {

        private int count;

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            long endTime = System.currentTimeMillis() + 5 * 1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                        count++;
                        message = "执行次数：" + count;
                    } catch (Exception e) {
                    }
                }
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "-->onCreate()");
        mAlive = true;
        HandlerThread handlerThread = new HandlerThread("LocalServiceHandlerThread", Process
                .THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();

        mLocalLooper = handlerThread.getLooper();
        mServiceHandler = new ServiceHandler(mLocalLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "-->onStartCommand()");
        Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;
        mServiceHandler.sendMessage(message);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "-->onBind()");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "-->onDestroy()");
        mAlive = false;
        super.onDestroy();
    }

    public String getMessage() {
        return message;
    }

    public void showMessage() {
        Log.d(TAG, "--> count : " + count);
        count = count + 1;
    }

    public class LocalBinder extends Binder {
        public LocalService getService() {
            return LocalService.this;
        }
    }

    public boolean isAlive() {
        return mAlive;
    }

    public void setAlive(boolean alive) {
        this.mAlive = alive;
    }
}
