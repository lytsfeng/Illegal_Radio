package com.ldkj.illegal_radio.activitys.base;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.activitys.MainActivity;
import com.ldkj.illegal_radio.activitys.SettingActivity;

/**
 * Created by john on 15-4-27.
 */
public class ActivityFrame extends ActivityBase {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_monitor:
                openActivity(MainActivity.class);
                break;
            case R.id.action_settings:
                openActivity(SettingActivity.class);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
