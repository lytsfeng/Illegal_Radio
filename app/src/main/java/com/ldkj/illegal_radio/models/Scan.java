package com.ldkj.illegal_radio.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.ldkj.illegal_radio.OwnApplication;

import java.math.BigDecimal;

/**
 * Created by john on 15-5-7.
 */
public class Scan {

    private static final String PREFERENCENAME = "com_ldkj_illegal_radio_scan";
    private static final String BEGINFREQ = "beginfraq";
    private static final String ENDFREQ = "endfreq";
    private static final String STEPFREQ = "stepfreq";

    public String beginFreq = "88000000";
    public String endFreq = "108000000";
    public String stepFreq = "25000";


    public int index = 0;
    public float[] date;


    public static Scan getScan() {
        Scan _Scan = new Scan();
        SharedPreferences preferences = OwnApplication.getContext().getSharedPreferences(PREFERENCENAME, Context.MODE_APPEND);
        _Scan.beginFreq = preferences.getString(BEGINFREQ, _Scan.beginFreq);
        _Scan.endFreq = preferences.getString(ENDFREQ, _Scan.endFreq);
        _Scan.stepFreq = preferences.getString(STEPFREQ, _Scan.stepFreq);
        _Scan.index = 0;
        BigDecimal begin = new BigDecimal(_Scan.beginFreq);
        BigDecimal end = new BigDecimal(_Scan.endFreq);
        BigDecimal setp = new BigDecimal(_Scan.stepFreq);
        int _length = Integer.parseInt(end.subtract(begin).divide(setp).toString()) + 1;
        _Scan.date = new float[_length];
        return _Scan;
    }

    public static void saveScan(Scan scan) {
        OwnApplication.getContext().getSharedPreferences(PREFERENCENAME, Context.MODE_APPEND).edit()
                .putString(BEGINFREQ, scan.beginFreq)
                .putString(ENDFREQ, scan.endFreq)
                .putString(STEPFREQ, scan.stepFreq)
                .commit();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Scan scan = (Scan) o;

        if (beginFreq != null ? !beginFreq.equals(scan.beginFreq) : scan.beginFreq != null)
            return false;
        if (endFreq != null ? !endFreq.equals(scan.endFreq) : scan.endFreq != null) return false;
        return !(stepFreq != null ? !stepFreq.equals(scan.stepFreq) : scan.stepFreq != null);

    }

    @Override
    public int hashCode() {
        int result = beginFreq != null ? beginFreq.hashCode() : 0;
        result = 31 * result + (endFreq != null ? endFreq.hashCode() : 0);
        result = 31 * result + (stepFreq != null ? stepFreq.hashCode() : 0);
        return result;
    }
}
