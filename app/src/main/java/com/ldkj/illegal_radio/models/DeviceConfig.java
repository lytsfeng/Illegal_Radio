package com.ldkj.illegal_radio.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.ldkj.illegal_radio.OwnApplication;

/**
 * Created by john on 15-4-23.
 */
public class DeviceConfig {
    private static final String DEVICECONFIG_FILE_NAME = "com_ldkj_illegal_raddio_deviceconfig";
    private static final String DEVICECONFIG_KEY_ADDRESS = "address";
    private static final String DEVICECONFIG_KEY_TCP_PORT = "tcp_port";
    private static final String DEVICECONFIG_KEY_UDP_PORT = "udp_port";
    private static final String DEVICECONFIG_KEY_DEVICE_TYPE = "type";


    private static DeviceConfig config = new DeviceConfig();

    public String address = "192.168.100.233";
    public int tcpPort = 65000;
    public int udpPort = 9999;
    public String type = "LD100";


    public static DeviceConfig getDeviceConfig() {
        SharedPreferences _SharedPreferences = OwnApplication.getContext().getSharedPreferences(DEVICECONFIG_FILE_NAME, Context.MODE_APPEND);
        config.address = _SharedPreferences.getString(DEVICECONFIG_KEY_ADDRESS, config.address);
        config.udpPort = _SharedPreferences.getInt(DEVICECONFIG_KEY_UDP_PORT, config.udpPort);
        config.tcpPort = _SharedPreferences.getInt(DEVICECONFIG_KEY_TCP_PORT, config.tcpPort);
        config.type = _SharedPreferences.getString(DEVICECONFIG_KEY_DEVICE_TYPE, config.type);
        return config;
    }

    public static void saveDeviceConfig(DeviceConfig deviceConfig) {
        SharedPreferences _SharedPreferences = OwnApplication.getContext().getSharedPreferences(DEVICECONFIG_FILE_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor _Editor = _SharedPreferences.edit();
        _Editor.putString(DEVICECONFIG_KEY_ADDRESS,deviceConfig.address);
        _Editor.putInt(DEVICECONFIG_KEY_TCP_PORT, deviceConfig.tcpPort);
        _Editor.putInt(DEVICECONFIG_KEY_UDP_PORT, deviceConfig.udpPort);
        _Editor.putString(DEVICECONFIG_KEY_DEVICE_TYPE,deviceConfig.type);
        _Editor.commit();
    }




}
