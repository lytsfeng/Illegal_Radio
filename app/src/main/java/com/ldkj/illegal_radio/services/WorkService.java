package com.ldkj.illegal_radio.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.ldkj.illegal_radio.activitys.MainActivity;
import com.ldkj.illegal_radio.events.NetEvent;
import com.ldkj.illegal_radio.events.SetParamEvent;
import com.ldkj.illegal_radio.models.DeviceConfig;
import com.ldkj.illegal_radio.models.IllegalRadioModel;
import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.DataConversion;
import com.ldkj.illegal_radio.utils.Net.UDPServer;
import com.ldkj.illegal_radio.utils.Utils;
import com.ldkj.illegal_radio.utils.audios.MyAudioTrack;
import com.ldkj.illegal_radio.utils.audios.WriteWaveFileThread;
import com.ldkj.illegal_radio.utils.devices.DeviceFactory;
import com.ldkj.illegal_radio.utils.devices.base.IDevice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.greenrobot.event.EventBus;

/**
 * Created by john on 15-4-24.
 */
public class WorkService extends Service implements UDPServer.UDPCallBack{


    private MainActivity activity;
    private IDevice device;
    private boolean isConn = false;
    private boolean isStart = false;
    private UDPServer udpServer;
    private MyAudioTrack audioTrack;
    private boolean isWorking = false;
    private FileOutputStream bos = null;
    private boolean isReco = false;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(Attribute.TAG, "workserviceOnBind");
        initPlay();
        EventBus.getDefault().register(this);

        return new LocalBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(Attribute.TAG, "workserviceOnUnBind");
        device.close();
        EventBus.getDefault().unregister(this);
        stopWork();
        return super.onUnbind(intent);
    }

    public void bindActivity(MainActivity activity) {
        this.activity = activity;
        startWork();
    }

    public void startWork() {
        Log.i(Attribute.TAG, "startwork");
        isConn = false;
        device = DeviceFactory.getDevice();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!isConn) {
                        EventBus.getDefault().post(new NetEvent(false));
                        isConn = device.connDevice();
                        isWorking = true;
                        Thread.sleep(1000);
                        Log.i(Attribute.TAG, "联机");
                    }
                    if(isWorking){
                        Log.i(Attribute.TAG, "开始工作");
                        startUDPServer();
                        EventBus.getDefault().post(new NetEvent(true));
                        sendCMD(Attribute.DELETEUDP);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void stopWork(){
        Log.i(Attribute.TAG, "//////////////////////////////" );
        isConn = true;
        isWorking = false;
        if(device != null){
            device.close();
        }
        if(udpServer!=null){
            udpServer.close();
            udpServer = null;
        }
        isStart = false;
    }
    public boolean sendCMD(String pCMD){
        if(device != null){
           return  device.sendCMD(pCMD);
        }
        return false;
    }

    public float[] readCMD(Attribute.DATATYPE datatype){

        float[] _date = null;
        if(device != null){
            switch (datatype){
                case IQDATA:
                    _date = device.getIQData();
                    break;
                case SCANDATA:
                    _date = device.getScanData();
                    break;
                case SPECDATE:
                    _date = device.getSpecData();
                    break;
                default:
                    _date = null;
                    break;
            }
        }
        return _date;
    }
    public boolean startTask(Attribute.TASKTYPE tasktype){
        boolean _success = false;
        if(device != null){
            switch (tasktype){
                case SINGLE:
                    _success = device.startSingle();
                    break;
                case SCAN:
                    _success = device.startScan();
                    break;
                default:
                    break;
            }
        }
        return _success;
    }

    private void startUDPServer() {
        if (udpServer == null){
            try {
                udpServer = new UDPServer(DeviceConfig.getDeviceConfig().udpPort, WorkService.this);
                new Thread(udpServer).start();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onEventMainThread(SetParamEvent event) {
        if (device != null) {
            device.sendCMD(event.getParam());
        }
    }

    @Override
    public void udpDate(byte[] pDate) {
        if(isSound){
            soundDataAnalysis(pDate);
        }
    }

    public final class LocalBinder extends Binder {
        public WorkService getService() {
            return WorkService.this;
        }
    }

    private void initPlay() {
        if (audioTrack == null) {
            audioTrack = new MyAudioTrack();
            audioTrack.init();
        }
    }

    //播放声音
    private void playSound(ByteBuffer ioBuffer) throws UnsupportedEncodingException {
        short tag = ioBuffer.getShort();
        if (tag != 401) {
            return;
        }
        int _len = ioBuffer.getInt();
        String _ChannelStr = DataConversion.Byte2Hex(ioBuffer.get());
        String freqStr = ioBuffer.getLong() + "";
        int bw = ioBuffer.getInt();
        byte[] c = new byte[4];
        ioBuffer.get(c);
        int count = _len - 23;
        if (count < 0) {
            return;
        }
        byte[] radio = new byte[count];
        ioBuffer.get(radio);

        if(isReco){
            synchronized (bos){
                if (bos != null){
                    try {
                        bos.write(radio);
                        bos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        audioTrack.playAudioTrack(radio, 0, count);
    }
    private boolean isSound =  true;


    protected void soundDataAnalysis(Object obj) {
        byte[] _b = (byte[]) obj;
        ByteBuffer _BB = ByteBuffer.wrap(_b);
        _BB.order(ByteOrder.LITTLE_ENDIAN);
        _BB.position(20);
        try {
            playSound(_BB);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private String soundFile = "";
    public void startReco( IllegalRadioModel model) {
        soundFile = Utils.getSoundFileName(model, false);
        if (!TextUtils.isEmpty(soundFile)) {
            File _file = new File(soundFile);
            if (_file.exists()) {
                _file.delete();
            }
            try {
                bos = new FileOutputStream(_file);
                isReco = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    public void stopReco(){
        isReco = false;
        synchronized (bos){
            try {
                bos.flush();
                bos.close();
                bos = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       new Thread( new WriteWaveFileThread(soundFile)).start();
    }
    public void setSound(boolean isSound) {
        this.isSound = isSound;
    }

}
