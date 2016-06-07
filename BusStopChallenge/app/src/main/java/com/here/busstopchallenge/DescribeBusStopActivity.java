package com.here.busstopchallenge;

import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.here.busstopchallenge.integration.ScbeAPI;

import java.util.List;
import java.util.Locale;

public class DescribeBusStopActivity extends AppCompatActivity {
    private Button describeBusButton;
    private TextToSpeech tts;
    private String busStopId;
    private HereTransitAPI transitApi = new HereTransitAPI();
    private ScbeAPI scbeApi = new ScbeAPI();
    private static final int HEADER_COLOR = Color.parseColor("#9999ee");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_describe_bus_stop);

        describeBusButton=(Button)findViewById(R.id.describeBusStop);

        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        busStopId = "";
        if (extras != null) {
             busStopId = extras.getString("BUS_STOP");
        }

        //api.getStationById(this, busStopId);
        BusStop busStop = transitApi.getCachedBusStop(busStopId);
        drawDescriptionTable(busStop);

        scbeApi.getUGCForBusStop(this, busStopId);
        describeBusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String spacifiedBusStopId = spacifyBusStopNumber(busStopId);
                BusStop cached = transitApi.getCachedBusStop(busStopId);
                String toSpeak = createBusStopDetailedDescription(cached);
                tts.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);

            }
        });
    }

    private String createBusStopDetailedDescription(BusStop b) {
        StringBuilder sb = new StringBuilder("Describing Bus stop  " + b.getName() + ".");

        if(b.hasBench() != null) {
            if(b.hasBench()) {
                sb.append("It has a bench.");
            } else {
                sb.append("It does not have a bench nearby.");
            }
        } else {
            sb.append("No bench information is available.");
        }
        if(b.hasShelter() != null) {
            if(b.hasShelter()) {
                sb.append("There is a shelter.");
            } else {
                sb.append("There is no shelter nearby.");
            }
        } else {
            sb.append("No shelter information is available.");
        }

        if(b.getAlsoNearby() != null) {
            if(!b.getAlsoNearby().isEmpty()) {
                sb.append("Also nearby :");
                for (String s : b.getAlsoNearby()) {
                    sb.append(s + ".");
                }
            } else {
                sb.append("No other nearby objects are noted.");
            }
        } else {
            sb.append("No other nearby objects are noted.");
        }

        return sb.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_describe_bus_stop, menu);
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
            tts.shutdown();
        }
        super.onPause();
    }

    public void drawDescriptionTable(BusStop busStop) {
        TableLayout stopTable = (TableLayout) this.findViewById(R.id.busStopDescriptionLayout);
        TableRow tbrow0 = new TableRow(this);
        TextView tv0 = new TextView(this);
        tv0.setText("Field");
        tv0.setTextColor(Color.BLACK);
        tv0.setBackgroundColor(HEADER_COLOR);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText("Value");
        tv1.setTextColor(Color.BLACK);
        tv1.setBackgroundColor(HEADER_COLOR);
        tbrow0.addView(tv1);

        stopTable.addView(tbrow0);


        TableRow stopRow = new TableRow(this);

        //Name
        TextView nameField = new TextView(this);
        nameField.setText("Name");
        nameField.setTextColor(Color.BLACK);
        nameField.setGravity(Gravity.LEFT);
        stopRow.addView(nameField);

        TextView nameValue = new TextView(this);
        nameValue.setText("" + busStop.getName());
        nameValue.setTextColor(Color.BLACK);
        nameValue.setGravity(Gravity.LEFT);
        stopRow.addView(nameValue);
        stopTable.addView(stopRow);

        //Distance
        stopRow = new TableRow(this);
        TextView distance = new TextView(this);
        distance.setText("Distance");
        distance.setTextColor(Color.BLACK);
        distance.setGravity(Gravity.LEFT);
        stopRow.addView(distance);

        TextView distanceValue = new TextView(this);
        distanceValue.setText("" + busStop.getDistanceInMeters());
        distanceValue.setTextColor(Color.BLACK);
        distanceValue.setGravity(Gravity.LEFT);
        stopRow.addView(distanceValue);
        stopTable.addView(stopRow);

        //Routes
        stopRow = new TableRow(this);
        TextView route = new TextView(this);
        route.setText("Routes Served");
        route.setTextColor(Color.BLACK);
        route.setGravity(Gravity.LEFT);
        stopRow.addView(route);

        TextView routeValues = new TextView(this);
        StringBuilder sb = new StringBuilder();
        for(String routeDesc : busStop.getRouteList()) {
            sb.append(routeDesc).append("\n");
        }

        routeValues.setText("" + sb.toString());
        routeValues.setTextColor(Color.BLACK);
        routeValues.setGravity(Gravity.LEFT);
        stopRow.addView(routeValues);
        stopTable.addView(stopRow);
    }
}
