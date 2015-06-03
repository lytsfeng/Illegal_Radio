package com.ldkj.illegal_radio.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.fragments.DBFragmes.DBIllegalFragment;
import com.ldkj.illegal_radio.fragments.DBFragmes.DBRadioFragment;
import com.ldkj.illegal_radio.fragments.DBFragmes.DBSyncFragment;
import com.ldkj.illegal_radio.fragments.base.FragmentBase;
import com.ldkj.illegal_radio.utils.Attribute;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DBFragment extends FragmentBase implements RadioGroup.OnCheckedChangeListener,ViewPager.OnPageChangeListener{


    private Map<Integer,Fragment> fragments = new HashMap<Integer,Fragment>();

    private static final int TAB_INDEX_ONE = 0;
    private static final int TAB_INDEX_TWO = 1;
    private static final int TAB_INDEX_THREE = 2;

    private ViewPager viewPager;
    private RadioGroup radioGroup;
    private View view;

    public DBFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        stopSelfTask();
        view = inflater.inflate(R.layout.fragment_db, container, false);
        initView();
        addListener();
        binddate();
        return view;
    }



    private void initView(){
        viewPager = (ViewPager)view.findViewById(R.id.id_illegal_db_viewpager);
        radioGroup = (RadioGroup) view.findViewById(R.id.id_illegal_db_title);
    }

    private void  addListener(){
        radioGroup.setOnCheckedChangeListener(this);
        viewPager.setOnPageChangeListener(this);
    }
    private void binddate(){
        fragments.clear();
        fragments.put(R.id.id_illegal_db_illegal, new DBIllegalFragment());
        fragments.put(R.id.id_illegal_db_radio, new DBRadioFragment());
        fragments.put(R.id.id_illegal_db_sync, new DBSyncFragment());
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
        radioGroup.check(R.id.id_illegal_db_illegal);
    }


    @Override
    protected void stopSelfTask() {
        if(listener != null){
            listener.stopTask(Attribute.TASKTYPE.NO);
        }
    }

    @Override
    protected void startNewTask() {
        if(listener != null){
            listener.startTask(Attribute.TASKTYPE.NO);
        }
    }

    @Override
    public void updateData(float[] data) {

    }





    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.id_illegal_db_illegal:
                viewPager.setCurrentItem(TAB_INDEX_ONE);
                break;
            case R.id.id_illegal_db_radio:
                viewPager.setCurrentItem(TAB_INDEX_TWO);
                break;
            case R.id.id_illegal_db_sync:
                viewPager.setCurrentItem(TAB_INDEX_THREE);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case TAB_INDEX_ONE:
                radioGroup.check(R.id.id_illegal_db_illegal);
                break;
            case TAB_INDEX_TWO:
                radioGroup.check(R.id.id_illegal_db_radio);
                break;
            case TAB_INDEX_THREE:
                radioGroup.check(R.id.id_illegal_db_sync);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }



    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            int id = R.id.id_illegal_db_illegal;
            switch (position) {
                case TAB_INDEX_ONE:
                    id =  R.id.id_illegal_db_illegal;
                    break;
                case TAB_INDEX_TWO:
                    id = R.id.id_illegal_db_radio;
                    break;
                case TAB_INDEX_THREE:
                    id = R.id.id_illegal_db_sync;
                    break;
            }
            return fragments.get(id);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }


}
