package com.ldkj.illegal_radio.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by john on 15-5-7.
 */
public class StationModel implements Parcelable
{
    private double id;
    private String name;
    private double freq;
    private int    span;
    private double lon;
    private double lat;
    private String address;
    private int power;
    private Date   begindate;
    private Date   enddate;
    private String tag;

    public void setId(double id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFreq(double freq) {
        this.freq = freq;
    }

    public void setSpan(int span) {
        this.span = span;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public void setBegindate(Date begindate) {
        this.begindate = begindate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public double getId() {

        return id;
    }

    public String getName() {
        return name;
    }

    public double getFreq() {
        return freq;
    }

    public int getSpan() {
        return span;
    }

    public double getLon() {
        return lon;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public int getPower() {
        return power;
    }

    public Date getBegindate() {
        return begindate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.id);
        dest.writeString(this.name);
        dest.writeDouble(this.freq);
        dest.writeInt(this.span);
        dest.writeDouble(this.lon);
        dest.writeDouble(this.lat);
        dest.writeString(this.address);
        dest.writeInt(this.power);
        dest.writeLong(begindate != null ? begindate.getTime() : -1);
        dest.writeLong(enddate != null ? enddate.getTime() : -1);
        dest.writeString(this.tag);
    }

    public StationModel() {
    }

    private StationModel(Parcel in) {
        this.id = in.readDouble();
        this.name = in.readString();
        this.freq = in.readDouble();
        this.span = in.readInt();
        this.lon = in.readDouble();
        this.lat = in.readDouble();
        this.address = in.readString();
        this.power = in.readInt();
        long tmpBegindate = in.readLong();
        this.begindate = tmpBegindate == -1 ? null : new Date(tmpBegindate);
        long tmpEnddate = in.readLong();
        this.enddate = tmpEnddate == -1 ? null : new Date(tmpEnddate);
        this.tag = in.readString();
    }

    public static final Creator<StationModel> CREATOR = new Creator<StationModel>() {
        public StationModel createFromParcel(Parcel source) {
            return new StationModel(source);
        }

        public StationModel[] newArray(int size) {
            return new StationModel[size];
        }
    };

}
