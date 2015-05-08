package com.ldkj.illegal_radio.utils.devices;

import com.ldkj.illegal_radio.models.Scan;
import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.CIOUtils;
import com.ldkj.illegal_radio.utils.Utils;
import com.ldkj.illegal_radio.utils.devices.base.ADevice;

import java.io.IOException;

/**
 * Created by john on 15-5-6.
 */
public class LD100 extends ADevice {


    private Scan scanData = null;
    public LD100(String address, int port) {
        super(address, port);
        scanData = new Scan();
        scanData.index = 0;
        scanData.date = new short[801];
    }

    @Override
    public String getDeviceName() {
        return "LD100";
    }

    @Override
    public double getLevel() {
        short[] data = readCMD(Attribute.IQMDATA1);
        if(data == null){
            return 0;
        }
        int _len = data.length / 2;
        double _TmpCount = 0;
        for(int i = 0; i < _len; i++) {
            _TmpCount += Math.pow(data[i], 2) + Math.pow(data[_len + i], 2);
        }
        return 10 * Math.log10(_TmpCount/(2*_len));
    }

    @Override
    public short[] getScanDate() {
        short[] _date = readCMD(Attribute.SCANDATA);
        if(_date == null){
            return null;
        }
        int _datalength = _date.length;
        if(_date[_datalength - 1] == 200){
            System.arraycopy(_date,0,scanData.date,scanData.index,_datalength-1);
            scanData.index = 0;
        }else {
            System.arraycopy(_date,0,scanData.date,scanData.index,_datalength);
            scanData.index += _datalength;
        }
        return scanData.date;
    }
    @Override
    public short[] getSpecDate() {

        short[] date = readCMD(Attribute.SPECTRUMDATA);
        if(date == null){
            return null;
        }
        return date;
    }

    private short[]  byteArraytoShortArray(byte[] data,int datalength){
        byte bLength = 2;
        byte[] bTemp = new byte[bLength];
        short[] s = new short[datalength / bLength];
        for (int iLoop = 0; iLoop < s.length; iLoop++) {
            for (int jLoop = 0; jLoop < bLength; jLoop++) {
                bTemp[jLoop] = data[iLoop * bLength + jLoop];
            }
            s[iLoop] = (short) (CIOUtils.getShort(bTemp, 0) / 100.0);
        }
        return s;
    }

    private short[] readCMD(String pCMD){
        byte[] _Data = null;
        if (sendCMD(pCMD)) {
            byte[] _HeadArray = new byte[7];
            try{
                if (readData(_HeadArray, 0, 2) == 2) {
                    if (_HeadArray[0] == 35) {
                        if (_HeadArray[1] > 48 || _HeadArray[1] < 57) {
                            int _dataOffset = Integer.parseInt(((char) _HeadArray[1]) + "");
                            if (_dataOffset == readData(_HeadArray, 0, _dataOffset)) {
                                String _DataLengthStr = new String(_HeadArray, 0, _dataOffset).trim();
                                if (Utils.isNumeric(_DataLengthStr)) {
                                    int _DataLength = Integer.parseInt(_DataLengthStr) + 2;
                                    byte[] _buf = readTcpData(_DataLength);
                                    if((_buf[_DataLength -2] == 13) && (_buf[_DataLength-1] == 10)){
                                        return byteArraytoShortArray(_Data,_DataLength-2);
                                    }
                                }
                            }
                        }
                    }
                }
            }catch (IOException e){

            }
        }
        return null;
    }
}
