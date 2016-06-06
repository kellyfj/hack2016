package com.here.busstopchallenge.integration;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.here.busstopchallenge.BusStop;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kellyfj on 6/6/16.
 */
public class ScbeAPI {

    private static final String SCBE_SEARCH_URL = "https://scbe.sit.api.here.com/scbe/v20/search?q=+type:busstop +placeId:";
    private static final String TAG = "ScbeAPI";
    private static final String ACCESS_TOKEN = "h1.PH3t8CcKm3XCVddsP96qgA.oWVHl-KVmz5oY1ikD63vjtIMMSyU3GaOuVgDDrwMwKPmZjO5o7YDkBSfDDSq4N2JBjopoPYQpf_-NZWGjJmJYyHLAsJtosI7MXLl6FYiEcSxVSiuM3sse8FflyN6jQEkwsD7C6C4Rn570ujk544hnX66nVBO6eOzByJXxeg-P1TFQe_qlveHefC2uKTPWf6crJL-ICLtzBR7yXsGc2vkhj5B9XcPHERvuhMlagbCUmHoSTBi6VSn9kLFKoFkcBE7T0RL0ibw5P6pnfvU1Jxt16ie2xQ9aKTtHmCeiwCIXflovGsbhG0hip57W2rdnrfBLpQSaeukPvw2J_PC_ZpQrjWmb_JMDfyyEgAr1BHgZ84EEpsCYBt4nLcnQhrXpYvV5vtW3v08PJ99AZLcTCiQrEBIm3vWGml8QYj8FxPlHDw.PjC9JvaQf6p-w6jwOoisVZmjFaeFaUyuHjJNMmXY1d292pdRhX851DYbn39FYnCdz4Br2Fkm5Il8Wr0etcngU-IWQF4-YmpmS0cbGN6nQMFtFnVC1fXzi8tiA-qcQUcqv8WWoZpZhXehwRuzwn-6iX5NkLfoM_dTsbCbM6xYhllM5Xzzn-linUtqt9LkgTMIuMbPDHnoCjy3CuiGdRe1oKNBi_QMKNNa4PeOwsX-SCFls1wP-RXHLBgzj8hqxOtCtYtyzVMUiLIaF1LYXGVLHuJTRSkXd58dA10EYU5XoPnLNQjWbOh9591wpKG1COj3cznFYDBYzoEfCGW6B1mkyg";

    public Object getUGCForBusStop(Activity callback, String stopId) {
        //this.callback = callback;
        new SearchForBusStopUGC(stopId).execute();

        return null;
    }

    private class SearchForBusStopUGC extends AsyncTask<Void, Void, Map> {
        private String stopId;

        public SearchForBusStopUGC(String stopId) {
            this.stopId = stopId;
        }

        @Override
        protected Map doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                String url = SCBE_SEARCH_URL+stopId;

                HttpHeaders headers = new HttpHeaders();
                headers.set("X-SCBE-USER-ID", "application");
                headers.set("Authorization","Bearer "+ACCESS_TOKEN);

                //Map obj = restTemplate.getForObject(url, LinkedHashMap.class);
                HttpEntity<String> entity = new HttpEntity<String>(headers);
                Object obj = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
                ResponseEntity<HashMap> re = (ResponseEntity<HashMap>) obj;
                Log.e(TAG, ""+obj);
                return re.getBody();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Map obj) {
            Log.e(TAG, "" + obj);

        }

    }
}
