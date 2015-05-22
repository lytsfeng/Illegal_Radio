package com.ldkj.illegal_radio.events;

import com.ldkj.illegal_radio.models.IllegalRadioModel;

/**
 * Created by john on 15-5-22.
 */
public class DrawMarkerEvent {
    IllegalRadioModel radioModel;

    public DrawMarkerEvent(IllegalRadioModel radioModel) {
        this.radioModel = radioModel;
    }

    public IllegalRadioModel getRadioModel() {
        return radioModel;
    }
}


