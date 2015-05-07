package com.ldkj.illegal_radio.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ldkj.illegal_radio.activitys.MainActivity;
import com.ldkj.illegal_radio.events.NetEvent;
import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.Devices.DeviceFactory;
import com.ldkj.illegal_radio.utils.Devices.base.IDevice;

import de.greenrobot.event.EventBus;

/**
 * Created by john on 15-4-24.
 */
public class WorkService extends Service {


    private MainActivity activity;
    private IDevice device;
    private boolean isConn = false;

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

    public void onEventBackgroundThread(NetEvent netEvent){
        if(netEvent.isConn()){
            device.sendCMD(Attribute.STOPSCAN);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



            /*
                 SendCmd("TRAC:FEED:CONT MTRAC,ALW");
                    SendCmd("TRAC:FEED:CONT IFPAN,NEV");



                        m_LDBase.SendCmd("TRACE:FEED:CONTROL ITRACE,NEVER\n");
            m_LDBase.SendCmd("TRACE:FEED:CONTROL IFPAN,NEVER\n");
            m_LDBase.SendCmd("TRACE:FEED:CONTROL MTRACE,ALWAYS\n");
            */
            device.sendCMD("TRAC:FEED:CONT MTRAC,ALW");
            device.sendCMD("TRAC:FEED:CONT IFPAN,NEV");

            device.sendCMD("SENS:FREQ:STAR 88000000 Hz");
            device.sendCMD("SENS:FREQ:STOP 108000000 Hz");
            device.sendCMD("SENS:FREQ:STEP 25000Hz");

            device.sendCMD("TRACE:FEED:CONTROL ITRACE,NEVER");
            device.sendCMD("TRACE:FEED:CONTROL IFPAN,NEVER");
            device.sendCMD("TRACE:FEED:CONTROL MTRACE,ALWAYS");
            device.sendCMD("INPut:ATTenuation:MODE NORM");
            device.sendCMD("INPut:ATT 0");
            device.sendCMD(Attribute.STARTSCAN);

            while (true){
               short[]  s =  device.getScanDate();


                int i = 0;
                i++;
            }


        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public final class LocalBinder extends Binder {
        public WorkService getService() {
            return WorkService.this;
        }
    }


}
