package com.ldkj.illegal_radio.utils.devices.base;

/**
 * Created by john on 15-5-6.
 */
public interface IDevice {

    String getDeviceName();
    boolean connDevice();
    void close();
    boolean sendCMD(String pCMD) ;


    boolean startScan();
    boolean startSingle();

    float[] getIQData();
    float[] getScanData();
    float[] getSpecData();

}
