package com.ldkj.illegal_radio;

import android.app.Application;
import android.content.Context;

import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.Utils;

/**
 * Created by john on 15-4-23.
 */
public class OwnApplication extends Application {

    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        InitFolder();
    }
    public static Context getContext() {
        return context;
    }



    private void InitFolder() {
        Utils.onCreateFolder(Attribute.FOLDER_PATH_ROOT);
        Utils.onCreateFolder(Attribute.FOLDER_PATH_SOUND);
    }



}
