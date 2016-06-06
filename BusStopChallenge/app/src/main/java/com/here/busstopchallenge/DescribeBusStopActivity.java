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

import java.util.List;
import java.util.Locale;

public class DescribeBusStopActivity extends AppCompatActivity {
    private Button describeBusButton;
    private TextToSpeech tts;
    private String busStopId;
    private HereTransitAPI api = new HereTransitAPI();

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
        BusStop busStop = api.getCachedBusStop(busStopId);
        drawDescriptionTable(busStop);

        describeBusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String spacifiedBusStopId = spacifyBusStopNumber(busStopId);
                BusStop cached = api.getCachedBusStop(busStopId);
                String toSpeak = "Describing Bus stop  " + cached.getName();
                tts.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);

            }
        });
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
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText("Value");
        tv1.setTextColor(Color.BLACK);
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
    }
}
