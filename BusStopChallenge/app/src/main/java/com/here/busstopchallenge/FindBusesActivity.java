package com.here.busstopchallenge;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_buses);

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

                String toSpeak = "There are " + nearby.size() + " buses nearby";
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

        BusStop b90 = new BusStop();
        b90.setRouteList(Arrays.asList(new String[]{"90"}));

        BusStop b94 = new BusStop();
        b94.setRouteList(Arrays.asList(new String[]{"94"}));

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
        tv0.setText(" Bus # ");
        tv0.setTextColor(Color.BLACK);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" Description ");
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
        for (int i = 0; i < 4; i++) {
            TableRow busRow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText("" + i);
            t1v.setTextColor(Color.BLACK);
            t1v.setGravity(Gravity.CENTER);
            busRow.addView(t1v);
            TextView t2v = new TextView(this);
            t2v.setText("Product " + i);
            t2v.setTextColor(Color.BLACK);
            t2v.setGravity(Gravity.CENTER);
            busRow.addView(t2v);
            TextView t3v = new TextView(this);
            t3v.setText("Rs." + i);
            t3v.setTextColor(Color.BLACK);
            t3v.setGravity(Gravity.CENTER);
            busRow.addView(t3v);
            TextView t4v = new TextView(this);
            t4v.setText("" + i * 15 / 32 * 10);
            t4v.setTextColor(Color.BLACK);
            t4v.setGravity(Gravity.CENTER);
            busRow.addView(t4v);
            buses.addView(busRow);
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
