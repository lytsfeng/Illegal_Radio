package com.ldkj.illegal_radio.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldkj.illegal_radio.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpecFragment extends Fragment {


    public SpecFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View _view = inflater.inflate(R.layout.fragment_spec, container, false);


        return _view;
    }


}
