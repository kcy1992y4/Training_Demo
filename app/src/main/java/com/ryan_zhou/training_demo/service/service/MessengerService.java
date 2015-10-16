package com.ryan_zhou.training_demo.service.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.ryan_zhou.training_demo.activity.service.ServiceFirstActivity;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/16 14:28
 * @copyright TCL-MIE
 */
public class MessengerService extends Service {

    private final static String TAG = "MessengerService";

    public final static int MSG_LOG_COUNT = 1;

    private int count = 0;

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOG_COUNT:
                    Log.d(TAG, "--> count : " + count);
                    count++;
                    Messenger removeMessenger = msg.replyTo;
                    Message message = obtainMessage();
                    message.what = ServiceFirstActivity.MSG;
                    try {
                        removeMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
