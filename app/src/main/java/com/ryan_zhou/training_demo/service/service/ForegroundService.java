package com.ryan_zhou.training_demo.service.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ryan_zhou.training_demo.R;
import com.ryan_zhou.training_demo.activity.MainActivity;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/16 16:42
 * @copyright TCL-MIE
 */
public class ForegroundService extends Service {

    @Override
    public void onCreate() {
        Notification notification = new Notification(R.drawable.ic_launcher, "呵呵",
                System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, "呵呵1",
                "呵呵1", pendingIntent);
        startForeground(1, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
