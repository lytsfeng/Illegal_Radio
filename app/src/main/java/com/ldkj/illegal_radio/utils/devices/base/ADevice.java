package com.ldkj.illegal_radio.utils.devices.base;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by john on 15-5-6.
 */
public abstract class ADevice implements IDevice {


    protected BufferedInputStream inputStream;
    protected OutputStream outputStream;
    protected boolean isconn = false;
    private Socket socket;
    private String address = "192.168.100.232";
    private int port = 65000;


    public ADevice(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public boolean connDevice() {
        close();
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(address, port), 5000);
            socket.setSoTimeout(1000);
            inputStream = new BufferedInputStream(socket.getInputStream());
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
        try {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {

        }
    }

    @Override
    public boolean sendCMD(String pCMD) {
        boolean _isSend = false;
        if (!isconn) {
//            resetConn();
            return _isSend;
        }
        if (pCMD.indexOf("\n") == -1) {
            pCMD += "\n";
        }
        try {
            byte[] _messageBuffer = pCMD.getBytes("ASCII");
            if (outputStream != null) {
                outputStream.write(_messageBuffer);
                outputStream.flush();
                _isSend = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            resetConn();
        }
        return _isSend;
    }

    protected void resetInput() {
        if (inputStream != null) {
            try {
                int _len = inputStream.available();
                byte[] _buf = null;
                if (_len > 0) {
                    _buf = readTcpData(_len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected int readData(byte[] _buf, int _PerIndex, int _DataLength) {
        int _Rec = 0;
        if(inputStream != null){
            if (inputStream == null || !isconn) {
                return _Rec;
            }
            try {
                _Rec = inputStream.read(_buf, _PerIndex, _DataLength);
            } catch (SocketTimeoutException e) {
                _Rec = 0;
            } catch (IOException e) {
                _Rec = 0;
            }
        }
        return _Rec;
    }

    protected byte[] readTcpData(int datalength) {
        byte[] _Data = new byte[datalength];
        int _index = 0;
        int readLength = 0;
        while (readLength != datalength) {
            int _recLen = datalength - readLength;
            int _rec = readData(_Data, _index + readLength, _recLen);
            if (_rec == 0) {
                readLength = datalength;
                _Data = null;
            } else {
                readLength += _rec;
            }
        }
        return _Data;
    }

    private void resetConn() {
        close();
        connDevice();
    }
}
