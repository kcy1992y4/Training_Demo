// IRemoteService.aidl
package com.ryan_zhou.training_demo.service.service;

// Declare any non-default types here with import statements
import com.ryan_zhou.training_demo.bean.service.Student;

interface IRemoteService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    Map getMap(in String test_class,in Student student);
    void getStudent(in Student student);
    void newStudent(out Student student);
    void changeStudent(inout Student student);

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
