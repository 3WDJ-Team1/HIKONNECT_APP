package kr.ac.yjc.wdj.hikonnect.activities;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.apis.LocationService;
import okhttp3.OkHttpClient;

public class MapsActivityTemp extends FragmentActivity implements
        OnMapReadyCallback,
        OnMapClickListener,
        LocationListener,
        View.OnClickListener {

    private final String TAG = "HIKONNECT";

    // 지도, 위치.
    private GoogleMap           gMap;
    private Location            myCurrentLocation;
    private SupportMapFragment  mapFragment;

    private LocationService     locationService;

    // HTTP 통신.
    private OkHttpClient        okHttpClient = new OkHttpClient();

    // UI
    // [1] 상태 표시 레이아웃.
    private TextView            txtViewAltitude;
    private TextView            txtViewAvgSpeed;
    private TextView            txtViewRank;

    // [2] 버튼.
    // [2.1] 위치 메모.
    private FloatingActionMenu      fabMenuLocationMemoMenu;
    private FloatingActionButton    fabWriteLocationMemoPicture;
    private FloatingActionButton    fabWriteLocationMemo;

    // [2.2] 자기 위치 갱신 버튼.
    private FloatingActionButton    fabUpdateMyLocation;

    // [2.3] 그룹 맴버 리스트 버튼
    private FloatingActionButton    fabShowMemberList;

    // [2.4] 자기 정보 조회 버튼
    private FloatingActionButton    fabShowMyInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // [1] GoogleMaps Fragment 불러오기.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // [2] 위치 데이터를
        locationService = new LocationService(this);
        locationService.setLocationListener(this);

        initializeUI();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onLocationChanged(Location location) {
        myCurrentLocation = location;

        Toast.makeText(this, location.getSpeed() + "M/S", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Speed: " + location.getSpeed() + "M/S");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

        }
    }

    private void initializeUI() {
        // Layout 초기화.
        // [1] 상태 표시 레이아웃.
//        txtViewAltitude         = (TextView) findViewById(R.id.altitude_txtview);
//        txtViewAvgSpeed         = (TextView) findViewById(R.id.avg_speed_txtview);
//        txtViewRank             = (TextView) findViewById(R.id.rank_txtview);

        // [2] 버튼.
        // [2.1] 위치 메모.
        fabMenuLocationMemoMenu         = (FloatingActionMenu) findViewById(R.id.write_l_memo_fabmenu);
        fabWriteLocationMemoPicture     = (FloatingActionButton) findViewById(R.id.l_memo_with_pic_fabbtn);
        fabWriteLocationMemo            = (FloatingActionButton) findViewById(R.id.l_memo_without_pic_fabbtn);

        // [2.2] 자기 위치 갱신 버튼.
        fabUpdateMyLocation             = (FloatingActionButton) findViewById(R.id.update_loc_btn);

        // [2.3] 그룹 맴버 리스트 버튼
        fabShowMemberList               = (FloatingActionButton) findViewById(R.id.show_member_list_btn);

        // [2.4] 자기 정보 조회 버튼
//        fabShowMyInfo                   = (FloatingActionButton) findViewById(R.id.show_my_info_btn);

        // 이벤트 리스너 등록
        // [1] 자기 위치 갱신 버튼
        fabMenuLocationMemoMenu.setOnClickListener(this);
        // [2] 그룹 맴버 리스트 버튼
        fabUpdateMyLocation.setOnClickListener(this);
        // [3] 자기 정보 조회 버튼
        fabShowMemberList.setOnClickListener(this);
    }
}
