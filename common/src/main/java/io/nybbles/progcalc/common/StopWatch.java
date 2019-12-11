package io.nybbles.progcalc.common;

import java.time.Duration;

public class StopWatch {
    private boolean _running;
    private long _startTime;
    private Duration _duration;

    public void start() {
        _running = true;
        _duration = null;
        _startTime = System.nanoTime();
    }

    public Duration stop() {
        if (!_running)
            return null;
        var endTime = System.nanoTime();
        _running = false;
        _duration = Duration.ofNanos(endTime - _startTime);
        return getElapsedTime();
    }

    public Duration getElapsedTime() {
        return _duration;
    }
}