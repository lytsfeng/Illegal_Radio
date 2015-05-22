package com.ldkj.illegal_radio.events;

import com.ldkj.illegal_radio.models.IllegalRadioModel;

/**
 * Created by john on 15-5-20.
 */
public class UpdateIllegalEvent {

    private IllegalRadioModel model;
    public UpdateIllegalEvent(IllegalRadioModel model) {
        this.model = model;
    }
    public IllegalRadioModel getModel() {
        return model;
    }
}
