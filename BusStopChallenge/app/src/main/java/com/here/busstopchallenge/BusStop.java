package com.here.busstopchallenge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kellyfj on 6/2/16.
 */
public class BusStop {

    private List<String> routeList = new ArrayList<String>();
    private String name;
    private String stopId;
    private String distanceInMeters;
    private String stopPoleOrShelterDescription;
    private Boolean hasBench;
    private String benchRelativeLocation;
    private Boolean hasShelter;
    private String shelterRelativeLocation;
    private List<String> alsoNearby;
    private String otherHelpfulText;
    private List<String> scbeAudioObjectIds;
    private String lat;
    private String lon;

    public void setRouteList(List<String> s) {
        routeList = new ArrayList<String>(s);
        Collections.sort(routeList);
    }

    public List<String> getRouteList() {
        return routeList;
    }


    public String getStopId() {
        return stopId;
    }
    public void setStopId(String s) {
        stopId = s;
    }

    public String getDistanceInMeters() {
        return distanceInMeters;
    }

    public void setDistanceInMeters(String d) {
        distanceInMeters = d;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setLat(String s) {
        this.lat = s;
    }

    public String getLat() {
        return this.lat;
    }

    public void setLon(String s) {
        this.lon = s;
    }

    public String getLon() {
        return this.lon;
    }

    public Boolean hasBench() {
        return hasBench;
    }

    public Boolean hasShelter() {
        return hasShelter;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name)
                .append("\n Id : " + stopId)
                .append("\n Distance in Meters: ").append(distanceInMeters)
                .append("\n Lat: ").append(lat)
                .append("\n Lon: ").append(lon);

        sb.append("\n Routes Include: ");

        for(String routeDescription : routeList) {
            sb.append("\n\t " + routeDescription);
        }
        return sb.toString();
    }
}
