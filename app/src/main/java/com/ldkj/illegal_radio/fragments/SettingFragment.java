package com.ldkj.illegal_radio.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.fragments.base.FragmentBase;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends FragmentBase {


    private FloatingActionButton dbBtn;
    private FloatingActionButton deviceBtn;
    private FloatingActionsMenu floatingActionsMenu;
    public SettingFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        stopSelfTask();
        View _view = inflater.inflate(R.layout.fragment_setting, container, false);
        initView(_view);
        addListener();
        return _view;
    }

    @Override
    public void onDestroyView() {
        startNewTask();
        super.onDestroyView();
    }

    private void initView(View view) {
        dbBtn = (FloatingActionButton) view.findViewById(R.id.id_setting_action_db);
        deviceBtn = (FloatingActionButton)view.findViewById(R.id.id_setting_action_device);
        floatingActionsMenu = (FloatingActionsMenu) view.findViewById(R.id.id_setting_multiple_actions);
        showFragment(R.id.id_setting_action_device);
    }

    private void addListener(){
        dbBtn.setOnClickListener(clickListener);
        deviceBtn.setOnClickListener(clickListener);
    }
    private int currID = -1;
    private void showFragment(int id){

        if(id == currID){
            return;
        }
        currID = id;
        FragmentBase fragmentBase = null;
        switch (id){
            case R.id.id_setting_action_db:
                fragmentBase = new DBFragment();
                break;
            case  R.id.id_setting_action_device:
                fragmentBase = new DeviceFragment();
                break;
        }
        if(fragmentBase != null){
            FragmentManager _Manager = getFragmentManager();
            FragmentTransaction _Transaction = _Manager.beginTransaction();
            _Transaction.replace(R.id.id_setting_fragment,fragmentBase);
            _Transaction.commit();
        }
    }
    @Override
    protected void stopSelfTask() {
        if(listener != null){
            listener.setWorkStatus(false);
        }
    }
    @Override
    protected void startNewTask() {
        if(listener != null){
            listener.setWorkStatus(true);
        }
    }
    @Override
    public void updateData(float[] data) {

    }


    private View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v instanceof FloatingActionButton){
                showFragment(v.getId());
            }
            floatingActionsMenu.toggle();
        }
    };

}
