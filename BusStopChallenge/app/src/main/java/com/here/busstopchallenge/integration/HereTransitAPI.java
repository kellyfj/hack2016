package com.here.busstopchallenge.integration;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;

import com.here.busstopchallenge.BusStop;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kellyfj on 6/6/16.
 */
public class HereTransitAPI {

    private static final String url = "https://cit.transit.api.here.com/search/by_geocoord.json?app_id=inhesa7azejETefrudAC&app_code=UP6A4YcFEAgshQMhc-sYsA" +
            "&y=42.365813&x=-71.185237&radius=100&max=20";
    private static final String TAG = "HereTransitAPI";
    private List<BusStop> busStops;

    public boolean hasBusStops() {
        return busStops != null;
    }

    public List<BusStop> getBusStops() {
        return Collections.unmodifiableList(busStops);
    }

    public Object getStationsNearby() {
        busStops = null;
        new HttpRequestTask().execute();

        return null;
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Map> {
        @Override
        protected Map doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Map obj = restTemplate.getForObject(url, LinkedHashMap.class);
                return obj;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Map obj) {
                Log.e(TAG, ""+obj);
                if(obj.containsKey("Res")) {
                    busStops = new ArrayList<>();
                    Map<String, Object> res = (Map<String, Object>) obj.get("Res");
                    Map<String, Object> stations = (Map<String, Object>) res.get("Stations");
                    List<Object> stnList = (List<Object>) stations.get("Stn");
                    Log.d(TAG, ""+stnList);
                    int i = 0;
                    for(Object stn : stnList) {
                        Map<String, Object> stnMap = (Map<String, Object>) stn;
                        String name = (String) stnMap.get("@name");
                        String distanceString = (String) stnMap.get("@distance");
                        Log.d(TAG, "Object " + i + ": " +stnMap);

                        Map<String, Object> lines = (Map<String, Object>) stnMap.get("Lines");
                        List<Object> routeList = (List<Object>) lines.get("Line");

                        List<String> routeDescriptionList = new ArrayList();
                        for(Object routeObj : routeList) {
                            Map<String, Object> route = ( Map<String, Object>) routeObj;
                            String routeName = (String) route.get("@name");
                            String routeDirection = (String) route.get("@dir");
                            String routeDescription = routeName + " towards " + routeDirection;
                            routeDescriptionList.add(routeDescription);
                        }
                        //Map<String, Object> lines =
                        i++;
                        BusStop b = new BusStop();
                        b.setName(name);
                        b.setDistanceInMeters(distanceString);
                        b.setRouteList(routeDescriptionList);
                        Log.i(TAG, ""+b.toString());
                        busStops.add(b);
                    }
                    Log.i(TAG, "Found " + i + " bus stops nearby");
                }
        }

    }
}
