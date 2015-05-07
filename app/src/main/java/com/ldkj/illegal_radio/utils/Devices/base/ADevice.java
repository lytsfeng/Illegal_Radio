package com.ldkj.illegal_radio.utils.Devices.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by john on 15-5-6.
 */
public abstract class ADevice implements IDevice {


    private Socket socket;
    protected InputStream inputStream;
    protected OutputStream outputStream;
    private String address = "192.168.100.232";
    private int port = 65000;
    private boolean isconn = false;


    public ADevice(String address,int port){
        this.address = address;
        this.port = port;
    }

    @Override
    public boolean connDevice() {
        close();
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(address,port),5000);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            isconn = true;
        } catch (IOException e) {
            socket = null;
        }
        return isconn;
    }
    @Override
    public void close() {
        isconn = false;
        try{
            if(inputStream != null){
                inputStream.close();
                inputStream = null;
            }
            if(outputStream != null){
                outputStream.close();
                outputStream = null;
            }
            if(socket != null){
                socket.close();
                socket = null;
            }
        }catch (IOException e){

        }
    }
    @Override
    public boolean sendCMD(String pCMD) {
        boolean _isSend = false;
        if(!isconn){
            return _isSend;
        }
        if(pCMD.indexOf("\n") == -1){
            pCMD += "\n";
        }
        try {
            byte[] _messageBuffer = pCMD.getBytes("ASCII");
            outputStream.write(_messageBuffer);
            outputStream.flush();
            _isSend = true;
        } catch (IOException e) {
            e.printStackTrace();
            resetConn();
        }
        return _isSend;
    }


    protected int readData(byte[] _buf, int _PerIndex, int _DataLength) throws IOException {
        int _Rec = 0;
        if (inputStream == null || !isconn) {
            return _Rec;
        }
        _Rec = inputStream.read(_buf, _PerIndex, _DataLength);
        return _Rec;
    }
    protected byte[] readTcpData(int datalength) throws IOException {
        byte[] _Data = new byte[datalength];
        int _index = 0;
        int readLength = 0;

        while (readLength != datalength){
            readLength += readData(_Data,_index+readLength,datalength-readLength);
        }
        return _Data;
    }
    private void resetConn(){
        close();
        connDevice();
    }
}
