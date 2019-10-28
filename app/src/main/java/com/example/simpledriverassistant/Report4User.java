package com.example.simpledriverassistant;

import android.util.Log;

public class Report4User {

    private static final String TAG = Report4User.class.getSimpleName();
    private String broadcaster;
    private String reportid;
    private Double latitudeReport;
    private Double longitudeReport;
    private Double distance;
    private String action;
    private Double raiting;

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

    public Double getRaiting() {
        return raiting;
    }

    public void setRaiting(Double raiting) {
        this.raiting = raiting;
    }

    protected void report4UserToString() {
        Log.d(TAG, "getBroadcaster " + getBroadcaster() + " getReportid " + getReportid() + " getAction " + getAction() + " getRaiting " + getRaiting() + " getDistance " + getDistance() + " getLatitudeReport " + getLatitudeReport() + " getLongitudeReport " + getLongitudeReport());
    }
}
