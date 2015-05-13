package com.ldkj.illegal_radio.utils.devices;

import com.ldkj.illegal_radio.models.Scan;
import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.CIOUtils;
import com.ldkj.illegal_radio.utils.Utils;
import com.ldkj.illegal_radio.utils.devices.base.ADevice;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by john on 15-5-6.
 */
public class LD100 extends ADevice {


    private Scan scanData = null;
    private float[] avgSpce;
    private Queue<float[]> spceQueue = new ArrayDeque<>();

    public LD100(String address, int port) {
        super(address, port);
        scanData = Scan.getScan();
        avgSpce = new float[scanData.date.length];
    }

    @Override
    public String getDeviceName() {
        return "LD100";
    }

    @Override
    public double getLevel() {
        float[] data = readCMD(Attribute.IQMDATA1);
        if (data == null) {
            return 0;
        }
        int _len = data.length / 2;
        double _TmpCount = 0;
        for (int i = 0; i < _len; i++) {
            _TmpCount += Math.pow(data[i], 2) + Math.pow(data[_len + i], 2);
        }
        return 10 * Math.log10(_TmpCount / (2 * _len));
    }

    @Override
    public float[] getScanDate() {
        float[] _date = readCMD(Attribute.SCANDATA);
        boolean _isOver = false;
        if (_date == null) {
            return null;
        }
        int _datalength = _date.length;
        if (_date[_datalength - 1] == 200) {
            System.arraycopy(_date, 0, scanData.date, scanData.index, _datalength - 1);
            _isOver = true;
            scanData.index +=  _datalength-1;
        } else {
            if(_datalength + scanData.index > scanData.date.length){
                scanData.index = 0;
            }
            System.arraycopy(_date, 0, scanData.date, scanData.index, _datalength);
            scanData.index += _datalength;
        }
        _datalength = (_isOver ? _datalength-1 : _datalength);
        return  getAvg(scanData.date,_isOver,scanData.index - _datalength,scanData.index);
    }


    private float[] getAvg(float[] pdate,boolean isOver,int begin,int length) {
        int _spceQueueSize = spceQueue.size();
        int _dateLen = pdate.length;
        float[] _tmp = null;
        if (_spceQueueSize >= 20) {
            _tmp = spceQueue.poll();
        }
        for (int i = begin; i < length; i++) {
            avgSpce[i] = (avgSpce[i] * _spceQueueSize + pdate[i] - (_tmp == null ? 0 : _tmp[i])) / (_spceQueueSize == 0 ? 1 : (_spceQueueSize < 20 ? _spceQueueSize + 1 : 20));
        }
        if(isOver)
            spceQueue.add(avgSpce);
        return avgSpce;
    }
    @Override
    public float[] getSpecDate() {
        float[] date = readCMD(Attribute.SPECTRUMDATA);
        if (date == null) {
            return null;
        }
        return date;
    }
    private float[] byteArraytoShortArray(byte[] data, int datalength) {
        byte bLength = 2;
        byte[] bTemp = new byte[bLength];
        float[] s = new float[datalength / bLength];
        for (int iLoop = 0; iLoop < s.length; iLoop++) {
            for (int jLoop = 0; jLoop < bLength; jLoop++) {
                bTemp[jLoop] = data[iLoop * bLength + jLoop];
            }
            s[iLoop] = (float) (CIOUtils.getShort(bTemp, 0) / 100.0);
        }
        return s;
    }
    private float[] readCMD(String pCMD) {
        byte[] _Data = null;
        if (sendCMD(pCMD)) {
            byte[] _HeadArray = new byte[7];
            if (readData(_HeadArray, 0, 1) == 1) {
                if (_HeadArray[0] == 35) {
                    if (readData(_HeadArray, 0, 1) == 1){
                        if (_HeadArray[0] > 48 && _HeadArray[0] < 57) {
                            int _dataOffset = Integer.parseInt(((char) _HeadArray[0]) + "");
                            if (_dataOffset == readData(_HeadArray, 0, _dataOffset)) {
                                String _DataLengthStr = new String(_HeadArray, 0, _dataOffset).trim();
                                if (Utils.isNumeric(_DataLengthStr)) {
                                    int _DataLength = Integer.parseInt(_DataLengthStr) + 2;
                                    byte[] _buf = readTcpData(_DataLength);
                                    if (_buf != null) {
                                        if ((_buf[_DataLength - 2] == 13) && (_buf[_DataLength - 1] == 10)) {
                                            return byteArraytoShortArray(_buf, _DataLength - 2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        resetInput();
        return null;
    }
}
