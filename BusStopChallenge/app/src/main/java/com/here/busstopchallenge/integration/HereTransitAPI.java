package com.here.busstopchallenge.integration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.here.busstopchallenge.BusStop;
import com.here.busstopchallenge.DescribeBusStopActivity;
import com.here.busstopchallenge.R;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kellyfj on 6/6/16.
 */
public class HereTransitAPI {
    private static final String APP_ID = "inhesa7azejETefrudAC";
    private static final String APP_CODE = "UP6A4YcFEAgshQMhc-sYsA";
    //Perkins school is close to Watertown square so using that for basic testing
    private static final String WATERTOWN_LAT = "42.365813";
    private static final String WATERTOWN_LON = "-71.185237";
    private static final Map<String, BusStop> BUS_STOP_CACHE = new HashMap<String, BusStop>();
    //Currently using Customer Integration Testing (CIT) environment
    private static final String SEARCH_URL = "https://cit.transit.api.here.com/search/by_geocoord.json?" +
            "app_id="+APP_ID+"&app_code=" +APP_CODE +
            "&y=" + WATERTOWN_LAT + "&x=" + WATERTOWN_LON + "&radius=50&max=20";

    private static final String STOP_IDS_URL = "https://cit.transit.api.here.com/search/by_stopids.json?" +
            "app_id=" +APP_ID +
            "&app_code=" + APP_CODE +
            "&stopIds=REPLACE_ME" +
            "&lang=en";

    private static final String TAG = "HereTransitAPI";
    private static final int HEADER_COLOR = Color.parseColor("#9999ee");
    private List<BusStop> busStops;
    private Activity callback;

    public List<BusStop> getBusStops() {
        return Collections.unmodifiableList(busStops);
    }

    public BusStop getCachedBusStop(String id) {
        return BUS_STOP_CACHE.get(id);
    }

    public Object getStationsNearby(Activity callback) {
        this.callback = callback;
        busStops = null;
        new SearchForBusStopsNearbyTask().execute();

        return null;
    }

    public Object getStationById(Activity callback, String id) {
        this.callback = callback;
        new RetrieveBusStopIdTask(id).execute();

        return null;
    }

    private class SearchForBusStopsNearbyTask extends AsyncTask<Void, Void, Map> {
        @Override
        protected Map doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Map obj = restTemplate.getForObject(SEARCH_URL, LinkedHashMap.class);
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
                        String stopId = (String) stnMap.get("@id");
                        String lat = (String) stnMap.get("@y");
                        String lon = (String) stnMap.get("@x");
                        Log.d(TAG, "Object " + i + ": " +stnMap);

                        Map<String, Object> lines = (Map<String, Object>) stnMap.get("Lines");
                        List<Object> routeList = (List<Object>) lines.get("Line");

                        List<String> routeDescriptionList = new ArrayList();
                        for(Object routeObj : routeList) {
                            Map<String, Object> route = ( Map<String, Object>) routeObj;
                            String routeName = (String) route.get("@name");
                            String routeDirection = (String) route.get("@dir");
                            String routeDescription = routeName + " towards \n " + routeDirection;
                            routeDescriptionList.add(routeDescription);
                        }
                        //Map<String, Object> lines =
                        i++;
                        BusStop b = new BusStop();
                        b.setStopId(stopId);
                        b.setName(name);
                        b.setDistanceInMeters(distanceString);
                        b.setRouteList(routeDescriptionList);
                        b.setLat(lat);
                        b.setLon(lon);
                        Log.i(TAG, ""+b.toString());
                        busStops.add(b);
                        BUS_STOP_CACHE.put(b.getStopId(), b);
                    }
                    Log.i(TAG, "Found " + i + " bus stops nearby");
                }

            drawTable(busStops);
        }

    }

    public void drawTable(List<BusStop> busList) {
        TableLayout busesTable = (TableLayout) callback.findViewById(R.id.busListLayout);
        TableRow tbrow0 = new TableRow(callback);
        TextView tv0 = new TextView(callback);
        tv0.setText("Route \n Description");
        tv0.setTextColor(Color.BLACK);
        tv0.setBackgroundColor(HEADER_COLOR);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(callback);
        tv1.setText("Stop \n Name");
        tv1.setTextColor(Color.BLACK);
        tv1.setBackgroundColor(HEADER_COLOR);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(callback);
        tv2.setText("Distance \n (Meters)");
        tv2.setTextColor(Color.BLACK);
        tv2.setBackgroundColor(HEADER_COLOR);
        tbrow0.addView(tv2);

        busesTable.addView(tbrow0);


        for (BusStop b : busList) {

            List<String> routes = b.getRouteList();

            for(String route : routes) {
                TableRow busRow = new TableRow(callback);
                Button describeMe = new Button(callback);
                if(route.length() > 30) {
                    route = route.substring(0,30);
                }
                describeMe.setTag(b.getStopId());
                describeMe.setText(""+ route);
                describeMe.setTextColor(Color.BLACK);
                describeMe.setGravity(Gravity.CENTER);
                describeMe.setTextSize(14.0f);
                describeMe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(callback.getApplicationContext(), DescribeBusStopActivity.class);
                        String stopId = (String) v.getTag();
                        intent.putExtra("BUS_STOP", stopId);
                        callback.startActivity(intent);
                    }
                });
                busRow.addView(describeMe);

                TextView stopNumber = new TextView(callback);
                stopNumber.setText("" + b.getName());
                stopNumber.setTextColor(Color.BLACK);
                stopNumber.setGravity(Gravity.LEFT);
                busRow.addView(stopNumber);

                TextView distance = new TextView(callback);
                distance.setText("" + b.getDistanceInMeters());
                distance.setTextColor(Color.BLACK);
                distance.setGravity(Gravity.LEFT);
                busRow.addView(distance);

                busesTable.addView(busRow);
            }
        }

    }


    /**
     * This class isn't used but could be useful
     */
    private class RetrieveBusStopIdTask extends AsyncTask<Void, Void, Map> {
        private String stopId;

        public RetrieveBusStopIdTask(String stopId) {
            this.stopId = stopId;
        }

        @Override
        protected Map doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                String url = STOP_IDS_URL.replace("REPLACE_ME", stopId);
                Map obj = restTemplate.getForObject(url, LinkedHashMap.class);
                return obj;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Map obj) {
            Log.e(TAG, "" + obj);
            BusStop b = null;
            if(obj.containsKey("Res")) {
                Map<String, Object> res = (Map<String, Object>) obj.get("Res");
                Map<String, Object> stations = (Map<String, Object>) res.get("Stations");
                List<Object> stnList = (List<Object>) stations.get("Stn");
                Log.d(TAG, "" + stnList);
                int i = 0;
                for (Object stn : stnList) {
                    Map<String, Object> stnMap = (Map<String, Object>) stn;
                    String name = (String) stnMap.get("@name");
                    //String distanceString = (String) stnMap.get("@distance");
                    String stopId = (String) stnMap.get("@id");
                    Log.d(TAG, "Object " + i + ": " + stnMap);
                    String lat = (String) stnMap.get("@y");
                    String lon = (String) stnMap.get("@x");

                    /*
                    Get by StopID does not include Line (Bus Route) info
                     */
                    i++;
                    b = new BusStop();
                    b.setStopId(stopId);
                    b.setName(name);
                    b.setLat(lat);
                    b.setLon(lon);
                    Log.i(TAG, "" + b.toString());
                }
                Log.i(TAG, "Found " + i + " bus stops for Id " + stopId);
                //Do something on callback
            }
        }

    }



}
