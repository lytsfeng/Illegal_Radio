package com.ldkj.illegal_radio.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by john on 15-4-24.
 */
public class WorkService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
