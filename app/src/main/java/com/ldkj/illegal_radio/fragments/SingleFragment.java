package com.ldkj.illegal_radio.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.activitys.MainActivity;
import com.ldkj.illegal_radio.fragments.base.FragmentBase;
import com.ldkj.illegal_radio.models.IllegalRadioModel;
import com.ldkj.illegal_radio.models.Single;
import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.Utils;
import com.ldkj.illegal_radio.views.dialogs.SelectGridDialog;
import com.ldkj.illegal_radio.views.dialogs.SelectListDialog;
import com.ldkj.illegal_radio.views.dialogs.base.DialogBase;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleFragment extends FragmentBase implements View.OnClickListener , DialogBase.DialogCallBack<String>{

    private boolean isTaskRuning = false;

    private TextView tvLevel ;
    private Integer[] idArray = {R.id.id_single_center_freq,R.id.id_single_freq_bandwidth,
            R.id.id_single_filter_bandwidth,R.id.id_single_attenuation_control,
            R.id.id_single_demodulation_mode,R.id.id_single_level_avg,
            R.id.id_single_sound_param};
    private View view;

    private Single single;
    private boolean isDraw = true;
    private ArrayList<IllegalRadioModel> radioModels = new ArrayList<>();

    public SingleFragment() {
        // Required empty public constructor
    }

    private static final String ARG = "arg";
    public static FragmentBase newInstance(int arg){
        SingleFragment fragment = new SingleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG, arg);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreateView(inflater, container, savedInstanceState);
        view= inflater.inflate(R.layout.fragment_single, container, false);
        initView();
        addListener();
        bindDate();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        tvLevel = (TextView) view.findViewById(R.id.id_single_single_level);
    }
    private void bindDate(){
        single = Single.getSingle();
        radioModels.addAll(((MainActivity) getActivity()).getIllegalRadioModelSet());
        String _tmpFreq = single.centFreq;
        if(radioModels.size() > 0){
            Bundle _Bundle = getArguments();
            int index = 0;
            if(_Bundle != null){
                index =  getArguments().getInt(ARG);
            }
            _tmpFreq = radioModels.get(index).freq;
        }
        DialogBack(_tmpFreq,R.id.id_single_center_freq,true);
        DialogBack(single.freqBandWidth,R.id.id_single_freq_bandwidth,true);
        DialogBack(single.filterBandwidth,R.id.id_single_filter_bandwidth,true);
        DialogBack(single.attcontrol, R.id.id_single_attenuation_control, true);
        DialogBack(single.demodulationMode, R.id.id_single_demodulation_mode, true);
    }

    private void addListener(){
        int _arrayLength = idArray.length;
        for (int i = 0; i < _arrayLength; i++){
            view.findViewById(idArray[i]).setOnClickListener(this);
        }
    }


    public void onEventMainThread(String pValue) {
        if(isDraw)
            tvLevel.setText(pValue);
    }

    private Queue<Double> levels = new ArrayDeque<Double>();
    private int avgCount = 1;
    private double levelSum = 0;
    public String getLevelAvg(double plevel) {

        if (levelSum == 0) {
            levels.clear();
        }
        if (avgCount == 1) {
            return String.format("%.0f", plevel);
        }
        double _avg = 0;
        int _listSize = levels.size();
        if (_listSize >= avgCount) {
            double _tmp = levels.poll();
            levelSum -= _tmp;
            _listSize--;
        }
        levels.add(plevel);
        levelSum += plevel;
        _listSize += 1;
        return String.format("%.0f", levelSum / _listSize);
    }




    @Override
    protected void stopSelfTask() {

         if(listener != null){

             listener.stopTask(Attribute.TASKTYPE.SCAN);
         }
    }
    @Override
    protected void startNewTask() {
       if(listener != null){
           listener.startTask(Attribute.TASKTYPE.SINGLE);
       }
    }

    @Override
    public void updateData(float[] data) {
        double d = Utils.calculateLevelfromIQ(data);
        String _s = getLevelAvg(d);
        EventBus.getDefault().post(_s);
    }

    @Override
    public void DialogBack(String s, int p_ResId, Boolean isInit) {
        isDraw = true;
        TextView _tv = (TextView) view.findViewById(p_ResId);
        _tv.setText(s);

        switch (p_ResId) {
            case R.id.id_single_center_freq:
                sendCmd("SENS:FREQ " + Utils.valueToHz(s) + " Hz");
                single.centFreq = s;
                break;
            case R.id.id_single_freq_bandwidth:
                sendCmd("FREQ:SPAN " + Utils.valueToHz(s) +" Hz");
                single.freqBandWidth = s;
                break;
            case R.id.id_single_attenuation_control:
                sendCmd("INPut:ATT " + s.substring(0, s.length() - 2));
                single.attcontrol = s;
                break;
            case R.id.id_single_filter_bandwidth:
                sendCmd("SYSTEM:CHANNEL:BANDwidth 0, "
                        + Utils.valueToHz(s) +" Hz");
                single.filterBandwidth = s;
                break;
            case R.id.id_single_demodulation_mode:
                setDemodulation(s);
                single.demodulationMode = s;
                break;
//                case R.id.sound_map:
//                case R.id.sound_param:
//                    setSoundBtnImage(p_Number);
//                    isInit = true;
//                    break;
            case R.id.id_single_level_avg:
                    setLevelCount(s);
                break;
        }
        if(!isInit)
            Single.setSingle(single);
    }

    private void setLevelCount(String p_Number) {
        if (!Utils.isNumeric(p_Number.trim())) {
            avgCount = 1;
        } else {
            avgCount = Integer.parseInt(p_Number.trim());
        }
        levelSum = 0;
    }

    private void setDemodulation(String pValue) {
        if (pValue.equalsIgnoreCase(getResources().getStringArray(
                R.array.array_demodulation_mode)[0])) {
            sendCmd("SYSTEM:CHAnnel:AUDio 0_");
        } else {
            sendCmd("SENS:DEM " + pValue);
            sendCmd("SYSTEM:CHAnnel:AUDio 1_");
        }
    }

