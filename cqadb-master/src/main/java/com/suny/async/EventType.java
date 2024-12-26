package com.suny.async;

/**
 * Created by admin on 23-2-7.4:16 pm
 */
public enum EventType {

    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
    FOLLOW(4),
    UNFOLLOW(5);

    private int value;


    EventType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
