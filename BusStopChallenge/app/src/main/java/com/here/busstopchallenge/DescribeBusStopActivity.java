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
                    tts.setLanguage(Locale.UK);
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        busStopId = "";
        if (extras != null) {
             busStopId = extras.getString("BUS_STOP");
        }

        scbeApi.getUGCForBusStop(this, busStopId);
        //api.getStationById(this, busStopId);
        BusStop busStop = transitApi.getCachedBusStop(busStopId);


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
        sb.append(" ");
        
        if(b.hasBench() != null) {
            if(b.hasBench()) {
                sb.append("It has a bench.");
            } else {
                sb.append("It does not have a bench nearby.");
            }
        } else {
            sb.append("No bench information is available.");
        }
        sb.append(" ");

        if(b.hasShelter() != null) {
            if(b.hasShelter()) {
                sb.append("There is a shelter.");
            } else {
                sb.append("There is no shelter nearby.");
            }
        } else {
            sb.append("No shelter information is available.");
        }
        sb.append(" ");

        if(b.getAlsoNearby() != null) {
            if(!b.getAlsoNearby().isEmpty()) {
                sb.append("Also nearby :");
                for (String s : b.getAlsoNearby()) {
                    sb.append(s + ".").append(" ");
                }
            } else {
                sb.append("No other nearby objects are noted.");
            }
        } else {
            sb.append("No other nearby objects are noted.");
        }
        sb.append(" ");

        //Hack to fix saying "Row" the right way (as in column vs. row, not, my wife and
        // I are having a row)
        return sb.toString().replace("Row", "Roe");
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
            //tts.shutdown();
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
        nameField.setPadding(5,1,5,1);
        stopRow.addView(nameField);

        TextView nameValue = new TextView(this);
        nameValue.setText("" + busStop.getName());
        nameValue.setTextColor(Color.BLACK);
        nameValue.setGravity(Gravity.LEFT);
        nameValue.setPadding(5, 1, 5, 1);
        stopRow.addView(nameValue);
        stopTable.addView(stopRow);

        //Distance
        stopRow = new TableRow(this);
        TextView distance = new TextView(this);
        distance.setText("Distance");
        distance.setTextColor(Color.BLACK);
        distance.setGravity(Gravity.LEFT);
        distance.setPadding(5, 1, 5, 1);
        stopRow.addView(distance);

        TextView distanceValue = new TextView(this);
        distanceValue.setText("" + busStop.getDistanceInMeters() + " meters");
        distanceValue.setTextColor(Color.BLACK);
        distanceValue.setGravity(Gravity.LEFT);
        distanceValue.setPadding(5, 1, 5, 1);
        stopRow.addView(distanceValue);
        stopTable.addView(stopRow);

        //Routes
        stopRow = new TableRow(this);
        TextView route = new TextView(this);
        route.setText("Routes Served");
        route.setTextColor(Color.BLACK);
        route.setGravity(Gravity.LEFT);
        route.setPadding(5, 1, 5, 1);
        stopRow.addView(route);

        TextView routeValues = new TextView(this);
        StringBuilder sb = new StringBuilder();
        for(String routeDesc : busStop.getRouteList()) {
            sb.append(routeDesc).append("\n");
        }

        routeValues.setText("" + sb.toString());
        routeValues.setTextColor(Color.BLACK);
        routeValues.setGravity(Gravity.LEFT);
        routeValues.setPadding(5, 1, 5, 1);
        stopRow.addView(routeValues);
        stopTable.addView(stopRow);

        //Bench

        stopRow = new TableRow(this);
        TextView bench = new TextView(this);
        bench.setText("Has Bench?");
        bench.setTextColor(Color.BLACK);
        bench.setGravity(Gravity.LEFT);
        bench.setPadding(5, 1, 5, 1);
        stopRow.addView(bench);

        TextView benchValue = new TextView(this);
        benchValue.setText("" + ((busStop.hasBench() == null) ? "Unknown" : busStop.hasBench()));
        benchValue.setTextColor(Color.BLACK);
        benchValue.setGravity(Gravity.LEFT);
        benchValue.setPadding(5, 1, 5, 1);
        stopRow.addView(benchValue);
        stopTable.addView(stopRow);

        //Shelter
        stopRow = new TableRow(this);
        TextView shelter = new TextView(this);
        shelter.setText("Has Shelter?");
        shelter.setTextColor(Color.BLACK);
        shelter.setGravity(Gravity.LEFT);
        shelter.setPadding(5, 1, 5, 1);
        stopRow.addView(shelter);

        TextView shelterValue = new TextView(this);
        shelterValue.setText("" + ((busStop.hasShelter() == null) ? "Unknown": busStop.hasShelter()));
        shelterValue.setTextColor(Color.BLACK);
        shelterValue.setGravity(Gravity.LEFT);
        shelterValue.setPadding(5, 1, 5, 1);
        stopRow.addView(shelterValue);
        stopTable.addView(stopRow);

        stopRow = new TableRow(this);

        //Also nearby
        TextView nearby = new TextView(this);
        nearby.setText("Also Nearby");
        nearby.setTextColor(Color.BLACK);
        nearby.setGravity(Gravity.LEFT);
        nearby.setPadding(5, 1, 5, 1);
        stopRow.addView(nearby);

        TextView nearbyValues = new TextView(this);
        StringBuilder sb2 = new StringBuilder();
        if(busStop.getAlsoNearby() != null) {
            for (String s : busStop.getAlsoNearby()) {
                sb2.append(s).append("\n");
            }
        } else {
            sb2.append("Unknown");
        }

        nearbyValues.setText("" + sb2.toString());
        nearbyValues.setTextColor(Color.BLACK);
        nearbyValues.setGravity(Gravity.LEFT);
        nearbyValues.setPadding(5, 1, 5, 1);
        stopRow.addView(nearbyValues);
        stopTable.addView(stopRow);

        Button sayItButton = (Button) this.findViewById(R.id.describeBusStop);
        sayItButton.setEnabled(true);
    }
}