//    private void startSingle(String freq){
//        for (IllegalRadioModel model :radioModels){
//            if(model.freq.equalsIgnoreCase(freq.trim())){
//                DialogBack(single.freqBandWidth,R.id.id_single_freq_bandwidth,true);
//                DialogBack(single.filterBandwidth,R.id.id_single_filter_bandwidth,true);
//                DialogBack(single.attcontrol,R.id.id_single_attenuation_control,true);
//                DialogBack(single.demodulationMode,R.id.id_single_demodulation_mode,true);
//                break;
//            }
//        }
//    }


    @Override
    public void onClick(View v) {
        if(v instanceof  TextView){
            isDraw = false;
            int vid = v.getId();

            switch (vid) {
                case R.id.id_single_center_freq:
                    SelectListDialog dialog = new SelectListDialog(getActivity(), R.id.id_single_center_freq, getValue(), this);
                    dialog.show();
                    break;
                case R.id.id_single_freq_bandwidth:
                    ShowSelectGridDialog(R.id.id_single_freq_bandwidth,R.array.array_freq_bandwidth);
                    break;
                case R.id.id_single_attenuation_control:
                    ShowSelectDialog(R.id.id_single_attenuation_control,R.array.array_attenuation_control);
                    break;
                case R.id.id_single_filter_bandwidth:
                    ShowSelectGridDialog(R.id.id_single_filter_bandwidth,R.array.array_filter_bandwidth);
                    break;
                case R.id.id_single_demodulation_mode:
                    ShowSelectDialog(R.id.id_single_demodulation_mode, R.array.array_demodulation_mode);
                    break;
                case R.id.id_single_level_avg:
                    ShowSelectDialog(vid,R.array.array_levelavg);
                    break;
            }
        }
    }
    private String[] getValue() {

        int size = radioModels.size();
        String[] _array = new String[size];
        for (int i = 0; i < size; i++){
            _array[i] = radioModels.get(i).freq;
        }
        return _array;
    }

    private void sendCmd(String pVlaue){
        if(listener != null){
            listener.setCommand(pVlaue);
        }
    }
    protected void ShowSelectDialog(int pResId, int pResArrayId) {
        SelectListDialog _ItemDialog = new SelectListDialog(getActivity(),pResId,
                pResArrayId,this);
        _ItemDialog.show();
    }

    protected void ShowSelectGridDialog(int pResId, int pResArrayId) {
        SelectGridDialog _SelectGridDialog = new SelectGridDialog(getActivity(), pResId,
                pResArrayId,this);
        _SelectGridDialog.show();
    }

}
