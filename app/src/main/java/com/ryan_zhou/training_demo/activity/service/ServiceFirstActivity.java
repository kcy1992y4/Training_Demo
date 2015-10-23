package com.ryan_zhou.training_demo.activity.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.ryan_zhou.training_demo.R;
import com.ryan_zhou.training_demo.service.service.ForegroundService;
import com.ryan_zhou.training_demo.service.service.IRemoteService;
import com.ryan_zhou.training_demo.service.service.InteractService1;
import com.ryan_zhou.training_demo.service.service.InteractService2;
import com.ryan_zhou.training_demo.service.service.LocalService;
import com.ryan_zhou.training_demo.service.service.RemoteService;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/14 10:31
 * @copyright TCL-MIE
 */
public class ServiceFirstActivity extends Activity {

    private final static String TAG = "ServiceFirstActivity";

    public final static int MSG = 1;

    private LocalService localService;

    private Intent intent1;
    private Intent intent2;
    private Intent intent3;
    private Intent intent4;

    private class ServiceFirstActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG:
                    Log.d(TAG, "互通了");
                    break;
                default:
                    break;
            }
        }
    }

    private Messenger mMessenger;

    private Messenger mServiceMessenger;

    // 链接LocalService的Connection
    private ServiceConnection myServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "myServiceConnection --> onServiceConnected");
            if (service instanceof LocalService.LocalBinder) {
                LocalService.LocalBinder localBinder = (LocalService.LocalBinder) service;
                localService = localBinder.getService();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "myServiceConnection1 --> onServiceDisconnected");
            localService.setAlive(false);
        }
    };

    private ServiceConnection messengerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "Service has unexpectedly disconnected");
            mServiceMessenger = null;
        }
    };

    private IRemoteService mIRemoteService;
    private ServiceConnection remoteServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIRemoteService = IRemoteService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "Service has unexpectedly disconnected");
            mIRemoteService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_common);
        intent1 = new Intent(this, RemoteService.class);
        intent2 = new Intent(this, InteractService1.class);
        intent3 = new Intent(this, InteractService2.class);
        intent4 = new Intent(this, ForegroundService.class);

        mMessenger = new Messenger(new ServiceFirstActivityHandler());
    }

    public void buttonOnClicked(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_1:
                startService(intent1);
                break;
            case R.id.button_2:
                bindService(intent1, remoteServiceConnection, BIND_AUTO_CREATE);
                break;
            case R.id.button_3:
                stopService(intent1);
                break;
            case R.id.button_4:
                unbindService(remoteServiceConnection);
                break;
            case R.id.button_5:
                startService(intent1);
                break;
            case R.id.button_6:
                stopService(intent1);
                break;
            case R.id.button_7:
                startService(intent4);
                break;
            case R.id.button_8:
                stopService(intent4);
                break;
            case R.id.button_9:
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (int i = 0; i < 1000; i++) {
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Message message = new Message();
//                                    message.what = MessengerService.MSG_LOG_COUNT;
//                                    message.replyTo = mMessenger;
//                                    try {
//                                        mServiceMessenger.send(message);
//                                    } catch (RemoteException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }).start();
//                        }
//                    }
//                }).start();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (int i = 0; i < 10000; i++) {
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        Log.d(TAG, "-->从RemoteService中获取的ID：" + mIRemoteService.getPid());
//                                    } catch (RemoteException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }).start();
//                        }
//                    }
//                }).start();
                break;
            case R.id.button_10:

                break;
            default:
                break;
        }
    }
}
