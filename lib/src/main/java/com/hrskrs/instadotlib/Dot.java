package com.hrskrs.instadotlib;

/**
 * Created by hrskrs on 10/16/17.
 */

public class Dot {
    enum State {
        SMALL,
        MEDIUM,
        INACTIVE,
        ACTIVE
    }

    private State state;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

}
