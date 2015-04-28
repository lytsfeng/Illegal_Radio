package com.ldkj.illegal_radio.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by john on 15-4-23.
 */
public class RadioModels implements Parcelable {
    public String name;                 //台标
    public double freq;                 //工作频率
    public double bandWidth;            //工作带宽
    public Date   startWork;            //工作起始时间
    public Date   endWork;              //工作结束时间
    public int    ver_num;              //数据库的版本号
    public boolean isDatebase = false;  //标识数据库中是否存在该数据。
    public boolean isIllegal = false;   //是否为黑广播


    public RadioModels() {
    }

    public RadioModels(String name, double freq, double bandWidth, Date startWork, Date endWork) {
        this.name = name;
        this.freq = freq;
        this.bandWidth = bandWidth;
        this.startWork = startWork;
        this.endWork = endWork;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeDouble(this.freq);
        dest.writeDouble(this.bandWidth);
        dest.writeLong(startWork != null ? startWork.getTime() : -1);
        dest.writeLong(endWork != null ? endWork.getTime() : -1);
        dest.writeInt(this.ver_num);
        dest.writeByte(isDatebase ? (byte) 1 : (byte) 0);
        dest.writeByte(isIllegal ? (byte) 1 : (byte) 0);
    }

    private RadioModels(Parcel in) {
        this.name = in.readString();
        this.freq = in.readDouble();
        this.bandWidth = in.readDouble();
        long tmpStartWork = in.readLong();
        this.startWork = tmpStartWork == -1 ? null : new Date(tmpStartWork);
        long tmpEndWork = in.readLong();
        this.endWork = tmpEndWork == -1 ? null : new Date(tmpEndWork);
        this.ver_num = in.readInt();
        this.isDatebase = in.readByte() != 0;
        this.isIllegal = in.readByte() != 0;
    }

    public static final Creator<RadioModels> CREATOR = new Creator<RadioModels>() {
        public RadioModels createFromParcel(Parcel source) {
            return new RadioModels(source);
        }

        public RadioModels[] newArray(int size) {
            return new RadioModels[size];
        }
    };
}
