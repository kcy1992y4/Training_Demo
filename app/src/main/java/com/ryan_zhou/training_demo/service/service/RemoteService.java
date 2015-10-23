package com.ryan_zhou.training_demo.service.service;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.util.Log;

import com.ryan_zhou.training_demo.bean.service.Student;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/16 17:42
 * @copyright TCL-MIE
 */
public class RemoteService extends Service {

    private final static String TAG = "RemoteService";

    private int id = 0;

    private final IRemoteService.Stub mBinder = new IRemoteService.Stub() {
        @Override
        public Map getMap(String test_class, Student student) throws RemoteException {
            Log.d(TAG, "-->getMap");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("class", "五年级");
            map.put("age", student.getAge());
            map.put("name", student.getName());
            return map;
        }

        @Override
        public void getStudent(Student student) throws RemoteException {
            Log.d(TAG, "-->getStudent:::student.name + " + student.getName());
            student.setAge(18);
            student.setName("小花");
        }

        @Override
        public void newStudent(Student student) throws RemoteException {
            Log.d(TAG, "-->newStudent:::student.name + " + student.getName());
            student.setAge(18);
            student.setName("小花");
        }

        @Override
        public void changeStudent(Student student) throws RemoteException {
            Log.d(TAG, "-->changeStudent:::student.name + " + student.getName());
            student.setAge(18);
            student.setName("小花");
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
