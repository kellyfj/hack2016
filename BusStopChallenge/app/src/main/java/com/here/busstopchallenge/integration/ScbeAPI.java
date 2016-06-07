package com.here.busstopchallenge.integration;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.here.busstopchallenge.BusStop;
import com.here.busstopchallenge.DescribeBusStopActivity;

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
    private DescribeBusStopActivity callback;

    //This is a very short-lived key lasting at most 24 hours
    private static final String ACCESS_TOKEN = "h1.5d0FQWrZkPcTz1EqJKCb6w.kaBcnjyGI-E_eqZXNmoXucgqzOM4aoRWxP7g8kiZYSEDuCnij5Bp0tt2O1xFyhrUNojd6bhAVwEqxc6te8cHKuVikhHLnh5iDyL_mmaVwTN9u3aPLfMpRq7YaRnChnNNKCAskzMZrLGdcNgU_9tEM_jpIz6zTOBORaSi3lqXweS3OgQWoIxfg8A2tkgQrWHou37yqlW6mJ9Jo5gd-0DMoJpNo2aMn3-OSLVJmU5Q2H3sxJofGHU1j2O7JD6NmzYZnthjRS8euY8FsKq6qT8Wka4OJ2-AJa9MnyTxYJkQcpX4g1YE-yhkFogTzhyhI-nzh2JBkLMO3VN9zqkaQbCeHjGFqNpY1t4btzgQ96qqNDe6W5eegJTF-vXUl-EC7ZKcnpHdygj6cBDPf_eC8n9fdNHbXUE7vStLs6NrKdM4XS8.NPkdCyhGFfuNu5pEESMx2VTAwl6QT1v5c6aNPcz3TKjr422-uJQroBO9tqAM2p9sKmhiWeFvXyB1z1SBRUlSXFh_hmqE24fHm4nrKP4ruq_Bs7QA4gqMKWd6faGHSmBZ2QpfG0Lsva0fNGFkmE-dNe7l2c2szY0E6Sr4RiniuFnaDNe4-RlE-aiL5OytchszYlgik9BR6F1ep_xLuZIyoLqf1wksBBDPr8nvQI4x-GvURghzl6EQv-6zuxuTKDD7cKFg40FOlMah-1ILGxQa68VpLslKIg_txsfuuWE4ZYr4f9NQ5O5dKDtqPVasEA9EIfdotY14cx7VbOFea7QswQ";

    public Object getUGCForBusStop(DescribeBusStopActivity callback, String stopId) {
        this.callback = callback;
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
            Log.w(TAG, "" + obj);

            List data = (List) obj.get("data");
            for(Object o : data) {
                Map<String, Object> mapObject = (Map<String, Object>) o;
                Map<String, Object> scbeObject = (Map<String, Object>) mapObject.get("object");

                String busStopId = (String) scbeObject.get("stopId");
                Boolean hasBench = (Boolean) scbeObject.get("hasBench");
                Boolean hasShelter = (Boolean) scbeObject.get("hasShelter");
                List<String> nearbyObjectList = (List<String>) scbeObject.get("nearbyObjects");
                HereTransitAPI.updateBusStopCache(busStopId, hasBench, hasShelter, nearbyObjectList);

                callback.drawDescriptionTable(HereTransitAPI.getCachedBusStop(busStopId));
                break;//execute for only one object
            }


        }

    }
}
