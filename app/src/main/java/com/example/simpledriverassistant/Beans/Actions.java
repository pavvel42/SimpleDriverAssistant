package com.example.simpledriverassistant.Beans;

public class Actions {

    private String carAccident = "carAccident";
    private String roadsideInspection = "roadsideInspection";
    private String speedCamera = "speedCamera";
    private String roadworks = "roadworks";

    public Actions() {
    }

    public void setCarAccident(String carAccident) {
        this.carAccident = carAccident;
    }

    public void setRoadsideInspection(String roadsideInspection) {
        this.roadsideInspection = roadsideInspection;
    }

    public void setSpeedCamera(String speedCamera) {
        this.speedCamera = speedCamera;
    }

    public void setRoadworks(String roadworks) {
        this.roadworks = roadworks;
    }

    public String getCarAccident() {
        return carAccident;
    }

    public String getRoadsideInspection() {
        return roadsideInspection;
    }

    public String getSpeedCamera() {
        return speedCamera;
    }

    public String getRoadworks() {
        return roadworks;
    }
}
