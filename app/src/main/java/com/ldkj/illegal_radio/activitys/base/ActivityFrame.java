package com.ldkj.illegal_radio.activitys.base;

import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.fragments.base.FragmentBase;
import com.ldkj.illegal_radio.utils.Reflection;

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
    protected FragmentBase getFragment(int position) {
        String pakeName = getPackageName() + ".fragments.";
        String[] _className = getResources().getStringArray(R.array.FragmentClassName);
        Reflection _Reflection = new Reflection();
        try {
            return (FragmentBase) _Reflection.newInstance(pakeName + _className[position], null, null);
        } catch (Exception e) {
            return null;
        }
    }
}
