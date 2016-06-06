package com.here.busstopchallenge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kellyfj on 6/2/16.
 */
public class BusStop {

    private List<String> routeList = new ArrayList<String>();
    private String stopId;
    private Double distanceInMeters;
    private String stopPoleOrShelterDescription;
    private boolean hasBench;
    private String benchRelativeLocation;
    private boolean hasShelter;
    private String shelterRelativeLocation;
    private List<String> alsoNearby;
    private String otherHelpfulText;
    private List<String> scbeAudioObjectIds;

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

    public Double getDistanceInMeters() {
        return distanceInMeters;
    }

    public void setDistanceInMeters(double d) {
        distanceInMeters = d;
    }

}
