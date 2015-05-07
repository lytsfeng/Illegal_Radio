package com.ldkj.illegal_radio.events;

/**
 * Created by john on 15-5-6.
 */
public class NetEvent {
    private boolean isConn;

    public NetEvent(boolean isConn){
        this.isConn = isConn;
    }

    public boolean isConn() {
        return isConn;
    }
}
