package com.ldkj.illegal_radio;

import android.app.Application;
import android.content.Context;

/**
 * Created by john on 15-4-23.
 */
public class OwnApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext() {
        return context;
    }




}
