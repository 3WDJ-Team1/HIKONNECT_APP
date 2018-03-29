package kr.ac.yjc.wdj.myapplication.models;

/**
 * @file        kr.ac.yjc.wdj.myapplication.models.HikingPlan
 * @author      Beomsu Kwon (rnjs9957@gmail.com), Sungeun Kang (kasueu0814@gmail.com)
 * @since       2018-03-26
 * @brief       (add plz)
 * @todo        fix any problems
 */

import android.content.ContentValues;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import kr.ac.yjc.wdj.myapplication.HttpRequestConnection;

public class HikingPlan {

    public HttpRequestConnection httpReqConn = null;

    public HikingPlan() {
        this.httpReqConn = new HttpRequestConnection();
    }

    public static class NetworkTask extends AsyncTask<Void, Void, String> {
        private final String TAG = "NetworkTask";

        private String          url;
        private ContentValues   values;
        private GoogleMap       map;

        public NetworkTask(String url, ContentValues values) {
            this.url    = url;
            this.values = values;
        }
        public NetworkTask(String url, ContentValues values, GoogleMap map) {
            this.url    = url;
            this.values = values;
            this.map    = map;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            HttpRequestConnection httpRequestConnection = new HttpRequestConnection();
            result = httpRequestConnection.request(url, values);

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d(TAG, s);
            if (map != null) {
                setPolyLineOnGoogleMap(s);
            }
        }

        /**
         * @brief       draw polyline on google map
         */
        private void setPolyLineOnGoogleMap(String str) {
            // create PolyLineOptions Object
            PolylineOptions polylineOptions = new PolylineOptions().geodesic(true).color(Color.RED);

            // parsing response message
            try {
                /*
                 * message example
                 * {
                 *      "attributes":{
                 *                      "FID":1,
                 *                      "PMNTN_SN":32657,
                 *                      "MNTN_CODE":"438001301",
                 *                      "MNTN_NM":"소백산_비로봉",
                 *                      "PMNTN_NM":"성금리구간",
                 *                      "PMNTN_MAIN":" ",
                 *                      "PMNTN_LT":16.36,
                 *                      "PMNTN_DFFL":"쉬움",
                 *                      "PMNTN_UPPL":289,
                 *                      "PMNTN_GODN":202,
                 *                      "PMNTN_MTRQ":" ",
                 *                      "PMNTN_CNRL":" ",
                 *                      "PMNTN_CLS_":" ",
                 *                      "PMNTN_RISK":" ",
                 *                      "PMNTN_RECO":" ",
                 *                      "DATA_STDR_":"2016-12-31",
                 *                      "MNTN_ID":"438001301"
                 *                  },
                 *      "geometry"  :{
                 *                      "paths":[[
                 *                                  {"lat":36.84888867687226,"lng":128.4491423520132},
                 *                                  {"lat":36.84935719537144,"lng":128.44878921350082}
                 *                              ]]
                 *                  }
                 * }
                 */
                // get whole JSON object
                JSONObject  jsonObject          = new JSONObject(str);
                // get geometry object inside of jsonObject
                JSONObject  jsonGeoObject       = jsonObject.getJSONObject("geometry");
                // get Array of paths
                JSONArray   jsonPathOuterArray  = jsonGeoObject.getJSONArray("paths");
                JSONArray   jsonPathArray       = jsonPathOuterArray.getJSONArray(0);

                // get all lat, lng and make LatLng objects, then add it in polylineOptions
                for (int j = 0 ; j < jsonPathArray.length() ; j++) {
                    JSONObject  jsonPathObject  = jsonPathArray.getJSONObject(j);
                    Double      lat             = jsonPathObject.getDouble("lat");
                    Double      lng             = jsonPathObject.getDouble("lng");
                    LatLng      latLng          = new LatLng(lat, lng);
                    polylineOptions.add(latLng);
                }
            } catch (JSONException jsonE) {
                jsonE.printStackTrace();
            }
            // add polyline in map, with polylineOptions
            map.addPolyline(polylineOptions);
        }
    }
}
