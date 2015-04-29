package com.ldkj.illegal_radio.activitys;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.activitys.base.ActivityFrame;
import com.ldkj.illegal_radio.fragments.MapFragment;
import com.ldkj.illegal_radio.fragments.OnFragmentInteractionListener;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActivityFrame implements OnFragmentInteractionListener{

    private static final String CONFIG_NAME = "com_ldkj_illegalradio_mainactivity";
    private static final String CONFIG_KEY_FRAGMENT = "fragment";
    private static Map<Integer,Fragment> fragmentMap = new HashMap<Integer,Fragment>(){
        {
            put(1,new MapFragment());
        }
    };

    private int fragmentid =1;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(CONFIG_NAME, Context.MODE_APPEND);
    }

    private int getFragment(){
        fragmentid = preferences.getInt(CONFIG_KEY_FRAGMENT,fragmentid);
        return fragmentid;
    }
    private void saveFragment(){
        preferences.edit().putInt(CONFIG_KEY_FRAGMENT,fragmentid).commit();
    }






    @Override
    protected void onResume() {
        showFreagment(R.id.main_fragment,fragmentMap.get(getFragment()));
        super.onResume();
    }
    @Override
    protected void onPause() {
        saveFragment();
        removeFreagment(R.id.main_fragment);
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
