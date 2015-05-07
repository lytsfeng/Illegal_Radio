package com.ldkj.illegal_radio.utils.Devices;

import com.ldkj.illegal_radio.models.DeviceConfig;
import com.ldkj.illegal_radio.utils.Devices.base.IDevice;

/**
 * Created by john on 15-5-6.
 */
public class DeviceFactory {
    public static IDevice getDevice(){
        DeviceConfig config = DeviceConfig.getDeviceConfig();
        if("LD100".equalsIgnoreCase(config.type)){
            return new LD100(config.address,config.tcpPort);
        }
        return null;
    }
}
