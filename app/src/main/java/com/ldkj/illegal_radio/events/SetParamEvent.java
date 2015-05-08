package com.ldkj.illegal_radio.events;

/**
 * Created by john on 15-5-7.
 */
public class SetParamEvent {
    private String param;

    public SetParamEvent(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }
}
