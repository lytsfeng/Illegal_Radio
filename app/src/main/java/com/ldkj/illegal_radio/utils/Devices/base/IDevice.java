package com.ldkj.illegal_radio.utils.Devices.base;

/**
 * Created by john on 15-5-6.
 */
public interface IDevice {

    String getDeviceName();
    boolean connDevice();
    void close();
    boolean sendCMD(String pCMD) ;

    double getLevel();
    short[] getScanDate();
    short[] getSpecDate();

}
