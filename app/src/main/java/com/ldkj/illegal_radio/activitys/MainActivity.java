package com.ldkj.illegal_radio.activitys;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.amap.api.maps.model.LatLng;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.activitys.base.ActivityFrame;
import com.ldkj.illegal_radio.events.NetEvent;
import com.ldkj.illegal_radio.fragments.MapFragment;
import com.ldkj.illegal_radio.fragments.OnFragmentInteractionListener;
import com.ldkj.illegal_radio.fragments.SpecFragment;
import com.ldkj.illegal_radio.services.LocationService;
import com.ldkj.illegal_radio.services.WorkService;
import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;


public class MainActivity extends ActivityFrame implements OnFragmentInteractionListener {

    private static final String CONFIG_NAME = "com_ldkj_illegalradio_mainactivity";
    private static final String CONFIG_KEY_FRAGMENT = "fragment";
    private static Map<Integer, Fragment> fragmentMap = new HashMap<Integer, Fragment>() {
        {
            put(R.id.action_map, new MapFragment());
            put(R.id.action_spec, new SpecFragment());
        }
    };

    private int currentFragmentID = R.id.action_map;
    private SharedPreferences preferences;
    private FloatingActionsMenu floatingActionsMenu;
    private WorkService workService;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            workService = ((WorkService.LocalBinder) service).getService();
            workService.bindActivity(MainActivity.this);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            workService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        preferences = getSharedPreferences(CONFIG_NAME, Context.MODE_APPEND);
        EventBus.getDefault().register(this);
    }

    public void OnClick(View v) {
        int _id = v.getId();
        floatingActionsMenu.toggle();
        if (currentFragmentID == _id) {
            return;
        }
        currentFragmentID = _id;
        showFreagment(R.id.main_fragment, fragmentMap.get(currentFragmentID));
        saveFragment();
    }

    private int getFragment() {
        currentFragmentID = preferences.getInt(CONFIG_KEY_FRAGMENT, currentFragmentID);
        return currentFragmentID;
    }

    private void saveFragment() {
        preferences.edit().putInt(CONFIG_KEY_FRAGMENT, currentFragmentID).commit();
    }

    @Override
    protected void onResume() {

        showFreagment(R.id.main_fragment, fragmentMap.get(getFragment()));
        bindService(new Intent(this, WorkService.class), connection, BIND_AUTO_CREATE);
        startService(LocationService.class);
        super.onResume();
    }

    @Override
    protected void onPause() {
        saveFragment();
        stopService(LocationService.class);
        unbindService(connection);
        removeFreagment(R.id.main_fragment);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    // 事件接收
    public void onupdate(float[] date) {
        Fragment _Fragment = getFreagment(R.id.main_fragment);
        if(_Fragment instanceof SpecFragment){
            ((SpecFragment)_Fragment).update(date);
        }

        Log.i(Attribute.TAG, "--------------------------------------------------------------");
        int _date = date.length;
        for (int i = 0 ; i < _date; i++){
            if(i > 0 & i < _date){
                if(date[i] >= 20 ){
                    if(date[i] > date[i-1] && date[i] > date[i+1]){
                        float _freq = (float) ((i) * 0.025 + 88.0);
                        Log.i(Attribute.TAG, "频率： "+_freq);
                    }
                }
            }
        }
        Log.i(Attribute.TAG, "///////////////////////////////////////////////////////////////");


    }


    public void onEventMainThread(float[] s){
        onupdate(s);
    }


    public void onEventMainThread(NetEvent netEvent){
        if(netEvent.isConn()){

        }
    }

    public void onEventMainThread(LatLng latLng) {
        LogUtils.w(Attribute.TAG, String.format("MainActivity当前的位置为： %s", latLng.toString()));
    }







}
