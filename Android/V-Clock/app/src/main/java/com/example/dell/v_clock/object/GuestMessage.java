package com.example.dell.v_clock.object;

/**
 * Created by andy on 7/6/17.
 */

public class GuestMessage {

    private String guestName;
    private String arriveTime;

    public GuestMessage(String guestName, String arriveTime) {
        this.guestName = guestName;
        this.arriveTime = arriveTime;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getArriveTime() {
        return arriveTime;
    }
}
