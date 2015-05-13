package com.ldkj.illegal_radio.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.ldkj.illegal_radio.activitys.MainActivity;
import com.ldkj.illegal_radio.events.NetEvent;
import com.ldkj.illegal_radio.events.SetParamEvent;
import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.devices.DeviceFactory;
import com.ldkj.illegal_radio.utils.devices.base.IDevice;

import de.greenrobot.event.EventBus;

/**
 * Created by john on 15-4-24.
 */
public class WorkService extends Service {


    private MainActivity activity;
    private IDevice device;
    private boolean isConn = false;
    private boolean isStart = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            EventBus.getDefault().post(new SpecEvent((Float[]) msg.obj));
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(Attribute.TAG, "workserviceOnBind");
        EventBus.getDefault().register(this);
        device = DeviceFactory.getDevice();
        return new LocalBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(Attribute.TAG, "workserviceOnUnBind");
        device.close();
        EventBus.getDefault().unregister(this);

        return super.onUnbind(intent);
    }

    public void bindActivity(MainActivity activity) {
        this.activity = activity;
        startWork();
    }

    private void startWork() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!isConn) {
                        EventBus.getDefault().post(new NetEvent(false));
                        isConn = device.connDevice();
                        Thread.sleep(1000);
                    }
                    EventBus.getDefault().post(new NetEvent(true));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void onEventMainThread(NetEvent netEvent) {
        if (netEvent.isConn()) {
            isStart = true;
            startScan();
//            startSingle();
        }
    }


    private void startScan(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                device.sendCMD(Attribute.STOPSCAN);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                device.sendCMD("FREQ:MODE DSC");
                device.sendCMD("TRAC:FEED:CONT MTRAC,ALW");
                device.sendCMD("TRAC:FEED:CONT IFPAN,NEV");
                device.sendCMD("SENS:FREQ:DSCan:STAR 88000000 Hz");
                device.sendCMD("SENS:FREQ:DSCan:STOP 108000000 Hz");
                device.sendCMD("SENS:FREQ:DSCan:STEP 25000Hz");
                device.sendCMD("TRACE:FEED:CONTROL ITRACE,NEVER");
                device.sendCMD("TRACE:FEED:CONTROL IFPAN,NEVER");
                device.sendCMD("TRACE:FEED:CONTROL MTRACE,ALWAYS");
                device.sendCMD("INPut:ATTenuation:MODE NORM");
                device.sendCMD("INPut:ATT 30");
                device.sendCMD(Attribute.STARTSCAN);
                while (isStart) {
                    float[] s = device.getScanDate();
                    if (s != null) {
                        if (activity != null) {
                            EventBus.getDefault().post(s);
                        }
                    }
                }
            }

        }).start();
    }



    private void startSingle() {
        device.sendCMD("FREQ:MODE CW");
        device.sendCMD("TRAC:FEED:CONT MTRAC,NEV");
        device.sendCMD("TRAC:FEED:CONT IFPAN,ALW");
        device.sendCMD("SENS:FREQ 91400000 Hz");
        device.sendCMD("FREQ:SPAN 20000000 Hz");
        device.sendCMD("INPut:ATT 30");
        device.sendCMD("SYSTEM:CHANNEL:BANDwidth 0, 125000 Hz");
        device.sendCMD("SYSTEM:CHAnnel:AUDio 0_");
        device.sendCMD("TRAC:FEED:CONT IF,ALW");
        device.sendCMD("SYSTEM:CHAnnel:IF 1_");
//        device.sendCMD("TRAC:UDP:TAG \"" + NetUtil.getLocalIpAddress() + "\",\""
//                + config.UDPPort + "\",AUDIO\n");
        String cmd = "SENSe:FUNCtion:ON  \"VOLT:AC\",\"FREQ:OFFS\",\"FSTR\",\"AM\",\"AM:POS\",\"AM:NEG\",\"FM\",\"FM:POS\",\"FM:NEG\",\"PM\",\"BAND\"";
        device.sendCMD(cmd);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isStart) {
                    float[] s = device.getSpecDate();
                    if (s != null) {
                        if (activity != null) {
                            EventBus.getDefault().post(s);
                        }
                    }
                }
            }
        }).start();

    }



    @Override
    public void onDestroy() {
        isStart = false;
        super.onDestroy();
    }

    public void onEventMainThread(SetParamEvent event) {
        if (device != null) {
            device.sendCMD(event.getParam());
        }
    }

    public final class LocalBinder extends Binder {
        public WorkService getService() {
            return WorkService.this;
        }
    }

}
