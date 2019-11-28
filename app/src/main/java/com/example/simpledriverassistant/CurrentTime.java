package com.example.simpledriverassistant;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class CurrentTime {

    private static final String TAG = CurrentTime.class.getSimpleName();
    private SimpleDateFormat simpleDateFormat;
    private long date;
    private String currentTime;

    public CurrentTime() {
    }

    protected Long milliseconds() {
        date = System.currentTimeMillis();
        String milliseconds = String.valueOf(date);
        return date;
    }

    protected Long convertMillisecondsToMinutes(Long milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        return minutes;
    }

    protected String timeToString() {
        date = System.currentTimeMillis();
        simpleDateFormat = new SimpleDateFormat("mmssSSS");
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    protected String generateIdWithTime() {
        date = System.currentTimeMillis();
        simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }
}
