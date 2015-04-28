package com.ldkj.illegal_radio.activitys;

import android.app.Fragment;
import android.os.Bundle;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.activitys.base.ActivityFrame;
import com.ldkj.illegal_radio.fragments.MapFragment;
import com.ldkj.illegal_radio.fragments.OnFragmentInteractionListener;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActivityFrame implements OnFragmentInteractionListener{

    private static Map<Integer,Fragment> fragmentMap = new HashMap<Integer,Fragment>(){
        {
            put(1,new MapFragment());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void onResume() {
        showFreagment(R.id.main_fragment,fragmentMap.get(1));
        super.onResume();
    }
    @Override
    protected void onPause() {
        removeFreagment(R.id.main_fragment);
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
