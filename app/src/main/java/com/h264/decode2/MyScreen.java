package com.h264.decode2;

import android.app.Application;

/**
 * Created by Administrator on 2015/9/19.
 */
public class MyScreen extends Application{
    private static MyScreen instance;
    private boolean isScreenLand;
    public static MyScreen getInstance(){
        return instance;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
        isScreenLand = false;
    }
    public boolean getlandflag(){
        return isScreenLand;
    }
    public void setlandflag(boolean flag){
        isScreenLand = flag;
    }


}
