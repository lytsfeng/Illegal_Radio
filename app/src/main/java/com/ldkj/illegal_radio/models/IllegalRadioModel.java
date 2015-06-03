package com.ldkj.illegal_radio.models;

/**
 * Created by john on 15-5-19.
 */
public class IllegalRadioModel  {

    public long uid = -1;
    public int handle = 0;
    public String freq = "";  // 带单位
    public String appeartime;
    public double lon;
    public double lat;
    public String address;
    public String tag;


    public IllegalRadioModel() {
    }

    public IllegalRadioModel(IllegalRadioModel pModel) {

        if(pModel != null){
            this.uid = pModel.uid;
            this.handle = pModel.handle;
            this.freq = pModel.freq;
            this.appeartime = pModel.appeartime;
            this.lon = pModel.lon;
            this.lat = pModel.lat;
            this.address = pModel.address;
            this.tag = pModel.tag;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IllegalRadioModel model = (IllegalRadioModel) o;

        if (Double.compare(model.lon, lon) != 0) return false;
        if (Double.compare(model.lat, lat) != 0) return false;
        return !(freq != null ? !freq.equals(model.freq) : model.freq != null);

    }
    @Override
    public int hashCode() {
        int result;
        long temp;
        result = freq != null ? freq.hashCode() : 0;
        temp = Double.doubleToLongBits(lon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public int  compareTo(IllegalRadioModel radioModel){
        if(radioModel == null){
            return 1;
        }
        if(this.handle == radioModel.handle){
            return 0;
        }
        if(this.handle > radioModel.handle){
            return 1;
        }
        return  -1;
    }

}
