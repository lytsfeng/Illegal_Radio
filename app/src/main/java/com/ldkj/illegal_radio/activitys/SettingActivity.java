package com.ldkj.illegal_radio.activitys;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.activitys.base.ActivityFrame;
import com.ldkj.illegal_radio.fragments.AboutFragment;
import com.ldkj.illegal_radio.fragments.DBFragment;
import com.ldkj.illegal_radio.fragments.DeviceFragment;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends ActivityFrame {



    private static Map<Integer,Fragment> fragmentMap = new HashMap<Integer,Fragment>(){
        {
            put(R.id.set_db,new DBFragment());
            put(R.id.set_device,new DeviceFragment());
            put(R.id.set_about,new AboutFragment());
        }
    };
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_setting);
        RadioGroup _Group = (RadioGroup) findViewById(R.id.radio);
        _Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                showFreagment(R.id.sett_fragment, fragmentMap.get(checkedId));
            }
        });
        _Group.check(R.id.set_db);
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }
}
