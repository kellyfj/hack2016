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

import com.here.busstopchallenge.integration.HereTransitAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FindBusesActivity extends AppCompatActivity {
    private static final String TAG = "FindBusesActivity";
    private TextToSpeech tts;
    private Button sayFoundBusesButton;
    private HereTransitAPI api = new HereTransitAPI();
    private GPSTracker gps;

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
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }


        api.getStationsNearby(this);

        //initTable();
        sayFoundBusesButton=(Button)findViewById(R.id.sayBusesFoundButton);

        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });

        sayFoundBusesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<BusStop> nearby = api.getBusStops();

                int numRoutes = 0;
                for(BusStop b : nearby) {
                    numRoutes += b.getRouteList().size();
                }

                String toSpeak = "There are " + nearby.size() + " bus stops near you. They cover " + numRoutes + " separate bus routes";
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                tts.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);

                if (nearby.size() > 0) {
                    tts.speak("The routes include", TextToSpeech.QUEUE_ADD, null);
                    for (BusStop b : nearby) {
                        List<String> routes = b.getRouteList();
                        for (String route : routes) {
                            tts.speak("Route " + route, TextToSpeech.QUEUE_ADD, null);
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



    public void onPause(){
        if(tts !=null){
            tts.stop();
            //tts.shutdown();  Required to avoid bug during demo
            // See http://stackoverflow.com/questions/36271853/pico-tts-engine-has-stopped-error
        }
        super.onPause();
    }
}
