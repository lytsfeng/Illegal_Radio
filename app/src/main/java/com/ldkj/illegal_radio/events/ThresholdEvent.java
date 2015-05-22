package com.ldkj.illegal_radio.events;

/**
 * Created by john on 15-5-14.
 */
public class ThresholdEvent {

    private int threshold;

    public ThresholdEvent(int threshold) {
        this.threshold = threshold;
    }

    public int getThreshold() {
        return threshold;
    }
}
