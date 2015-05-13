package com.ldkj.illegal_radio.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.views.chart.Lines;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpecFragment extends Fragment {

    private Lines lines;
    public SpecFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View _view = inflater.inflate(R.layout.fragment_spec, container, false);
        lines = (Lines) _view.findViewById(R.id.specfragment);
        return _view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    public void update(float[] date) {
        if(lines != null)
            lines.bindData(date);
    }
}
