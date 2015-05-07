package com.ldkj.illegal_radio.events;

/**
 * Created by john on 15-5-6.
 */
public class SpecEvent {

    private byte[] data;
    public SpecEvent(byte[] data) {
        this.data = data;
    }
    public byte[] getData() {
        return data;
    }
}
