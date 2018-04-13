package kr.ac.yjc.wdj.hikonnect.activities;

/**
 * @file        kr.ac.yjc.wdj.hikonnect.activities.MapsActivity1.java
 * @author      Sungeun Kang (kasueu0814@gmail.com), Beomsu Kwon (rnjs9957@gmail.com)
 * @since       2018-03-26
 * @brief       The Activity used while hiking
 * @see         kr.ac.yjc.wdj.hikonnect.models.HikingPlan
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.apis.wifi_p2p.WifiP2pBroadCastReceiver;
import kr.ac.yjc.wdj.hikonnect.activities.LocationService;
import kr.ac.yjc.wdj.hikonnect.activities.PermissionManager;
import kr.ac.yjc.wdj.hikonnect.apis.http_request.HttpRequestConnection;
import kr.ac.yjc.wdj.hikonnect.models.HikingPlan;
import kr.ac.yjc.wdj.hikonnect.models.Conf;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class MapsActivity1 extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = "MapsActivity1";

    private GoogleMap mMap;

    private UiSettings mUiSettings;

    // 와이 파이 다이렉트를 위한 변수
    private WifiP2pManager mManager;

    private Channel mChannel;

    private IntentFilter mIntentFilter;

    private BroadcastReceiver mReceiver;

    // 권한 설정을 위한 변수
    private PermissionManager pManager;

    private LocationService lService;

    private static final String DATA_SERVER_URL = "http://hikonnect.ga:3000/paths/438001301/1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 퍼미션 관리자 생성
        pManager        = new PermissionManager(this);
        // 퍼미션 검사 수행
        Map<String, Integer> checkResult = pManager.checkPermissions();
        // 퍼미션 권한 요청
        pManager.requestPermissions();

        // 와이파이 다이렉트 사용을 위한 변수들.
        mManager        = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel        = mManager.initialize(this, getMainLooper(), null);

        // 와이파이 다이렉트를 위한 IntentFilter 초기화
        mIntentFilter   = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        // 와이파이 다이렉트 Broadcast Receiver 등록.
        mReceiver       = new WifiP2pBroadCastReceiver(mManager, mChannel, this);

        // GPS 위치 관리자 등록.
        lService = new LocationService(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionManager.PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);
                } else {
                    Toast.makeText(this, "권한을 등록해주세요", Toast.LENGTH_SHORT).show();
                    pManager.requestPermissions();
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mReceiver, mIntentFilter);

        // 와이파이 다이렉트 연결을 위해 연결 가능한 디바이스들을 검색.
//        mWDController.discoverPeers(mChannel);

        lService.sendMyLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 와이파이 다이렉트 Broadcast Receiver 등록 해제.
        unregisterReceiver(mReceiver);
    }

    /**
     * @param googleMap     The object of GoogleMap which is ready.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // init this.mMap
        mMap = googleMap;

        mUiSettings = mMap.getUiSettings();


        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        /* String url = Conf.HTTP_ADDR + "/paths/438001301/1";

        HikingPlan.NetworkTask networkTask = new HikingPlan.NetworkTask(url, null, mMap);
        networkTask.execute();

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String url = "http://hikonnect.ga/api";
                String url = "http://10.0.2.2:8000/api/hi";
                Log.d("POST", HttpRequestConnection.postRequest(url, null));
                return null;
            }
        };
        task.execute(); */
    }
}
