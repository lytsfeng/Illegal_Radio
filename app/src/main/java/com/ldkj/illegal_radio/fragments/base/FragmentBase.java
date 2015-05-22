package com.ldkj.illegal_radio.fragments.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldkj.illegal_radio.models.IllegalRadioModel;
import com.ldkj.illegal_radio.utils.Attribute;

/**
 * Created by john on 15-5-18.
 */
public abstract class FragmentBase extends Fragment {
    protected OnFragmentInteractionListener listener = null;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (OnFragmentInteractionListener) activity;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        stopSelfTask();
        startNewTask();
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listener = null;
    }

    protected void setCommand(String pCMD){
        if(listener != null){
            listener.setCommand(pCMD);
        }
    }

    public void updateIllegal(IllegalRadioModel pModel, boolean isAdd){

    }
    protected Attribute.DATATYPE  datatype = Attribute.DATATYPE.SPECDATE;
    public void updateData(float[] date,Attribute.DATATYPE datatype){
        this.datatype = datatype;
        updateData(date);
    }

    protected abstract void stopSelfTask();
    protected abstract void startNewTask();
    public abstract  void updateData(float[] data);


}
