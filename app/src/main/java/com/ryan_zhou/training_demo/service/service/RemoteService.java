package com.ryan_zhou.training_demo.service.service;

import android.app.Service;
import android.content.Intent;
import android.os.*;

import com.ryan_zhou.training_demo.IRemoteService;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/16 17:42
 * @copyright TCL-MIE
 */
public class RemoteService extends Service {

    private final IRemoteService.Stub mBinder = new IRemoteService.Stub() {
        @Override
        public int getPid() throws RemoteException {
            return android.os.Process.myPid();
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            //Do nothing
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
