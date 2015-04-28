package com.ldkj.illegal_radio.views;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;


/**
 * 自定义Toast　实现不会持续显示Toast
 * Created by john on 15-4-10.
 */
public class CustomToast {

    private static Toast toast;
    private static Handler mHandler = new Handler();
    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            toast.cancel();
        }
    };



    public static void showToast(Context mContext, String text, int duration) {

        mHandler.removeCallbacks(runnable);
        if (toast != null)
            toast.setText(text);
        else
            toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        mHandler.postDelayed(runnable, duration);

        toast.show();
    }

    public static void showToast(Context mContext, int resId, int duration) {
        showToast(mContext, mContext.getResources().getString(resId), duration);
    }

}
