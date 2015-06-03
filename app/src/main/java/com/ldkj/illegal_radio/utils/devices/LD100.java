package com.ldkj.illegal_radio.utils.devices;

import com.ldkj.illegal_radio.models.DeviceConfig;
import com.ldkj.illegal_radio.models.Scan;
import com.ldkj.illegal_radio.models.Single;
import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.CIOUtils;
import com.ldkj.illegal_radio.utils.Net.NetUtil;
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
    private float multiple = 1;

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
    public boolean startScan() {

        if (!isconn) {
            return false;
        }
        sendCMD(Attribute.STOPSCAN);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendCMD("FREQ:MODE DSC");
        sendCMD("TRAC:FEED:CONT MTRAC,ALW");
        sendCMD("TRAC:FEED:CONT IFPAN,NEV");
        sendCMD("SENS:FREQ:DSCan:STAR 88000000 Hz");
        sendCMD("SENS:FREQ:DSCan:STOP 108000000 Hz");
        sendCMD("SENS:FREQ:DSCan:STEP 25000Hz");
        sendCMD("TRACE:FEED:CONTROL ITRACE,NEVER");
        sendCMD("TRACE:FEED:CONTROL IFPAN,NEVER");
        sendCMD("TRACE:FEED:CONTROL MTRACE,ALWAYS");
        sendCMD("INPut:ATTenuation:MODE NORM");
        sendCMD("INPut:ATT 30");
        sendCMD(Attribute.STARTSCAN);
        return true;
    }

    @Override
    public boolean startSingle() {
        if (!isconn) {
            return false;
        }
        Single _Single = Single.getSingle();
        sendCMD("FREQ:MODE CW");
        sendCMD("TRAC:FEED:CONT MTRAC,NEV");
        sendCMD("TRAC:FEED:CONT IFPAN,ALW");
        sendCMD("SENS:FREQ "+Utils.valueToHz(_Single.centFreq)+" Hz");
        sendCMD("FREQ:SPAN "+Utils.valueToHz(_Single.freqBandWidth)+" Hz");
        sendCMD("INPut:ATT " + _Single.attcontrol.substring(0, _Single.attcontrol.length() - 2));
        sendCMD("SYSTEM:CHANNEL:BANDwidth 0, "+Utils.valueToHz(_Single.filterBandwidth)+" Hz");
//        sendCMD("SENS:DEM " + _Single.demodulationMode);
        sendCMD("SYSTEM:CHAnnel:AUDio 0_");
        sendCMD("TRAC:FEED:CONT IF,ALW");
        sendCMD("SYSTEM:CHAnnel:IF 1_");
        sendCMD("TRAC:UDP:TAG \"" + NetUtil.getLocalIpAddress() + "\",\""
                + DeviceConfig.getDeviceConfig().udpPort + "\",AUDIO\n");
        String cmd = "SENSe:FUNCtion:ON  \"VOLT:AC\",\"FREQ:OFFS\",\"FSTR\",\"AM\",\"AM:POS\",\"AM:NEG\",\"FM\",\"FM:POS\",\"FM:NEG\",\"PM\",\"BAND\"";
        sendCMD(cmd);
        return true;
    }

    @Override
    public float[] getIQData() {
        multiple = 1;
        return readCMD(Attribute.IQMDATA1);

    }

    @Override
    public float[] getScanData() {
        multiple = 100;
        float[] _date = readCMD(Attribute.SCANDATA);
        boolean _isOver = false;
        if (_date == null) {
            return null;
        }
        int _datalength = _date.length;
        if(_datalength > scanData.date.length){
            return null;
        }

        if (_date[_datalength - 1] == 200) {
            if (_datalength + scanData.index > scanData.date.length + 1) {
                scanData.index = scanData.date.length - _datalength + 1;
            }
            System.arraycopy(_date, 0, scanData.date, scanData.index, _datalength - 1);
            _isOver = true;
            scanData.index += _datalength - 1;
        } else {
            if (_datalength + scanData.index > scanData.date.length) {
                scanData.index = 0;
            }
            System.arraycopy(_date, 0, scanData.date, scanData.index, _datalength);
            scanData.index += _datalength;
        }
        _datalength = (_isOver ? _datalength - 1 : _datalength);
        return getAvg(scanData.date, _isOver, scanData.index - _datalength, scanData.index);
    }


    private float[] getAvg(float[] pdate, boolean isOver, int begin, int length) {
        int _spceQueueSize = spceQueue.size();
        int _dateLen = pdate.length;
        float[] _tmp = null;
        if (_spceQueueSize >= Attribute.AVG_COUNT) {
            _tmp = spceQueue.poll();
        }
        for (int i = begin; i < length; i++) {
            avgSpce[i] = (avgSpce[i] * _spceQueueSize + pdate[i] - (_tmp == null ? 0 : _tmp[i])) / (_spceQueueSize == 0 ? 1 : (_spceQueueSize < Attribute.AVG_COUNT ? _spceQueueSize + 1 : Attribute.AVG_COUNT));
        }
        if (isOver)
            spceQueue.add(avgSpce);
        return avgSpce;
    }

    @Override
    public float[] getSpecData() {
        multiple = 100;
        float[] date = readCMD(Attribute.SPECTRUMDATA);
        if (date == null) {
            return null;
        }
        return date;
    }

    private float[] byteArraytoShortArray(byte[] data, int datalength, float Multiple) {
        byte bLength = 2;
        byte[] bTemp = new byte[bLength];
        float[] s = new float[datalength / bLength];
        for (int iLoop = 0; iLoop < s.length; iLoop++) {
            for (int jLoop = 0; jLoop < bLength; jLoop++) {
                bTemp[jLoop] = data[iLoop * bLength + jLoop];
            }
            s[iLoop] = (float) (CIOUtils.getShort(bTemp, 0) / Multiple);
        }
        return s;
    }

    private float[] readCMD(String pCMD) {
        byte[] _Data = null;
        if (sendCMD(pCMD)) {
            byte[] _HeadArray = new byte[7];
            if (readData(_HeadArray, 0, 1) == 1) {
                if (_HeadArray[0] == 35) {
                    if (readData(_HeadArray, 0, 1) == 1) {
                        if (_HeadArray[0] > 48 && _HeadArray[0] < 57) {
                            int _dataOffset = Integer.parseInt(((char) _HeadArray[0]) + "");
                            if (_dataOffset == readData(_HeadArray, 0, _dataOffset)) {
                                String _DataLengthStr = new String(_HeadArray, 0, _dataOffset).trim();
                                if (Utils.isNumeric(_DataLengthStr)) {
                                    int _DataLength = Integer.parseInt(_DataLengthStr) + 2;
                                    byte[] _buf = readTcpData(_DataLength);
                                    if (_buf != null) {
                                        if ((_buf[_DataLength - 2] == 13) && (_buf[_DataLength - 1] == 10)) {
                                            return byteArraytoShortArray(_buf, _DataLength - 2,multiple);
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
