package com.here.busstopchallenge;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

        api.getStationById(this, busStopId);

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
}
