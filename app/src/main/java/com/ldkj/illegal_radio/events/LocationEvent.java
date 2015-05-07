package com.ldkj.illegal_radio.events;

import com.amap.api.maps.model.LatLng;

/**
 * Created by john on 15-5-7.
 */
public class LocationEvent {

    private LatLng latLng;

    public LocationEvent(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
