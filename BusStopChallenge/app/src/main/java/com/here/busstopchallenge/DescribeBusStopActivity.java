package com.here.busstopchallenge;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class DescribeBusStopActivity extends AppCompatActivity {
    private Button describeBusButton;
    private TextToSpeech tts;
    private String busStopId;

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

        describeBusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String spacifiedBusStopId = spacifyBusStopNumber(busStopId);
                String toSpeak = "Describing Bus stop Id " + spacifiedBusStopId;
                tts.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);

            }
        });
    }

    private String spacifyBusStopNumber(String s) {

        char[] original = s.toCharArray();
        char[] newArray = new char[original.length*2+1];
        int j=0;

        for(int i=0; i< original.length; i++) {
            newArray[j++] = original[i];
            newArray[j++] = ' ';
        }

        return new String(newArray).trim();
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
