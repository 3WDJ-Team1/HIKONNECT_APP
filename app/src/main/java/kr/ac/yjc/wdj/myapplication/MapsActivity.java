package kr.ac.yjc.wdj.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

import kr.ac.yjc.wdj.myapplication.APIs.WifiP2p.WifiDirectBroadCastReceiver;
import kr.ac.yjc.wdj.myapplication.APIs.WifiP2p.WifiP2pController;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = "MapsActivity";

    private GoogleMap mMap;

    private UiSettings mUiSettings;

    // 와이 파이 다이렉트를 위한 변수
    private WifiP2pManager mManager;

    private WifiP2pController mWDController;

    private Channel mChannel;

    private IntentFilter mIntentFilter;

    private BroadcastReceiver mReceiver;

    // 권한 설정을 위한 변수
    private PermissionManager pManager;

    private static final String DATA_SERVER_URL = "http://hikonnect.ga:3000/paths/438001301/1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // 퍼미션 관리자 생성
        pManager        = new PermissionManager(this);
        // 퍼미션 검사 수행
        Map<String, Integer> checkResult = pManager.checkPermissions();
        // 퍼미션 권한 요청
        pManager.requestPermissions();

        // 와이파이 다이렉트 사용을 위한 변수들.
        mManager        = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mWDController   = new WifiP2pController(mManager);
        mChannel        = mManager.initialize(this, getMainLooper(), null);

        // 와이파이 다이렉트를 위한 IntentFilter 초기화
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        // 와이파이 다이렉트 Broadcast Receiver 등록.
        mReceiver       = new WifiDirectBroadCastReceiver(mManager, mChannel, this);


        // 데이터 서버에서 등산 경로를 받아오는 구문
        // HikingPlan.NetworkTask networkTask = new HikingPlan.NetworkTask(DATA_SERVER_URL, null);
        // networkTask.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        mWDController.discoverPeers(mChannel);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 와이파이 다이렉트 Broadcast Receiver 등록 해제.
        unregisterReceiver(mReceiver);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

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
    }
}
