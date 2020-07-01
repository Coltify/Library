package com.ddylan.library.util;

public class Stopwatch {

    private long started;
    private long ended;

    Stopwatch(long started) {
        this.started = started;
    }

    public void stop() {
        this.ended = System.currentTimeMillis();
    }

    public long elapsed() {
        return this.ended - this.started;
    }

    public static Stopwatch createStarted() {
        return new Stopwatch(System.currentTimeMillis());
    }

}
