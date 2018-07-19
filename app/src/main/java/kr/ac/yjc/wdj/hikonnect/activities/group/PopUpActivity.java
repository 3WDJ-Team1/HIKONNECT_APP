package kr.ac.yjc.wdj.hikonnect.activities.group;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by LEE AREUM on 2018-06-18.
 */

public class PopUpActivity extends FragmentActivity implements OnMapReadyCallback {
    // UI 변수
    private Button                  mapOkBtn;       // 확인

    // 데이터 담을 변수
    private ArrayList<Integer>      fidList;        // 경로(FID)
    private ArrayList<LatLng>       routeList;      // 경로 좌표
    private int                     mntId;

    // 구글 맵 관련
    private GoogleMap               GoogleMap;          // 지도
    private MapFragment             mapFragment;
    private PolylineOptions         polylineOptions;    // 지도에 찍어 낼 PolyLineOption

    private static final String LOG_TAG         = "PopUpAcitivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.map_popup);

        this.initialize();

        // 변수 초기화
        // UI 변수
        mapOkBtn    = (Button) findViewById(R.id.mapOkBtn);

        // 데이터 변수
        routeList       = new ArrayList<>();

        // Google Maps 관련 변수
        polylineOptions = new PolylineOptions();

        // 확인 버튼 리스너
        touchedMapOkBtn();

        //데이터 가져오기
        Intent intent       = getIntent();
        String mntName             = intent.getStringExtra("mntName");

        getMntIdFromMntName(mntName);
    }

    public void initialize() {
        this.mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mountRouteFragment);
        this.mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.GoogleMap = googleMap;
    }

    /*private void setData() {
        *//**
         * 데이터 초기화
         *//*
        Intent intent   = getIntent();

        // FID 리스트 초기화
        try {
            // 받아온 String을 JSON 객체로 변환
            JSONArray jArray    = new JSONArray(intent.getStringExtra("scheduleRoute"));
            // fidList 초기화
            fidList = new ArrayList<>();

            // 값 초기화
            for (int i = 0 ; i < jArray.length() ; i++) {
                fidList.add(jArray.getInt( i));
            }

        } catch (JSONException je) {

            Log.e(LOG_TAG, je.getMessage());
        }
    }
*/
    // 확인 버튼 리스너
    private void touchedMapOkBtn() {
        mapOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 데이터 전달하기
                //Intent intent= new Intent();

                // Popup 닫기
                finish();
            }
        });
    }

    // 해당 산의 이름으로 산 코드 검색
    private void getMntIdFromMntName(final String mntName) {
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {

                try {

                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url(Environments.LARAVEL_HIKONNECT_IP + "/api/searchMount/" + mntName)
                            .build();

                    Response response = client.newCall(request).execute();

                    return response.body().string();

                } catch (IOException ie) {
                    Log.e(LOG_TAG, ie.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                Log.d(LOG_TAG, s);
                try {
                    JSONArray jsonArray = new JSONArray(s);

                    for (int i = 0 ; i < jsonArray.length() ; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        mntId = jsonObject.getInt("mnt_id");

                        Log.d("산코드는 " , Integer.toString(mntId));
                    }

                    drawPolylineFromMountRoute(mntId);

                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }.execute();
    }

    // 산코드로 DB에서 검색하여 해당 산 경로 배열 받아오기
    // 산 경로 배열을 토대로 지도에 찍기
    private void drawPolylineFromMountRoute(final int mntId) {
        Log.d("전달받은 산코드는 " , Integer.toString(mntId));
        // 산코드로 DB에서 검색하여 해당 산 경로 배열 받아오기
        new AsyncTask<Void, Integer, String>() {

            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();

                try {
                    Request request = new Request.Builder()
                            .url(Environments.NODE_HIKONNECT_IP + "/paths/" + mntId)
                            .build();

                    Response response = client.newCall(request).execute();

                    return response.body().string();

                } catch (IOException ie) {
                    Log.e(LOG_TAG, ie.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONArray routes = new JSONArray(s);

                    Log.d("배열 길이", Integer.toString(routes.length()));

                    for (int i = 0 ; i < routes.length() ; i++) {

                        JSONObject jsonObject   = routes.getJSONObject(i);
                        JSONObject infoObj      = jsonObject.getJSONObject("attributes");

                        /*if (!fidList.contains(infoObj.getInt("FID"))) {
                            continue;
                        }*/

                        JSONObject  geometryObj = jsonObject.getJSONObject("geometry");
                        JSONArray   tempsArr    = geometryObj.getJSONArray("paths");
                        JSONArray   pathsArr    = tempsArr.getJSONArray(0);

                        routeList       = new ArrayList<>();
                        polylineOptions = new PolylineOptions();

                        Log.d("tempsArr", Integer.toString(tempsArr.length()));
                        Log.d("pathsArr", Integer.toString(pathsArr.length()));

                        for (int j = 0; j < tempsArr.length(); j++) {

                            JSONObject pathObj = tempsArr.getJSONObject(j);

                            double lat = pathObj.getDouble("lat");
                            double lng = pathObj.getDouble("lng");

                            Log.d("lat", Double.toString(lat));
                            Log.d("lng", Double.toString(lng));

                            // 폴리라인 옵션에 지점 추가
                            LatLng latLng = new LatLng(lat, lng);

                            if (infoObj.getInt("FID") == 0 && j == 0) {
                                GoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                            }
                            GoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                            routeList.add(latLng);
                        }
                        polylineOptions.addAll(routeList);
                        polylineOptions.color(Color.YELLOW);
                        polylineOptions.width(13);
                        GoogleMap.addPolyline(polylineOptions);
                    }

                } catch (JSONException je) {
                    Log.e(LOG_TAG, je.getMessage());
                }
            }
        }.execute();
    }
}
