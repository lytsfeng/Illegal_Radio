package com.ldkj.illegal_radio.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.ldkj.illegal_radio.OwnApplication;

/**
 * Created by john on 15-5-18.
 */
public class Single {


    private static final String NAME = "Illega_Single";

    private static final String CENTFREQ = "centFreq";    //中心频率
    private static final String FREQBANDWIDTH = "freqBandWidth";        //频谱带宽
    private static final String FILTERBANDWIDTH = "filterBandwidth";      //滤波带宽
    private static final String ATTCONTROL = "attcontrol";           //衰减控制
    private static final String DEMODULATIONMODE = "demodulationMode";     //解调模式
    private static final String SOUND = "sound";



    public String centFreq = "91.4MHz";    //中心频率
    public String freqBandWidth = "250KHz";        //频谱带宽
    public String filterBandwidth = "50KHz";      //滤波带宽
    public String attcontrol = "10dB";           //衰减控制
    public String demodulationMode = "FM";     //解调模式


    public  static  Single getSingle(){
        Single _Single = new Single();
        SharedPreferences _Preferences = OwnApplication.getContext().getSharedPreferences(NAME, Context.MODE_APPEND);
        _Single.centFreq = _Preferences.getString(CENTFREQ, _Single.centFreq);
        _Single.freqBandWidth = _Preferences.getString(FREQBANDWIDTH, _Single.freqBandWidth);
        _Single.filterBandwidth = _Preferences.getString(FILTERBANDWIDTH, _Single.filterBandwidth);
        _Single.attcontrol = _Preferences.getString(ATTCONTROL, _Single.attcontrol);
        _Single.demodulationMode = _Preferences.getString(DEMODULATIONMODE,_Single.demodulationMode);
        return _Single;
    }
    public static void setSingle(Single pSingle){
        SharedPreferences _Preferences = OwnApplication.getContext().getSharedPreferences(NAME, Context.MODE_APPEND);
        SharedPreferences.Editor _Editor = _Preferences.edit();
        _Editor.putString(CENTFREQ, pSingle.centFreq);
        _Editor.putString(FILTERBANDWIDTH, pSingle.filterBandwidth);
        _Editor.putString(FREQBANDWIDTH, pSingle.freqBandWidth);
        _Editor.putString(ATTCONTROL, pSingle.attcontrol);
        _Editor.putString(DEMODULATIONMODE, pSingle.demodulationMode);
        _Editor.commit();
    }

    public static Single setSingle(String pFreq){
        Single _Single = getSingle();
        _Single.centFreq = pFreq;
        setSingle(_Single);
        return  _Single;
    }




}
