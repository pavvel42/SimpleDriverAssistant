package com.example.simpledriverassistant.Support;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class CurrentTime {

    private final String TAG = CurrentTime.class.getSimpleName();
    private SimpleDateFormat simpleDateFormat;
    private long date;
    private String currentTime;

    public CurrentTime() {
    }

    public Long milliseconds() {
        date = System.currentTimeMillis();
        String milliseconds = String.valueOf(date);
        return date;
    }

    public Long convertMillisecondsToMinutes(Long milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        return minutes;
    }

    public String timeToString() {
        date = System.currentTimeMillis();
        simpleDateFormat = new SimpleDateFormat("mmssSSS");
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    public String generateIdWithTime() {
        date = System.currentTimeMillis();
        simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }
}
