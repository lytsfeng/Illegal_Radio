package com.ldkj.illegal_radio.models;

/**
 * Created by john on 15-4-23.
 */
public class RadioModels {
    public long id;
    public String name;
    public String freq;
    public String span;
    public double lon;
    public double lat;
    public String address;
    public String beginTime;
    public String engTime;
    public int power;
    public String tag;


    public String getFreq() {

        Long _freq = Long.parseLong(freq.trim());
        double _f = _freq / (1000.0 * 1000.0);
        return String.format("%.2fMHz",_f);
    }

    public String getSpan() {
        Long _freq = Long.parseLong(span.trim());
        double _f = _freq / (1000.0);
        return String.format("%.0fKHz",_f);
    }
}
