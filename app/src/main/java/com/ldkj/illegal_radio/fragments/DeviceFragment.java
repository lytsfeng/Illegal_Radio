package com.ldkj.illegal_radio.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.fragments.base.FragmentBase;
import com.ldkj.illegal_radio.models.DeviceConfig;

public class DeviceFragment extends FragmentBase {


    private DeviceConfig config;
    private EditText etAddress;
    private EditText etTcpPort;
    private EditText etUdpPort;
    private TextView tvType;
    private Button iBtnOk;
    private Button iBtnCannel;

    public DeviceFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        stopSelfTask();
        config = DeviceConfig.getDeviceConfig();
        View _View = inflater.inflate(R.layout.fragment_device, container, false);
        initView(_View);
        bindData();
        addListener();
        return _View;
    }

    @Override
    public void onDestroyView() {
        startNewTask();
        super.onDestroyView();
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

    private void initView(View v) {
        etAddress = (EditText) v.findViewById(R.id.et_device_address);
        etTcpPort = (EditText) v.findViewById(R.id.et_device_tcp_port);
        etUdpPort = (EditText) v.findViewById(R.id.et_device_udp_port);
        tvType = (TextView) v.findViewById(R.id.tv_device_type);
        iBtnOk = (Button)v.findViewById(R.id.ibtn_device_ok);
        iBtnCannel =(Button)v.findViewById(R.id.ibtn_device_cannel);
    }
    private void bindData(){
        etAddress.setText(config.address);
        etTcpPort.setText(config.tcpPort+"");
        etUdpPort.setText(config.udpPort + "");
        tvType.setText(config.type);
    }
    private void addListener(){
        iBtnCannel.setOnClickListener(onClickListener);
        iBtnOk.setOnClickListener(onClickListener);
        tvType.setOnClickListener(onClickListener);
    }


    private void saveDeviceConfig(){
        config.address = etAddress.getText().toString();
        config.udpPort = Integer.parseInt(etUdpPort.getText().toString().trim());
        config.tcpPort = Integer.parseInt(etTcpPort.getText().toString().trim());
        config.type = tvType.getText().toString();
        DeviceConfig.saveDeviceConfig(config);
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vID = v.getId();
            switch (vID){
                case R.id.tv_device_type:
                    break;
                case R.id.ibtn_device_cannel:
//                    getActivity().finish();
                    break;
                case R.id.ibtn_device_ok:
                    saveDeviceConfig();
                    break;
                default:
                    break;
            }
        }
    };

}
