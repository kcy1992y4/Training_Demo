package com.ryan_zhou.training_demo.bean.service;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/19 11:14
 * @copyright TCL-MIE
 */
public class Student implements Parcelable {

    private final static String TAG = "Student";

    private int mAge;
    private String mName;

    public Student() {
        this.mAge = 0;
        this.mName = "默认";
    }

    public Student(Parcel parcel) {
        readFromParcel(parcel);
    }

    public final static Parcelable.Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel source) {
            return new Student(source);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mAge);
        dest.writeString(mName);
    }

    public void readFromParcel(Parcel in) {
        this.mAge = in.readInt();
        this.mName = in.readString();
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        this.mAge = age;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }
}
