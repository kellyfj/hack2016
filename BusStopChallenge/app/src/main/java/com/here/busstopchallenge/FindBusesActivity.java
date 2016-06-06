package com.here.busstopchallenge;

import android.content.Intent;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FindBusesActivity extends AppCompatActivity {
    private static final String TAG = "FindBusesActivity";
    private TextToSpeech tts;
    private Button sayFoundBusesButton;

    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_buses);

        gps = new GPSTracker(FindBusesActivity.this);

        // Check if GPS enabled
        if(gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }

        initTable();
        sayFoundBusesButton=(Button)findViewById(R.id.sayBusesFoundButton);

        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                }
            }
        });

        sayFoundBusesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<BusStop> nearby = getBusStopsNearby();

                String toSpeak = "There are " + nearby.size() + " bus stops near you";
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                tts.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);

                if(nearby.size() > 0) {
                    tts.speak("The routes include", TextToSpeech.QUEUE_ADD, null);
                    for (BusStop b : nearby) {
                        List<String> routes = b.getRouteList();
                        for(String route: routes) {
                            tts.speak("Route "+route, TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_buses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<BusStop> getBusStopsNearby() {
        BusStop b350 = new BusStop();
        List<String> routes = new ArrayList();
        routes.add("350");
        routes.add("354");
        b350.setRouteList(routes);
        b350.setStopId("8401");
        b350.setDistanceInMeters(48.0);

        BusStop b90 = new BusStop();
        b90.setRouteList(Arrays.asList(new String[]{"90"}));
        b90.setStopId("7201");
        b90.setDistanceInMeters(98.0);

        BusStop b94 = new BusStop();
        b94.setRouteList(Arrays.asList(new String[]{"94"}));
        b94.setStopId("4892");
        b94.setDistanceInMeters(201.0);

        List<BusStop> list = new ArrayList<>();
        list.add(b350);
        list.add(b90);
        list.add(b94);

        return list;
    }


    public void initTable() {
        TableLayout buses = (TableLayout) findViewById(R.id.busListLayout);
        TableRow tbrow0 = new TableRow(this);
        TextView tv0 = new TextView(this);
        tv0.setText(" Route # ");
        tv0.setTextColor(Color.BLACK);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" Stop # ");
        tv1.setTextColor(Color.BLACK);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(" Distance (Meters) ");
        tv2.setTextColor(Color.BLACK);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" Select ");
        tv3.setTextColor(Color.BLACK);
        tbrow0.addView(tv3);
        buses.addView(tbrow0);

        List<BusStop> busList = getBusStopsNearby();

        for (BusStop b : busList) {

            List<String> routes = b.getRouteList();

            for(String route : routes) {
                TableRow busRow = new TableRow(this);
                TextView routeView = new TextView(this);
                routeView.setText("" + route);
                routeView.setTextColor(Color.BLACK);
                routeView.setGravity(Gravity.CENTER);
                busRow.addView(routeView);

                TextView stopNumber = new TextView(this);
                stopNumber.setText("" + b.getStopId());
                stopNumber.setTextColor(Color.BLACK);
                stopNumber.setGravity(Gravity.CENTER);
                busRow.addView(stopNumber);

                TextView distance = new TextView(this);
                distance.setText("" + b.getDistanceInMeters());
                distance.setTextColor(Color.BLACK);
                distance.setGravity(Gravity.CENTER);
                busRow.addView(distance);


                Button describeMe = new Button(this);
                describeMe.setTag(b.getStopId());
                describeMe.setText("Describe");
                describeMe.setTextColor(Color.BLACK);
                describeMe.setGravity(Gravity.CENTER);
                describeMe.setTextSize(14.0f);
                describeMe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), DescribeBusStopActivity.class);
                        String stopNumber = (String) v.getTag();
                        intent.putExtra("BUS_STOP", stopNumber);
                        startActivity(intent);
                    }
                });

                busRow.addView(describeMe);

                buses.addView(busRow);
            }
        }

    }

    public void onPause(){
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }
}
