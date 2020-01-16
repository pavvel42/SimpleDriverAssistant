package com.example.simpledriverassistant.Beans;

import android.util.Log;

public class Report4User {

    private final String TAG = Report4User.class.getSimpleName();
    private String broadcaster;
    private String reportid;
    private Double latitudeReport;
    private Double longitudeReport;
    private Double distance;
    private String action;
    private Double rating;

    public Report4User() {
    }

    public String getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(String broadcaster) {
        this.broadcaster = broadcaster;
    }

    public String getReportid() {
        return reportid;
    }

    public void setReportid(String reportid) {
        this.reportid = reportid;
    }

    public Double getLatitudeReport() {
        return latitudeReport;
    }

    public void setLatitudeReport(Double latitudeReport) {
        this.latitudeReport = latitudeReport;
    }

    public Double getLongitudeReport() {
        return longitudeReport;
    }

    public void setLongitudeReport(Double longitudeReport) {
        this.longitudeReport = longitudeReport;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void report4UserToString() {
        Log.d(TAG, "getBroadcaster " + getBroadcaster() + " getReportid " + getReportid() + " getAction " + getAction() + " getRating " + getRating() + " getDistance " + getDistance() + " getLatitudeReport " + getLatitudeReport() + " getLongitudeReport " + getLongitudeReport());
    }
}
