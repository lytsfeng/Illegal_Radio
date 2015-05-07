package com.ldkj.illegal_radio.utils.Devices;

import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.CIOUtils;
import com.ldkj.illegal_radio.utils.Utils;
import com.ldkj.illegal_radio.utils.Devices.base.ADevice;

import java.io.IOException;

/**
 * Created by john on 15-5-6.
 */
public class LD100 extends ADevice {
    public LD100(String address, int port) {
        super(address, port);
    }

    @Override
    public String getDeviceName() {
        return "LD100";
    }

    @Override
    public double getLevel() {
        byte[] data = readCMD(Attribute.IQMDATA1);
        if(data == null){
            return 0;
        }
        short[] _IQ = CIOUtils.getShort(data);
        int _len = _IQ.length / 2;
        double _TmpCount = 0;
        for(int i = 0; i < _len; i++) {
            _TmpCount += Math.pow(_IQ[i], 2) + Math.pow(_IQ[_len + i], 2);
        }
        return 10 * Math.log10(_TmpCount/(2*_len));
    }

    @Override
    public short[] getScanDate() {
        byte[] _date = readCMD(Attribute.SCANDATA);
        if(_date == null){
            return null;
        }
        return byteArraytoShortArray(_date,_date.length-2);
    }
    @Override
    public short[] getSpecDate() {

        byte[] date = readCMD(Attribute.SPECTRUMDATA);
        if(date == null){
            return null;
        }
        return byteArraytoShortArray(date,date.length);
    }

    private short[]  byteArraytoShortArray(byte[] data,int datalength){
        byte bLength = 2;
        byte[] bTemp = new byte[bLength];
        short[] s = new short[datalength / bLength];
        for (int iLoop = 0; iLoop < s.length; iLoop++) {
            for (int jLoop = 0; jLoop < bLength; jLoop++) {
                bTemp[jLoop] = data[iLoop * bLength + jLoop];
            }
            s[iLoop] = CIOUtils.getShort(bTemp, 0);
        }
        return s;
    }

    private byte[] readCMD(String pCMD){
        byte[] _Data = null;
        if (sendCMD(pCMD)) {
            byte[] _HeadArray = new byte[7];
            try{
                if (readData(_HeadArray, 0, 2) == 2) {
                    if (_HeadArray[0] == 35) {
                        if (_HeadArray[1] < 48 || _HeadArray[1] > 57) {
                            return _Data;
                        }
                        int _dataOffset = Integer.parseInt(((char) _HeadArray[1]) + "");

                        if (_dataOffset != readData(_HeadArray, 0, _dataOffset)) {
                            return _Data;
                        }
                        String _DataLengthStr = new String(_HeadArray, 0, _dataOffset).trim();
                        if (!Utils.isNumeric(_DataLengthStr)) {
                            return _Data;
                        }
                        int _DataLength = Integer.parseInt(_DataLengthStr) + 2;

                        byte[] _buf = readTcpData(_DataLength);

                        if(!(_buf[_DataLength -2] == 13) && !(_buf[_DataLength-1] == 10)){
                            return _Data;
                        }
                        return _Data;
                    }
                }
            }catch (IOException e){

            }
        }
        return _Data;
    }
}
