package com.ldkj.illegal_radio.fragments;


import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.activitys.MainActivity;
import com.ldkj.illegal_radio.fragments.base.FragmentBase;
import com.ldkj.illegal_radio.models.IllegalRadioModel;
import com.ldkj.illegal_radio.models.Single;
import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.Utils;
import com.ldkj.illegal_radio.views.dialogs.NumberDialog;
import com.ldkj.illegal_radio.views.dialogs.SelectListDialog;
import com.ldkj.illegal_radio.views.dialogs.base.DialogBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleFragment2 extends FragmentBase implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, DialogBase.DialogCallBack<String> {


    private static final String ARG = "arg";
    private View view = null;
    private RadioGroup rgSound;
    private RadioGroup rgWorktype;
    private LinearLayout llFloorControl;
    private TextView tvLevel;
    private TextView tvLevelMax;
    private TextView tvLevelMin;
    private TextView tvCenterFreq;
    private TextView tvATT;
    private TextView tvAVGLevel;

    private Button btnFloorAdd;
    private Button btnFloorSub;
    private Button btnTaskStatus;
    private TextView tvCurrentFloor;

//    private Boolean isDraw = true;
    private Single single;
    private ArrayList<IllegalRadioModel> singleRadioModels = new ArrayList<>();
    private Boolean isFloor = false;
    private Boolean isRuning = false;   //  true   为正在运行single   false 为  没有运行single
    private int currentFloor = 1;
    private int oldFloor = 1;
    private Boolean isChangeFloor = false;
    private Map<Integer, Level> levelMap = new HashMap<>();
    private Level currentLevel = null;
    private Integer avgCount;

    private Typeface typeface;


    public SingleFragment2() {


    }

    public static FragmentBase newInstance(int arg) {
        SingleFragment2 fragment = new SingleFragment2();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG, arg);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DS-DIGIT.TTF");
        stopSelfTask();
        view = inflater.inflate(R.layout.fragment_single_fragment2, container, false);
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
        rgSound = (RadioGroup) view.findViewById(R.id.id_illegal_single_sound);
        rgWorktype = (RadioGroup) view.findViewById(R.id.id_illegal_single_work);
        llFloorControl = (LinearLayout) view.findViewById(R.id.id_illegal_single_floor_control);
        tvCenterFreq = (TextView) view.findViewById(R.id.id_illegal_single_center_freq);
        tvATT = (TextView) view.findViewById(R.id.id_illegal_single_att);
        tvAVGLevel = (TextView) view.findViewById(R.id.id_illegal_single_level_avg);
        tvLevel = (TextView) view.findViewById(R.id.id_illegal_single_lever);
        tvLevel.setTypeface(typeface);
        tvLevelMax = (TextView) view.findViewById(R.id.id_illegal_single_level_max);
        tvLevelMin = (TextView) view.findViewById(R.id.id_illegal_single_level_min);
        tvCurrentFloor = (TextView) view.findViewById(R.id.id_illegal_single_floor_value);
        btnFloorAdd = (Button) view.findViewById(R.id.id_illegal_single_floor_add);
        btnFloorSub = (Button) view.findViewById(R.id.id_illegal_single_floor_sub);
        btnTaskStatus = (Button) view.findViewById(R.id.id_illegal_single_bottom);

    }

    private void addListener() {
        rgWorktype.setOnCheckedChangeListener(this);
        rgSound.setOnCheckedChangeListener(this);
        tvCenterFreq.setOnClickListener(this);
        tvATT.setOnClickListener(this);
        tvAVGLevel.setOnClickListener(this);

        btnFloorSub.setOnClickListener(this);
        btnFloorAdd.setOnClickListener(this);
        btnTaskStatus.setOnClickListener(this);
    }

    private void bindDate() {
        rgSound.check(R.id.id_illegal_single_sound_device);
        rgWorktype.check(R.id.id_illegal_single_work_usually);
        tvCurrentFloor.setText(currentFloor+"");
        single = Single.getSingle();
        singleRadioModels.addAll(((MainActivity) getActivity()).getMainIllegalRadioModelSet());
        String _tmpFreq = single.centFreq;
        if (singleRadioModels.size() > 0) {
            Bundle _Bundle = getArguments();
            int index = 0;
            if (_Bundle != null) {
                index = getArguments().getInt(ARG);
                _tmpFreq = singleRadioModels.get(index).freq;
                startNewTask();
                isRuning = true;
            }else {
                _tmpFreq = singleRadioModels.get(0).freq;
            }

        }
        setTaskStatus(isRuning);
        DialogBack(_tmpFreq, R.id.id_illegal_single_center_freq, true);
        DialogBack(single.freqBandWidth, R.id.id_single_freq_bandwidth, true);
        DialogBack(single.filterBandwidth, R.id.id_single_filter_bandwidth, true);
        DialogBack(single.attcontrol, R.id.id_illegal_single_att, true);
        DialogBack(single.demodulationMode, R.id.id_single_demodulation_mode, true);
        DialogBack(single.levelAvg,R.id.id_illegal_single_level_avg,true);
    }

    public void onEventMainThread(String pValue) {
//        if (isDraw) {
            tvLevel.setText(pValue);
            SaveLevel(Integer.parseInt(pValue.trim()));
//        }
    }

    @Override
    protected void stopSelfTask() {
        if (listener != null) {
            Attribute.TASKTYPE  t =  listener.getTaskType();
            if(t != Attribute.TASKTYPE.SINGLE)
                listener.stopTask(Attribute.TASKTYPE.SCAN);
            else {
                isRuning = true;
            }
        }
    }

    @Override
    protected void startNewTask() {
        if (listener != null) {
            listener.startTask(Attribute.TASKTYPE.SINGLE);
        }
    }

    @Override
    public void updateData(float[] data) {
//        double _leve = Utils.calculateLevelfromIQ(data);
//        EventBus.getDefault().post(getLevelAvg(_leve));
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        int _id = group.getId();
        int i = group.getCheckedRadioButtonId();
        switch (_id) {
            case R.id.id_illegal_single_sound:
                switch (checkedId){
                    case R.id.id_illegal_single_sound_device:
                        EventBus.getDefault().post(Attribute.SOUNDTYPE.DEVICE);
                        break;
                    case R.id.id_illegal_single_sound_mute:
                        EventBus.getDefault().post(Attribute.SOUNDTYPE.MUTE);
                        break;
                    case R.id.id_illegal_single_sound_voice:
                        EventBus.getDefault().post(Attribute.SOUNDTYPE.VOICE);
                        break;
                    default:
                        break;
                }
                break;
            case R.id.id_illegal_single_work:
                if (checkedId == R.id.id_illegal_single_work_floor) {
                    llFloorControl.setVisibility(View.VISIBLE);
                    tvCurrentFloor.setText(currentFloor+"");
                    isFloor = true;
                } else if (checkedId == R.id.id_illegal_single_work_usually) {
                    llFloorControl.setVisibility(View.GONE);
                    isFloor = false;
                    setFloor(1);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
//        isDraw = false;
        int _id = v.getId();
        switch (_id) {
            case R.id.id_illegal_single_center_freq:
                new NumberDialog(getActivity(), _id, this).show();
                break;
            case R.id.id_illegal_single_att:
                new SelectListDialog(getActivity(), _id, R.array.array_attenuation_control, this).show();
                break;
            case R.id.id_illegal_single_level_avg:
                new SelectListDialog(getActivity(), _id, R.array.array_levelavg, this).show();
                break;

            case R.id.id_illegal_single_floor_add:
                if(!isRuning ){
                    if(currentFloor >= levelMap.size()){
                        break;
                    }
                }
                setFloor(currentFloor+1);
                currentLevel = null;
                tvCurrentFloor.setText(currentFloor+"");
                break;
            case R.id.id_illegal_single_floor_sub:
                if(currentFloor > 1){
                    currentLevel = null;
                    setFloor(currentFloor-1);;
                    tvCurrentFloor.setText(currentFloor+"");
                }
                break;
            case R.id.id_illegal_single_bottom:
                setTaskStatus(!isRuning);
                break;
            default:
                break;
        }


    }

    private void sendCmd(String pVlaue) {
        if (listener != null) {
            listener.setCommand(pVlaue);
        }
    }

    @Override
    public void DialogBack(String s, int p_ResId, Boolean isInit) {
        TextView _tv = (TextView) view.findViewById(p_ResId);
        if (_tv != null)
            _tv.setText(s);

        switch (p_ResId) {
            case R.id.id_illegal_single_center_freq:
                sendCmd("SENS:FREQ " + Utils.valueToHz(s) + " Hz");
                single.centFreq = s;
                break;
            case R.id.id_single_freq_bandwidth:
                sendCmd("FREQ:SPAN " + Utils.valueToHz(s) + " Hz");
                single.freqBandWidth = s;
                break;
            case R.id.id_illegal_single_att:
                sendCmd("INPut:ATT " + s.substring(0, s.length() - 2));
                sendCmd("SENS:FREQ " + Utils.valueToHz(single.centFreq) + " Hz");
                single.attcontrol = s;
                break;
            case R.id.id_single_filter_bandwidth:
                sendCmd("SYSTEM:CHANNEL:BANDwidth 0, "
                        + Utils.valueToHz(s) + " Hz");
                single.filterBandwidth = s;
                break;
            case R.id.id_single_demodulation_mode:
                setDemodulation(s);
                single.demodulationMode = s;
                break;
            case R.id.id_illegal_single_level_avg:
                setLevelCount(s);
                single.levelAvg = s;
                break;
        }
        if (!isInit)
            Single.setSingle(single);
    }

    private void setLevelCount(String p_Number) {
        if (!Utils.isNumeric(p_Number.trim())) {
            avgCount = 1;
        } else {
            avgCount = Integer.parseInt(p_Number.trim());
        }
        EventBus.getDefault().post(avgCount);
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

    public void SaveLevel(Integer pValue) {

        Boolean _isChange = false;
        if (currentLevel == null) {
            currentLevel = levelMap.get(currentFloor);
            if (currentLevel == null) {
                currentLevel = new Level(pValue);
                _isChange = true;
            }
        }
        if(isChangeFloor){
            currentLevel.setLevelMax(currentLevel.levelMax);
            currentLevel.setLevelMin(currentLevel.levelMin);
            isChangeFloor = false;
        }
        if (currentLevel.levelMax < pValue) {
            currentLevel.setLevelMax(pValue);
            _isChange = true;
        } else if (currentLevel.levelMin > pValue) {
            currentLevel.setLevelMin(pValue);
            _isChange = true;
        }
        if (_isChange)
            levelMap.put(currentFloor, currentLevel);
    }

    public void setTaskStatus(Boolean taskStatus) {
        this.isRuning = taskStatus;
        if(isRuning){
            btnTaskStatus.setText(R.string.task_single_off);
            startNewTask();
            DialogBack(single.centFreq, R.id.id_illegal_single_center_freq, true);
            DialogBack(single.freqBandWidth, R.id.id_single_freq_bandwidth, true);
            DialogBack(single.filterBandwidth, R.id.id_single_filter_bandwidth, true);
            DialogBack(single.attcontrol, R.id.id_illegal_single_att, true);
            DialogBack(single.demodulationMode, R.id.id_single_demodulation_mode, true);
            DialogBack(single.levelAvg,R.id.id_illegal_single_level_avg,true);
        }else {
            btnTaskStatus.setText(R.string.task_single_on);
            if(listener != null){
                listener.stopTask(Attribute.TASKTYPE.SINGLE);
                listener.setCommand(Attribute.DELETEUDP);
            }
        }
    }


    private void setFloor(int floor){
        isChangeFloor = floor != oldFloor;
        if(isChangeFloor){
            currentFloor = oldFloor = floor;
        }
        if(!isRuning){
            int _floorCount = levelMap.size();
            if(floor <= _floorCount){
                currentLevel = levelMap.get(floor);
                currentLevel.setLevelMax(currentLevel.levelMax);
                currentLevel.setLevelMin(currentLevel.levelMin);
            }
        }

    }

    class Level {
        private int levelMax;
        private int levelMin;

        public Level(int level) {
            setLevelMax(level);
            setLevelMin(level);
        }

        public Level() {
        }

        public void setLevelMin(int levelMin) {
            this.levelMin = levelMin;
            tvLevelMin.setText(levelMin + "");
        }

        public void setLevelMax(int levelMax) {
            this.levelMax = levelMax;
            tvLevelMax.setText(levelMax + "");
        }

        public int Compare(Level level) {
            if (level == null) {
                return 1;
            }
            if (level.levelMax == this.levelMax) {
                return 0;
            }
            if (level.levelMax > this.levelMax) {
                return -1;
            }
            return 1;
        }
    }


}
