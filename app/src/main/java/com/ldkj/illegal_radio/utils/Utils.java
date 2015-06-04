package com.ldkj.illegal_radio.utils;

import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.ldkj.illegal_radio.OwnApplication;
import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.models.IllegalRadioModel;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by john on 15-5-6.
 */
public class Utils {
    /**
     * 判断字符串是否为全数字组成
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if(TextUtils.isEmpty(str)){
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str.trim());
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


    public static double calculateLevelfromIQ(float[] data) {
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

    public static boolean isSetEqual(Set set1, Set set2) {
        if (set1 == null && set2 == null) {
            return true; // Both are null
        }
        if (set1 == null || set2 == null || set1.size() != set2.size()
                || set1.size() == 0 || set2.size() == 0) {
            return false;
        }
        Iterator ite1 = set1.iterator();
        Iterator ite2 = set2.iterator();
        boolean isFullEqual = true;
        while (ite2.hasNext()) {
            if (!set1.contains(ite2.next())) {
                isFullEqual = false;
            }
        }
        return isFullEqual;
    }

    public static float valueToMHz(String pValue) {
        float _value = 0;
        int _Length = pValue.length();
        String _Unit = pValue.substring(_Length - 3, _Length);
        double freq = Double.valueOf(pValue.substring(0, _Length - 3));
        if (_Unit.equalsIgnoreCase(OwnApplication.getContext().getResources().getString(R.string.ButtonTextMHZ))) {
            _value = (float) freq;
        } else if (_Unit.equalsIgnoreCase(OwnApplication.getContext().getResources().getString(R.string.ButtonTextGHZ))) {
            _value = (float) (freq * 1000);
        } else if (_Unit.equalsIgnoreCase(OwnApplication.getContext().getResources().getString(R.string.ButtonTextKHZ))) {
            _value = (float) (freq / 1000);
        }
        return _value;
    }

    public static long valueToHz(String pValue) {
        long _value = 0;
        int _Length = pValue.length();
        String _Unit = pValue.substring(_Length - 3, _Length);
        double freq = Double.valueOf(pValue.substring(0, _Length - 3));
        if (_Unit.equalsIgnoreCase(OwnApplication.getContext().getResources().getString(R.string.ButtonTextMHZ))) {
            _value = (long) (freq * 1000000);
        } else if (_Unit.equalsIgnoreCase(OwnApplication.getContext().getResources().getString(R.string.ButtonTextGHZ))) {
            _value = (long) (freq * 1000000000);
        } else if (_Unit.equalsIgnoreCase(OwnApplication.getContext().getResources().getString(R.string.ButtonTextKHZ))) {
            _value = (long) (freq * 1000);
        }
        return _value;
    }

    public static String removalUnit(String pValue) {
        if (pValue.toLowerCase().indexOf("z") < 0) {
            return pValue;
        }

        int _Length = pValue.length();
        return pValue.substring(0, _Length-3);
    }

    private int min(float[] pdate, int index) {
        int _step = 0;
        for (int i = index; i > 1; i--) {
            if (pdate[i] > pdate[i - 1]) {
                _step++;
            } else {
                break;
            }
        }
        return _step;
    }

    private int Max(float[] pdate, int index) {
        int _step = 0;
        int l = pdate.length - 1;
        for (int i = index; i < l; i--) {
            if (pdate[i] > pdate[i - 1]) {
                break;
            } else {
                _step++;
            }
        }
        return _step;
    }

    private static final String MAGIC = "EEEEEEEE";
    private static final String MINOR = "0100";
    private static final String MAJOR = "0200";
    /**
     * 验证数据头是否正确
     * @param pbuf
     * @return 返回数据长度
     */
    public static int isHead(byte[] pbuf) {

        int _Result = 0;
        if (pbuf.length < 20) {
            return _Result;
        }
        ByteBuffer in = ByteBuffer.wrap(pbuf);
        in.order(ByteOrder.LITTLE_ENDIAN);
        byte[] _magicArr = new byte[4];
        byte[] _minorArr = new byte[2];
        byte[] _majorArr = new byte[2];
        in.get(_magicArr);
        in.get(_minorArr);
        in.get(_majorArr);
        String _magic = DataConversion.ByteArrToHex(_magicArr);
        String _minor = DataConversion.ByteArrToHex(_minorArr);
        String _major = DataConversion.ByteArrToHex(_majorArr);
        if (MAGIC.equalsIgnoreCase(_magic)) {
            if (MAJOR.equalsIgnoreCase(_major) && MINOR.equalsIgnoreCase(_minor)) {
                in.position(16);
                _Result = in.getInt();
            }
        }
        return _Result;
    }


    public static Boolean isExit(String pFilePath){
        File file = new File(pFilePath);
        return file.exists();
    }

    public static String getSoundFileName(IllegalRadioModel model,Boolean isUnit){
        String _SoundFileName = "";

        String _time = model.appeartime.replaceAll(":","-");

        if(isUnit){
            _SoundFileName = MessageFormat.format("{0}{1}-{2}.wav",Attribute.FOLDER_PATH_SOUND,model.freq,_time);
       }else
        {
            _SoundFileName = MessageFormat.format("{0}{1}-{2}",Attribute.FOLDER_PATH_SOUND,model.freq,_time);
        }

               return _SoundFileName;
    }


    /**
     * 根据传入的文件夹路径 创建文件夹，并返回当前文件夹得路径
     *
     * @param pFolderPath 文件夹得路径
     * @return 文件夹得路径
     * @author 张亚峰
     */
    public static String onCreateFolder(String pFolderPath) {
        File _file = new File(pFolderPath);
        boolean _flag = true;
        if (!_file.exists()) {
            _flag = _file.mkdir();
        }
        return _file.getPath();
    }


    public static LatLng getLatLng(AMapLocation latLng){
        LatLng _latLng = new LatLng(Double.parseDouble(String.format("%.4f", latLng.getLatitude())), Double.parseDouble(String.format("%.4f", latLng.getLongitude())));
        return _latLng;
    }
    public static LatLng getLatLng(LatLng latLng){
        LatLng _latLng = new LatLng(Double.parseDouble(String.format("%.4f", latLng.latitude)), Double.parseDouble(String.format("%.4f", latLng.longitude)));
        return _latLng;
    }
}
