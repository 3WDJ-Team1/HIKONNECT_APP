package kr.ac.yjc.wdj.hikonnect.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.support.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import kr.ac.yjc.wdj.hikonnect.AfterHikingActivity;
import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.HikingField;
import kr.ac.yjc.wdj.hikonnect.LocationMemo;
import kr.ac.yjc.wdj.hikonnect.LocationMemoActivity;
import kr.ac.yjc.wdj.hikonnect.MapItem;
import kr.ac.yjc.wdj.hikonnect.Member;
import kr.ac.yjc.wdj.hikonnect.Othersinfo;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.adapters.HikingMemberListAdapter;
import kr.ac.yjc.wdj.hikonnect.adapters.MyInfoWindowAdapter;
import kr.ac.yjc.wdj.hikonnect.apis.LocationService;
import kr.ac.yjc.wdj.hikonnect.apis.PermissionManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// walkietalkie
import kr.ac.yjc.wdj.hikonnect.apis.walkietalkie.*;

public class MapsActivityTemp extends FragmentActivity implements
        OnMapReadyCallback,
        OnMapClickListener,
        LocationListener {

    public static final String          TAG                 = "HIKONNECT";
    public static final int             REQUEST_INTERVAL_TIME = 2000;

    private final int                   GALLERY_CODE        = 1110;
    // private final int                   CAMERA_CODE         = 1111;
    private final int                   REQUEST_TAKE_PHOTO  = 1112;

    private int                         myMemberNo;

    public static Timer                 timer = new Timer();

    // 지도, 위치
    private GoogleMap                   gMap;
    private Location                    myCurrentLocation   =   null;
    public double                       hikedDistance = 0.0;

    // HTTP 통신
    private OkHttpClient                okHttpClient = new OkHttpClient();

    // 위치 메모
    private String                      mImageCaptureName;           //이미지 이름

    // 등산 데이터
    private ArrayList<HikingField>      hikingFields        = new ArrayList<>();
    private int                         myHikingState = 0;
    private int                         currentFID          = 0;
    private int                         currentPointInFID   = 0;
    private double                      wholeDistance       = 0.0;
    private double                      direction_set_lat1                 = 0.0;
    private double                      direction_set_lng1                 = 0.0;
    private double                      direction_set_lat2                 = 0.0;
    private double                      direction_set_lng2                 = 0.0;

    private ArrayList<LatLng>   toPaintRouteSet = new ArrayList<>();
    private JSONArray           scheduledFIDSet;
    private long                mntID;

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, MapItem>   mapItems = new HashMap<>();
    private ClusterManager<MapItem>     myClusterManager;
    private ArrayList<Marker>           markers = new ArrayList<>();
    private Marker                      myMarker;

    // UI 클래스
    // 현재 상태 표시
    // 위치 비활성화 알림 바
    private RelativeLayout      noticeBar;
    private TextView            mapNoticeBarMainText;
    private TextView            mapNoticeBarSubText;

    private CardView            userDataBox;            // 자신의 현재 정보를 보여줄 CardView.
    private TextView            tvUserSpeed;            // 현재 속도 TextView (값 -> km/h 기준).
    private TextView            tvDistance;             // 총 이동 거리 TextView (값 -> km 기준).
    private TextView            tvArriveWhen;           // 예상 도착 시간 TextView (값 -> 시간 기준).
    private TextView            tvUserRank;             // 등수 TextView (값 -> 등산 거리 기준).
    private ProgressBar         pgbarHikingProgress;    // 등산 진행도 ProgressBar (값 -> 현재 등산 거리 / 총 경로 거리).
    private TextView            tvHikingProgress;       // 등산 진행도 TextView.

    // 위치 메모.
    private FloatingActionButton    fabWriteLocationMemo;   // 위치 메모 작성 버튼.

    // 위치 메모 작성 팝업.
    private LinearLayout    linearLayoutLocationMemo;       // 위치 메모 작성 팝업 창 LinearLayout.
    private EditText        edtTextLMemoTitle;              // 위치 메모 제목 입력 칸 EditText.
    private EditText        edtTextLMemoContent;            // 위치 메모 내용 입력 칸 EditText.
    private ImageView       imgViewLMemoImg;                // 선택한 사진을 띄울 창 ImageView.
    private Button          btnLMemoSendReq;                // 위치 메모 작성 요청 전송 버튼.
    private Button          btnLMemoCancel;                 // 위치 메모 작성 취소 버튼.

    // 자기 위치 갱신 버튼.
    private FloatingActionButton    fabUpdateMyLocation;    // 위치 갱신 버튼.
    private boolean                 isMyLocationBtnPressed;

    // 그룹 맴버 리스트 버튼.
    private FloatingActionButton    fabShowMemberList;      // 그룹 맴버 리스트 버튼.

    // 무전 UI.
    private LinearLayout    drawerLayout;       // 무전 버튼을 넣어둘 레이아웃
    private Button          btnSendRadio;       // 무전 시작 버튼
//    private ImageButton     showRecordList;     // 음성 녹음 리스트.

    // 무전 기능 변수.
    private WalkieTalkie    walkieTalkie;   // 무전 객체
    private boolean         isSendingNow;   // 현재 무전을 전송중인지

    // 등산 상태 변경 버튼.
    private Button          btnChangeHikingState;   // 등산 시작, 등산 끝 버튼.

    // 상태 저장 변수.
    private int             hikingProgress          = 0;    // 현재 등산 상태 (0 = 등산 전, 1 = 등산 중, 2 = 등산 끝)

    private SharedPreferences   pref;                   // 유저 로그인 데이터를 참조 변수.

    private boolean     isRecBtnVisible         = false;    // 현재 녹음 버튼 상태
    private boolean     isRequestingLocation    = false;    // 위치 관리자 활성화 상태.

    private class MapItemRenderer extends DefaultClusterRenderer<MapItem> {

        static private final int BIT_MAP_SIZE = 150;
        private final IconGenerator mIconGenerator          = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator   = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        private Bitmap bitmap;

        MapItemRenderer() {
            super(getApplicationContext(), gMap, myClusterManager);

            @SuppressLint("InflateParams")
            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.marker_img);

            mImageView = new ImageView(getApplicationContext());
            mDimension = 5;
            int padding = 5;
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(MapItem item, MarkerOptions markerOptions) {

            if (item instanceof LocationMemo) {
                // Resource에서 Drawable을 불러옴.
                Drawable drawable = getResources().getDrawable(R.drawable.location_memo_icon_svg);
                // 변경된 이미지를 ImageView에 적용.
                mImageView.setImageDrawable(drawable);
                // ImgaeVie를 이용해 Icon 생성.
                Bitmap icon = mIconGenerator.makeIcon();
                // Marker에 Icon 등록.
                markerOptions
                        .icon(BitmapDescriptorFactory.fromBitmap(icon));
            }
            if (item instanceof Member) {
                // 자기 마커를 지도에서 숨기기.
                if (((Member) item).member_no == myMemberNo) {
                    markerOptions.visible(false);
                } else{
                    if (((Member) item).profileImg == null) {
                        // Resource에서 Drawable을 불러옴.
                        Drawable drawable = getResources().getDrawable(R.drawable.default_profile);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            drawable = (DrawableCompat.wrap(drawable)).mutate();
                        }
                        // Bitmap 포멧으로 변환.
                        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);

                        // 변경된 이미지를 ImageView에 적용.
                        mImageView.setImageBitmap(bitmap);
                        // ImgaeVie를 이용해 Icon 생성.
                        Bitmap icon = mIconGenerator.makeIcon();
                        // Marker에 Icon 등록.
                        markerOptions
                                .icon(BitmapDescriptorFactory.fromBitmap(icon));
                    } else  {
                        // Member객체에서 ProfileImage를 Bitmap 포멧으로 불러옴.
                        Bitmap profileImg = ((Member) item).profileImg;
                        // 이미지 크기를 변경.
                        profileImg = Bitmap.createScaledBitmap(profileImg, BIT_MAP_SIZE, BIT_MAP_SIZE, true);
                        // 변경된 이미지를 ImageView에 적용
                        mImageView.setImageBitmap(profileImg);
                        // ImgaeVie를 이용해 Icon 생성.
                        Bitmap icon = mIconGenerator.makeIcon();
                        // Marker에 Icon 등록.
                        markerOptions
                                .icon(BitmapDescriptorFactory.fromBitmap(icon));
                    }
                }
            }
        }

        @Override
        protected void onClusterItemRendered(MapItem clusterItem, Marker marker) {
            // Marker에 ClusterItem을 Tag로 설정.
            // 설정된 Tag는 marker.getTag를 통해 불러올 수 있다.
            marker.setTag(clusterItem);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timer = new Timer();

        // 사용자 로그인 데이터 저장공간 불러오기.
        pref = getSharedPreferences("loginData", MODE_PRIVATE);
        String userid = pref.getString("user_id", "null");
        getMemberNoByUserID(userid);

        // 지도 사요에 필요한 Permission 요청.
        PermissionManager permissionManager = new PermissionManager(this);
        permissionManager.requestPermissions();

        // Activity에 레이아웃 등록.
        setContentView(R.layout.activity_maps_temp);

        // [1] GoogleMaps Fragment 불러오기.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        // GoogleMap 객체 요청.
        mapFragment.getMapAsync(this);

        // [2] 위치 서비스 클래스 초기화.
        LocationService locationService = new LocationService(this);
        locationService.setLocationListener(this);

        // [3] HTTP 클래스 초기화.
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        okHttpClient = builder.build();

        // [4] 무전 서비스 클래스 초기화
        isSendingNow = false;

        // 무전 객체 초기화
//        walkieTalkie = new WalkieTalkie(getSharedPreferences("loginData", MODE_PRIVATE));
        // 무전 받아오기 시작
//        walkieTalkie.receiveStart();

        initializeUI();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;       // 시스템으로 부터 반환 된 GoogleMap 객체.

        UiSettings mapUiSettings = gMap.getUiSettings();
        // GoogleMap Default 툴바 비활성화.
        mapUiSettings.setMapToolbarEnabled(false);

        // GoogleMap OnclickListener 등록.
        googleMap.setOnMapClickListener(this);

        // GoogleMap ClusterManager 초기화
        myClusterManager = new ClusterManager<>(this, gMap);
        // Custom Renderer 등록
        myClusterManager.setRenderer(new MapItemRenderer());

        gMap.setOnMarkerClickListener(myClusterManager);
        gMap.setOnMapClickListener(this);
        gMap.setOnCameraIdleListener(myClusterManager);

        gMap.setInfoWindowAdapter(new MyInfoWindowAdapter(this, gMap, this));

        myClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MapItem>() {
            @Override
            public boolean onClusterClick(Cluster<MapItem> cluster) {

                ArrayList<Integer> memberIDs    = new ArrayList<>();
                ArrayList<Integer> lMemoIDs     = new ArrayList<>();

                for (MapItem mapItem : cluster.getItems()) {
                    if (mapItem instanceof Member) {
                        memberIDs.add(((Member) mapItem).member_no);
                    } else if (mapItem instanceof LocationMemo) {
                        lMemoIDs.add(((LocationMemo) mapItem).no);
                    }
                }

                Intent intent = new Intent(MapsActivityTemp.this, ClusterDetailActivity.class);
                intent.putExtra("key", ClusterDetailActivity.CLUSTER_CLICKED);
                intent.putIntegerArrayListExtra("memberIDs", memberIDs);
                intent.putIntegerArrayListExtra("lMemoIDs", lMemoIDs);
                startActivity(intent);


                return false;
            }
        });
        // Cluster Item OnClickLister 등록.
        myClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MapItem>() {
            @Override
            public boolean onClusterItemClick(final MapItem mapItem) {
                if (mapItem instanceof LocationMemo) { // Location Memo Marker

                    // 위치 메모 인텐트 생성.
                    Intent locationMemoIntent = new Intent(MapsActivityTemp.this, LocationMemoActivity.class);

                    // 인텐트에 값 입력.
                    locationMemoIntent.putExtra("location_no", ((LocationMemo) mapItem).no);    // 위치 메모의 ID값.
                    locationMemoIntent.putExtra("latitude", mapItem.getPosition().latitude);    // 위치 메모 위도.
                    locationMemoIntent.putExtra("longitude", mapItem.getPosition().longitude);  // 위치 메모 경도.

                    // 액티비티를 시작.
                    startActivity(locationMemoIntent);

                }

                return false;
            }
        });

        myMarker = gMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)/*R.drawable.baseline_fiber_manual_record_black_18dp*/)
                .alpha(0.8f)
                .zIndex(1.0f));

        requestHikingRoute();

        timer.schedule(mainTimerTask, 0, REQUEST_INTERVAL_TIME);
    }

    private TimerTask mainTimerTask = new TimerTask() {
        @Override
        public void run() {

            updateHikingInfo();
            updateCurrentFID();
            paintMarkers();
            updateHikedDistance();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    LatLng latLng;

                    if (myCurrentLocation == null) {
                        latLng = new LatLng(133,33);
                    }
                    else
                        latLng = new LatLng(myCurrentLocation.getLatitude(),myCurrentLocation.getLongitude());
                    short bear = locbearing(direction_set_lat1,direction_set_lng1,direction_set_lat2,direction_set_lng2);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)
                            .bearing(bear)
                            .zoom(19)
                            .build();
                    gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    try {
                        switch (myHikingState) {
                            case 0:
                                if (getDisToStartPoint() < 100) {
                                    btnChangeHikingState.setVisibility(View.VISIBLE);
                                } else {
                                    btnChangeHikingState.setVisibility(View.GONE);
                                }
                                break;
                            case 1:
                                if (getDisToStartPoint() < 100 && pgbarHikingProgress.getProgress()> 90) {
                                    btnChangeHikingState.setVisibility(View.VISIBLE);
                                } else {
                                    btnChangeHikingState.setVisibility(View.GONE);
                                }
                                break;
                            case 2:
                                btnChangeHikingState.setVisibility(View.VISIBLE);
                                break;
                        }

                        if (myHikingState != 0) {
                            tvDistance.setText(String.valueOf(hikedDistance));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "mainTimerTask: ", e);
                    }
                }
            });
        }
    };

    @Override
    public void onMapClick(LatLng latLng) {
        if (userDataBox.getVisibility() == View.VISIBLE || myHikingState == 0) {
            userDataBox.setVisibility(View.GONE);
        } else {
            requestMyHikingInfo();
            userDataBox.setVisibility(View.VISIBLE);
        }

        // 마커 인포 윈도우 숨기기.
        for (Marker marker : markers) {
            if (marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
            }
        }

        // 내 위치 추적 비활성화.
        isMyLocationBtnPressed = false;
        fabUpdateMyLocation.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.my_position_btn_svg));
    }

    private void requestMyHikingInfo() {
        new Thread(new Runnable() {
            int rank;
            @Override
            public void run() {
                try {

                    HttpUrl httpUrl = Objects.requireNonNull(HttpUrl
                            .parse(Environments.LARAVEL_HIKONNECT_IP + "/api/getMemberDetail"))
                            .newBuilder()
                            .build();

                    RequestBody reqBody = new FormBody.Builder()
                            .add("member_no", String.valueOf(myMemberNo))   // 사용자의 등산 맴버 ID.
                            .build();

                    Request req = new Request.Builder()
                            .url(httpUrl)
                            .post(reqBody)
                            .build();

                    Response response = okHttpClient
                            .newCall(req)
                            .execute();

                    JSONParser parser = new JSONParser();

                    JSONObject result = (JSONObject) ((JSONArray) parser.parse(response.body().string())).get(0);

                    rank        = Integer.valueOf(result.get("rank").toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvUserRank.setText(String.valueOf(rank));
                        }
                    });

                    // 사용자 정보 CardView가 보여질 때 지속적으로 값을 갱신.
                    if (userDataBox.getVisibility() == View.VISIBLE) {
                        Thread.sleep(REQUEST_INTERVAL_TIME);
                        requestMyHikingInfo();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Get my hiking info: ", e);
                }
            }
        }).start();
    }

    private void requestHikingRoute() throws NullPointerException {
        Intent mainActivityIntent = getIntent();
        final String scheduleNum = mainActivityIntent.getStringExtra("schedule_no");

        HttpUrl reqUrl = HttpUrl
                .parse(Environments.LARAVEL_HIKONNECT_IP + "/api/getNowScheduleDetail")
                .newBuilder()
                .build();

        RequestBody reqBody = new FormBody.Builder()
                .add("schedule_no", scheduleNum)
                .build();

        Request req = new Request.Builder()
                .url(reqUrl)
                .post(reqBody)
                .build();

        okHttpClient.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Http updateHikingInfo: ", e);
                setNoticeBar(View.VISIBLE, "연결 상태 불량.", "네트워크 상태를 확인해주세요.");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (noticeBar.getVisibility() == View.VISIBLE) {
                    setNoticeBar(View.GONE, null, null);
                }
                String result = response.body().string();

                try {

                    JSONParser jsonParser = new JSONParser();

                    JSONObject jsonObject = (JSONObject) ((JSONArray) jsonParser.parse(result)).get(0);

                    // 그룹 리더가 계획한 FID 리스트.
                    // Ex) [32, 10, 23, ...]
                    scheduledFIDSet      = (JSONArray) jsonParser.parse((String) jsonObject.get("route"));
                    // 그룹 리더가 계획한 산 이름.
                    // Ex) 소백산.
                    mntID                  = (Long) jsonObject.get("mnt_id");

                    HttpUrl reqUrl = HttpUrl
                            .parse(Environments.NODE_HIKONNECT_IP + "/paths/" + mntID)
                            .newBuilder()
                            .build();

                    Request req = new Request.Builder()
                            .url(reqUrl)
                            .build();

                    okHttpClient.newCall(req).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.e(TAG, "Http updateHikingInfo: ", e);
                            setNoticeBar(View.VISIBLE, "연결 상태 불량.", "네트워크 상태를 확인해주세요.");
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            try {
                                JSONParser parser = new JSONParser();
                                String serverRes = response.body().string();

                                JSONArray routeSet = (JSONArray) parser.parse(serverRes);
                                wholeDistance = 0.0;

                                boolean isCrntFIDForward = true;
                                boolean isNextFIDForward = true;

                                // 등산 경로 정방향, 역방향 판별 알고리즘.
                                // [1] FID1과 FID2를 가져온다.
                                // [2] FID1의 first와 last를 가져온다.
                                // [3] FID2의 first와 last를 가져온다.
                                // [4] 총 경우의 수는 4가지
                                // [4-1] f1-first = f2-first.     => FID1는 역방향. FID2는 정방향.
                                // [4-2] f1-last  = f2-first.     => FID1는 정방향. FID2는 정방향.
                                // [4-3] f1-first = f2-last.      => FID1는 역방향. FID2는 역방향.
                                // [4-4] f1-last  = f2-last.      => FID1는 정방향. FID2는 역방향.
                                for (Object scheduleRoute : scheduledFIDSet) {
                                    int locatedFID = scheduledFIDSet.indexOf(scheduleRoute);

                                    JSONObject routes = (JSONObject) routeSet.get(Integer.valueOf(scheduleRoute.toString()));
                                    JSONObject attributes = (JSONObject) routes.get("attributes");
                                    JSONArray paths = (JSONArray) ((JSONArray) ((JSONObject) routes.get("geometry")).get("paths")).get(0);

                                    int fid             = Integer.valueOf(attributes.get("FID").toString());
                                    double fieldLength  = Double.valueOf(attributes.get("PMNTN_LT").toString());

                                    wholeDistance += fieldLength;

                                    HikingField hikingField = new HikingField(fid, fieldLength);

                                    // 일정의 마지막 Field(경로)이면.
                                    if (locatedFID >= scheduledFIDSet.size() - 1) {
                                        // 결과가 정방향이면.
                                        if (isNextFIDForward) {
                                            // 현재 FID를 정방향으로 입력.
                                            for (Object _idx : paths) {
                                                double _lat = Double.valueOf(((JSONObject) _idx).get("lat").toString());
                                                double _lng = Double.valueOf(((JSONObject) _idx).get("lng").toString());

                                                hikingField.addLatLng(new LatLng(_lat, _lng));

                                                if (toPaintRouteSet.size() > 0) {
                                                    if (toPaintRouteSet.get(toPaintRouteSet.size() - 1).equals(new LatLng(_lat, _lng))) {
                                                        continue;
                                                    }
                                                }
                                                toPaintRouteSet.add(new LatLng(_lat, _lng));
                                            }
                                        } else {
                                            // 현재 FID를 역방향으로 입력.
                                            for (int _i = paths.size() - 1; _i >= 0; _i--) {
                                                Object _idx = paths.get(_i);

                                                double _lat = Double.valueOf(((JSONObject) _idx).get("lat").toString());
                                                double _lng = Double.valueOf(((JSONObject) _idx).get("lng").toString());

                                                hikingField.addLatLng(new LatLng(_lat, _lng));

                                                if (toPaintRouteSet.size() > 0) {
                                                    if (toPaintRouteSet.get(toPaintRouteSet.size() - 1).equals(new LatLng(_lat, _lng))) {
                                                        continue;
                                                    }
                                                }
                                                toPaintRouteSet.add(new LatLng(_lat, _lng));
                                            }
                                        }
                                    } else {
                                        int nextFID             = Integer.valueOf(scheduledFIDSet.get(locatedFID + 1).toString());
                                        JSONObject nextRoute    = (JSONObject) routeSet.get(nextFID);
                                        JSONArray nextPaths     = (JSONArray) ((JSONArray) ((JSONObject) nextRoute.get("geometry")).get("paths")).get(0);

                                        if (paths.get(0).equals(nextPaths.get(0))) {
                                            // 현재 FID의 처음과 다음 FID의 처음이 같을 때.
                                            // 현재 FID는 역방향.
                                            isCrntFIDForward = false;
                                            // 다음 FID는 정방향.
                                            isNextFIDForward = true;
                                        } else if (paths.get(paths.size() - 1).equals(nextPaths.get(0))) {
                                            // 현재 FID의 마지막과 다음 FID의 처음이 같을 때.
                                            // 현재 FID는 정방향.
                                            isCrntFIDForward = true;
                                            // 다음 FID는 정방향.
                                            isNextFIDForward = true;
                                        } else if (paths.get(0).equals(nextPaths.get(nextPaths.size() - 1))) {
                                            // 현재 FID의 처음과 다음 FID의 마지막이 같을 때.
                                            // 현재 FID는 역방향.
                                            isCrntFIDForward = false;
                                            // 다음 FID는 역방향.
                                            isNextFIDForward = false;
                                        } else if (paths.get(paths.size() - 1).equals(nextPaths.get(nextPaths.size() - 1))) {
                                            // 현재 FID의 마지막과 다음 FID의 마지막이 같을 때.
                                            // 현재 FID는 정방향.
                                            isCrntFIDForward = true;
                                            // 다음 FID는 역방향.
                                            isNextFIDForward = false;
                                        }

                                        // 현재 FID를 정방향으로 입력.
                                        if (isCrntFIDForward) {
                                            for (Object _idx : paths) {
                                                double _lat = Double.valueOf(((JSONObject) _idx).get("lat").toString());
                                                double _lng = Double.valueOf(((JSONObject) _idx).get("lng").toString());

                                                hikingField.addLatLng(new LatLng(_lat, _lng));

                                                if (toPaintRouteSet.size() > 0) {
                                                    if (toPaintRouteSet.get(toPaintRouteSet.size() - 1).equals(new LatLng(_lat, _lng))) {
                                                        continue;
                                                    }
                                                }
                                                toPaintRouteSet.add(new LatLng(_lat, _lng));
                                            }
                                        } else {
                                        // 현재 FID를 역방향으로 입력.
                                            for (int _i = paths.size() - 1; _i >= 0; _i--) {
                                                Object _idx = paths.get(_i);

                                                double _lat = Double.valueOf(((JSONObject) _idx).get("lat").toString());
                                                double _lng = Double.valueOf(((JSONObject) _idx).get("lng").toString());

                                                hikingField.addLatLng(new LatLng(_lat, _lng));

                                                if (toPaintRouteSet.size() > 0) {
                                                    if (toPaintRouteSet.get(toPaintRouteSet.size() - 1).equals(new LatLng(_lat, _lng))) {
                                                        continue;
                                                    }
                                                }
                                                toPaintRouteSet.add(new LatLng(_lat, _lng));
                                            }
                                        }

                                        hikingFields.add(hikingField);
                                    }
                                }

                                paintHikingRoute();
                            } catch (NullPointerException npe) {
                                Log.e(TAG, "onResponse: ", npe);
                            } catch (ParseException pse) {
                                Log.e(TAG, "Parse Server Res: ", pse);
                            }
                        }
                    }); // okHttp enqueue() end.

                } catch (Exception e) {
                    Log.e(TAG, "onResponse: ", e);
                }
            }
        });
    }

    private void getMemberNoByUserID(String userID){
            String scheduleNum = getIntent().getStringExtra("schedule_no");

            HttpUrl reqUrl = Objects.requireNonNull(HttpUrl
                    .parse(Environments.LARAVEL_HIKONNECT_IP + "/api/getMemberNoByUserId"))
                    .newBuilder().build();

            RequestBody requestBody = new FormBody.Builder()
                    .add("user_id", userID)
                    .add("schedule_no", scheduleNum)
                    .build();

            Request req = new Request.Builder().url(reqUrl).post(requestBody).build();

            okHttpClient.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "onFailure: ", e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {

                        JSONParser parser = new JSONParser();

                        JSONArray result = (JSONArray) parser.parse(response.body().string());

                        int memNo = Integer.valueOf(((JSONObject) result.get(0)).get("member_no").toString());
                        int hikingState = Integer.valueOf(((JSONObject) result.get(0)).get("hiking_state").toString());

                        myMemberNo      = memNo;
                        myHikingState   = hikingState;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (myHikingState) {
                                    case 0:
                                        btnChangeHikingState.setText("등산 시작");
                                        break;
                                    case 1:
                                        btnChangeHikingState.setText("등산 종료");
                                        break;
                                    case 2:
                                        btnChangeHikingState.setText("결과 보기");
                                        break;
                                }
                            }
                        });
                    } catch (IOException | ParseException ioe) {
                        Log.e(TAG, "getMemberNoByUserID: ", ioe);
                    }
                }
            });
    }

    private void paintHikingRoute() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (LatLng position : toPaintRouteSet) {
                    int idx = toPaintRouteSet.indexOf(position);

                    if (idx + 1 >= toPaintRouteSet.size()) break;

                    gMap.addPolyline(new PolylineOptions()
                            .add(toPaintRouteSet.get(idx), toPaintRouteSet.get(idx + 1))
                            .color(Color.RED)
                            .width(10));

                }
                setPointMarkers();
                moveToCurrentPosition();
            }
        });
    }

    private void setPointMarkers() {
        // [1] 시작점과 도착점이 같을 때.
        if (toPaintRouteSet.get(0).equals(toPaintRouteSet.get(toPaintRouteSet.size() - 1))) {
            // [1-1] 반환점 표시.
            // [1-1-1] 중복되지 않는 FID 찾기.
            int notDuplicatedFID = 0;   // 중복되지 않는 FID

            for (Object _o : scheduledFIDSet) {
                for (Object __o : scheduledFIDSet) {
                    if (!_o.equals(__o)) notDuplicatedFID = scheduledFIDSet.indexOf(__o);
                }
            }
            // [1-1-2] 중복되지 않는 FID의 마지막 좌표 얻기.


            // [1-2] 시/종점 표시.
        } else {    // [2] 시작점과 도착점이 다를 때.
            // [2-1] 시작점 표시.
            // 가장 앞 좌표.
            LatLng startPoint   = toPaintRouteSet.get(0);
            Drawable startPointDrawable = ContextCompat.getDrawable(getBaseContext(), R.drawable.start_point);
            Bitmap startPointBitmap = createMapIcon(this, startPointDrawable);
            // 시작점 마커 초기화
            MarkerOptions startPointOpt = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(startPointBitmap))
                    .position(startPoint);
            // 지도에 마커 등록
            gMap.addMarker(startPointOpt);
            // [2-2] 도착점 표시.
            // 가장 마지막 좌표.
            LatLng endPoint     = toPaintRouteSet.get(toPaintRouteSet.size() - 1);
            Drawable endPointDrawable = ContextCompat.getDrawable(getBaseContext(), R.drawable.end_point);
            Bitmap endPointBitmap = createMapIcon(this, endPointDrawable);
            // 도착점 마커 초기화
            MarkerOptions endPointOpt = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(endPointBitmap))
                    .position(endPoint);
            // 지도에 마커 등록
            gMap.addMarker(endPointOpt);
        }
    } // setPointMarkers END

    private Bitmap createMapIcon(Context context, Drawable drawable) {
        IconGenerator iconGenerator = new IconGenerator(context);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.multi_profile, null, false);

        ImageView imageView = view.findViewById(R.id.marker_img);
        imageView.setMaxWidth(24);
        imageView.setMaxHeight(24);
        imageView.setImageDrawable(drawable);

        iconGenerator.setContentView(view);
        iconGenerator.setContentPadding(5, 2, 5, 2);

        return iconGenerator.makeIcon();
    }

    private void moveToCurrentPosition() {
        try {

            LatLng latLng = new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

        } catch (NullPointerException npe) {
            Log.e(TAG, "moveToCurrentPosition: ", npe);
            setNoticeBar(View.VISIBLE, "위치 정보를 얻을 수 없습니다.", "GPS를 켜주세요");
        }
    } // moveToCurrentPosition END

    private void paintMarkers() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                myClusterManager.clearItems();

                for (int key : mapItems.keySet()) {
                    MapItem item = mapItems.get(key);

                    myClusterManager.addItem(item);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (Marker marker : myClusterManager.getMarkerCollection().getMarkers()) {
                            MapItem item = (MapItem) marker.getTag();

                            if (item instanceof Member) {
                                marker.setPosition((item.getPosition()));

                                if(marker.isInfoWindowShown()) {
                                    gMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                                }
                            }
                        }

                        myClusterManager.cluster();
                    }
                });
            }
        }).start();
    } // paintMarkers END

    private void updateHikingInfo() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (myCurrentLocation == null) {    // GPS 정보를 가져올 수 없을 때.
                    setNoticeBar(View.VISIBLE, "위치 정보를 얻을 수 없습니다.", "GPS를 켜주세요");
                    return;
                } else if (myHikingState == 0) {    // 등산 전 상태일 때.
                    setNoticeBar(View.VISIBLE, "등산을 시작해주세요.", "등산 시작 버튼은 계획한 일정의\n시작점 근처에서 활성화됩니다.");
                    return;
                }

                final double _hikedDistance = hikedDistance;

                HttpUrl reqUrl = Objects.requireNonNull(HttpUrl
                        .parse(Environments.LARAVEL_HIKONNECT_IP + "/api/storesend"))
                        .newBuilder()
                        .build();

                String memberNo     = String.valueOf(myMemberNo);
                String latitude     = String.valueOf(myCurrentLocation.getLatitude());
                String longitude    = String.valueOf(myCurrentLocation.getLongitude());
                String velocity     = String.valueOf(myCurrentLocation.getSpeed() * 3.6);
                String distance     = String.valueOf(_hikedDistance);

                RequestBody requestBody = new FormBody.Builder()
                        .add("member_no", memberNo)
                        .add("distance", distance)
                        .add("latitude", latitude)
                        .add("longitude", longitude)
                        .add("velocity", velocity)
                        .build();

                Request req = new Request.Builder().url(reqUrl).post(requestBody).build();

                okHttpClient.newCall(req).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e(TAG, "Http updateHikingInfo: ", e);
                        setNoticeBar(View.VISIBLE, "위치 보내기 실패.", "네트워크 상태를 확인해주세요.");
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (noticeBar.getVisibility() == View.VISIBLE) {
                            setNoticeBar(View.GONE, null, null);
                        }
                        try {
                            JSONParser parser = new JSONParser();

                            JSONObject result = (JSONObject) parser.parse(response.body().string());

                            JSONArray locationMemos = (JSONArray) result.get("location_memos");
                            JSONArray members = (JSONArray) result.get("members");

                            for (Object idx : locationMemos) {
                                double  latitude    = Double.valueOf(((JSONObject) idx).get("latitude").toString());
                                double  longitude   = Double.valueOf(((JSONObject) idx).get("longitude").toString());
                                int     no          = Integer.valueOf(((JSONObject) idx).get("no").toString());

                                LocationMemo locationMemo = new LocationMemo(new LatLng(latitude, longitude), no);

                                mapItems.put(no, locationMemo);
                            }

                            for (Object idx : members) {
                                double  latitude        = Double.valueOf(((JSONObject) idx).get("latitude").toString());
                                double  longitude       = Double.valueOf(((JSONObject) idx).get("longitude").toString());
                                int     memberNo        = Integer.valueOf(((JSONObject) idx).get("member_no").toString());
                                String  userID          = String.valueOf(((JSONObject) idx).get("userid").toString());
                                boolean exist           = false;

                                if (mapItems.containsKey(memberNo)) {
                                    Member member = (Member) mapItems.get(memberNo);
                                    member.setLocation(new LatLng(latitude, longitude));

                                } else {
                                    Member member = new Member(new LatLng(latitude, longitude), memberNo);
                                    member.userID = userID;

                                    HttpUrl httpUrl = HttpUrl
                                            .parse(Environments.NODE_HIKONNECT_IP + "/images/UserProfile/" + userID + ".jpg")
                                            .newBuilder()
                                            .build();

                                    Request req = new Request.Builder().url(httpUrl).build();

                                    Response res = okHttpClient.newCall(req).execute();

                                    InputStream is = res.body().byteStream();

                                    member.profileImg = BitmapFactory.decodeStream(is);

                                    mapItems.put(memberNo, member);
                                }

                                for (Integer _idx : mapItems.keySet()) {
                                    if (_idx == memberNo) exist = true;
                                }
                                if (!exist) {
                                    mapItems.remove(memberNo);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Post Response: ", e);
                        } finally {
                            if (noticeBar.getVisibility() == View.VISIBLE) {
                                setNoticeBar(View.GONE, null, null);
                            }
                        }
                    }
                });
            }
        }).start();
    } // updateHikingInfo END

    // 상단 알림 메시지 바 컨트롤러.
    private void setNoticeBar(final int visibility, final String main, @Nullable final String sub) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noticeBar.setVisibility(visibility);
                mapNoticeBarMainText.setText(main);
                mapNoticeBarSubText.setText(sub);
            }
        });
    } // setNoticeBar END

    private void updateCurrentFID() {
        try {

            double distanceFromLoc = Double.POSITIVE_INFINITY;

            Location pathStartPoint = new Location("pathStartPoint");

            for (HikingField field : hikingFields) {
                int idx = hikingFields.indexOf(field);

//                if (idx == currentFID - 1 || idx == currentFID || idx == currentFID + 1) {
                for (LatLng _loc : field.getRoutes()) {
                    pathStartPoint.setLatitude(_loc.latitude);
                    pathStartPoint.setLongitude(_loc.longitude);

                    double distance = pathStartPoint.distanceTo(myCurrentLocation);
                    if (distance < distanceFromLoc) {
                        distanceFromLoc     = distance;
                        currentFID          = idx;
                        currentPointInFID   = field.getRoutes().indexOf(_loc);
                    }
                }
//                }
            }
        } catch (Exception e) {
            Log.e(TAG, "updateCurrentFID: ", e);

        }
    } // updateCurrentFID END

    private double getDisToStartPoint() {
        try {
            LatLng startPoint = null;
            if (myHikingState == 0 ) {
                startPoint = toPaintRouteSet.get(0);
            } else if (myHikingState == 1){
                startPoint = toPaintRouteSet.get(toPaintRouteSet.size() -1 );
            }

           Location startPointLoc = new Location("startPoint");
           startPointLoc.setLatitude(startPoint != null ? startPoint.latitude : 0);
           startPointLoc.setLongitude(startPoint != null ? startPoint.longitude : 0);

           return startPointLoc.distanceTo(myCurrentLocation);

        } catch (Exception e) {
            Log.e(TAG, "getDisToStartPoint: ", e);
        }
        return Double.MAX_VALUE;
    } // getDisToStartPoint END

    private void updateHikedDistance() {
        if (hikingFields.size() == 0) {
            Log.e(TAG, "updateHikedDistance: hikingFields is empty.");
            return;
        }

        try {
            hikedDistance = 0.0;

            ArrayList<LatLng> userIn = hikingFields.get(currentFID).getRoutes();

            for (HikingField hikingField : hikingFields) {
                int idx             = hikingFields.indexOf(hikingField);
                int idxCurrentFID   = hikingFields.indexOf(hikingFields.get(currentFID));

                if (idx == 0) continue;
                if (idx > idxCurrentFID) break;

                hikedDistance += hikingFields.get(idx - 1).getFieldLength() * 100d;
            }

            for (LatLng latLng : hikingFields.get(currentFID).getRoutes()) {
                int idx = userIn.indexOf(latLng);
                if (idx > currentPointInFID || idx + 1 >= userIn.size()) continue;

                Location loc1 = new Location("loc1");
                loc1.setLatitude(latLng.latitude);
                loc1.setLongitude(latLng.longitude);

                Location loc2 = new Location("loc2");
                loc2.setLatitude(userIn.get(idx + 1).latitude);
                loc2.setLongitude(userIn.get(idx + 1).longitude);


                direction_set_lat1 = latLng.latitude;
                direction_set_lng1 = latLng.longitude;

                direction_set_lat2 = userIn.get(idx+ 1).latitude;
                direction_set_lng2 = userIn.get(idx + 1).longitude;

                hikedDistance += loc1.distanceTo(loc2) / 10;
            }

            hikedDistance = Math.round(hikedDistance) / 100d;

            hikingProgress = (int) (hikedDistance / wholeDistance * 100);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvHikingProgress.setText(String.valueOf(hikingProgress));
                    pgbarHikingProgress.setProgress(hikingProgress);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "updateHikedDistance: ", e);
        }
    } // updateHikedDistance END

    private void updateHikingState() {
        try {

            HttpUrl httpUrl = HttpUrl.parse(Environments.LARAVEL_HIKONNECT_IP + "/api/updateHikingState").newBuilder().build();

            RequestBody reqBody = new FormBody.Builder()
                    .add("member_no", String.valueOf(myMemberNo))
                    .add("state", String.valueOf(myHikingState))
                    .build();

            Request req = new Request.Builder()
                    .url(httpUrl)
                    .post(reqBody)
                    .build();

            okHttpClient.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "onFailure: ", e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String result = response.body().string();

                    Log.i(TAG, "Update Hiking State: res: " + result);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "updateHikingState: ", e);
        }
    } // updateHikingState END

    private short locbearing(double lat1,double lng1, double lat2, double lng2) {
        double Cur_Lat_radian = lat1 * (3.141592 / 180);
        double Cur_Lon_radian = lng1 * (3.141592 / 180);

        double Dest_Lat_radian = lat2 * (3.141592 / 180);
        double Dest_Lon_radian = lng2 * (3.141592 / 180);

        double radian_distance = 0;
        radian_distance = Math.acos(Math.sin(Cur_Lat_radian) * Math.sin(Dest_Lat_radian)
                + Math.cos(Cur_Lat_radian) * Math.cos(Dest_Lat_radian)
                * Math.cos(Cur_Lon_radian - Dest_Lon_radian));

        double radian_bearing = Math.acos((Math.sin(Dest_Lat_radian) - Math.sin(Cur_Lat_radian)
                * Math.cos(radian_distance)) / (Math.cos(Cur_Lat_radian)
                * Math.sin(radian_distance)));

        double true_bearing = 0;
        if (Math.sin(Dest_Lon_radian - Cur_Lon_radian) < 0)
        {
            true_bearing = radian_bearing * (180 / 3.141592);
            true_bearing = 360 - true_bearing;
        }
        else
        {
            true_bearing = radian_bearing * (180 / 3.141592);
        }

        return (short)true_bearing;
    }

    private void initializeUI() {
        // [1] Layout 초기화.
        noticeBar                   = findViewById(R.id.mapNoticeBar);
        mapNoticeBarMainText        = findViewById(R.id.mapNoticeBarMainText);
        mapNoticeBarSubText         = findViewById(R.id.mapNoticeBarSubText);
        // [1.1] 상태 표시 레이아웃.
        userDataBox                 = findViewById(R.id.userDataBox);     // Text View를 담을 부모 레이아웃.
        tvUserSpeed                 = findViewById(R.id.userSpeed);       // 유저의 현재 속도.
        tvDistance                  = findViewById(R.id.distance);        // 유저가 온 거리.
        tvArriveWhen                = findViewById(R.id.arriveWhen);      // 유저의 예상 도착시간.
        tvUserRank                  = findViewById(R.id.userRank);
        pgbarHikingProgress         = findViewById(R.id.seekBar);
        tvHikingProgress            = findViewById(R.id.seekBarProgress);

        // [1.2] 위치 메모.
        fabWriteLocationMemo        = findViewById(R.id.write_location_memo_btn);

        // [1.2.1] 위치 메모 작성 팝업.
        linearLayoutLocationMemo    = findViewById(R.id.imagelayout);
        edtTextLMemoTitle           = findViewById(R.id.l_memo_title);
        edtTextLMemoContent         = findViewById(R.id.l_memo_contnets_edttxt);
        imgViewLMemoImg             = findViewById(R.id.l_memo_img);
        btnLMemoCancel              = findViewById(R.id.l_memo_cancel_btn);
        btnLMemoSendReq             = findViewById(R.id.loc_memo_store_btn);

        // [1.3] 자기 위치 갱신 버튼.
        fabUpdateMyLocation         = findViewById(R.id.update_loc_btn);

        // [1.4] 그룹 맴버 리스트 버튼.
        fabShowMemberList           = findViewById(R.id.show_member_list_btn);

        // [1.5] 무전 레이아웃.
        drawerLayout                = findViewById(R.id.drawer);          // Hidden 레이아웃 활성/비활성 버튼.
        btnSendRadio                = findViewById(R.id.sendRecordData);  // 무전 보내기 버튼
        // showRecordList              = (ImageButton) findViewById(R.id.showRecordList);

        // [1.6] 등산 상태 변경 버튼.
        btnChangeHikingState        = findViewById(R.id.change_h_status_btn);

        // [2] 이벤트 리스너 등록.
        userDataBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivityTemp.this, ClusterDetailActivity.class);
                startActivity(intent);
            }
        });
        // [2.1] 위치 메모 작성 버튼.
        fabWriteLocationMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayoutLocationMemo.getVisibility() == View.VISIBLE) {
                    linearLayoutLocationMemo.setVisibility(View.INVISIBLE);
                } else {
                    linearLayoutLocationMemo.setVisibility(View.VISIBLE);
                }
            }
        });
        // 레이아웃 뒤쪽 클릭 무시.
        linearLayoutLocationMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        imgViewLMemoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivityTemp.this);
                builder.setTitle("사진 등록");
                builder.setMessage("사진 등록 방법을 선택해주세요.");
                builder.setPositiveButton("카메라", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());

                        startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
                    }
                });
                builder.setNegativeButton("갤러리", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent getPhotoIntent = new Intent(Intent.ACTION_PICK);
                        getPhotoIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        getPhotoIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(getPhotoIntent, GALLERY_CODE);
                    }
                });
                builder.show();
            }
        });
        btnLMemoSendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String scheduleNum = getIntent().getStringExtra("schedule_no");

                BitmapDrawable bitmapDrawable = (BitmapDrawable) imgViewLMemoImg.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] bytesImg = bos.toByteArray();

                BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.location_memo_img_view_txt);
                Bitmap photoUnregDrawable = drawable.getBitmap();

                String isPhotoRegisterd = String.valueOf(!bitmap.equals(photoUnregDrawable));

                HttpUrl httpUrl = Objects.requireNonNull(HttpUrl
                        .parse(Environments.NODE_HIKONNECT_IP + "/location/regLocation"))
                        .newBuilder()
                        .build();

                RequestBody reqBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("schedule_no",     scheduleNum)
                        .addFormDataPart("title",           edtTextLMemoTitle.getText().toString())
                        .addFormDataPart("content",         edtTextLMemoContent.getText().toString())
                        .addFormDataPart("writer",          pref.getString("user_id", ""))
                        .addFormDataPart("picture",         isPhotoRegisterd)
                        .addFormDataPart("latitude",        String.valueOf(myCurrentLocation.getLatitude()))
                        .addFormDataPart("longitude",       String.valueOf(myCurrentLocation.getLongitude()))
                        .addFormDataPart("location", "location.jpg", RequestBody.create(MediaType.parse("image/jpeg"), bytesImg))
                        .build();

                Request request = new Request.Builder()
                        .url(httpUrl)
                        .post(reqBody)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e(TAG, "onFailure: ", e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MapsActivityTemp.this, "메모 등록에 실패했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String result = response.body().string();

                        if (result.equals("Success")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    linearLayoutLocationMemo.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                });
            }
        }); // btnLMemoSendReq.setOnClickListener END
        btnLMemoCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = getResources().getDrawable(R.drawable.location_memo_img_view_txt);
                Bitmap bitmap = Bitmap.createBitmap(((BitmapDrawable) drawable).getBitmap());

                edtTextLMemoTitle.setText("");
                edtTextLMemoContent.setText("");
                imgViewLMemoImg.setImageBitmap(bitmap);
                linearLayoutLocationMemo.setVisibility(View.INVISIBLE);
            }
        }); // btnLMemoCancel.setOnClickListener END
        // [2.2] 자기 위치 갱신 버튼
        fabUpdateMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMyLocationBtnPressed = !isMyLocationBtnPressed;
                if (isMyLocationBtnPressed) {
                    if (myCurrentLocation != null) {
                        LatLng myLocation = new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18));
                    }
                    fabUpdateMyLocation.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.my_position_btn_pressed_svg));
                } else {
                    fabUpdateMyLocation.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.my_position_btn_svg));
                }
            }
        }); // fabUpdateMyLocation.setOnClickListener END
        // [2.3] 그룹 맴버 리스트 버튼
        fabShowMemberList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groupMemberList = new Intent(MapsActivityTemp.this, Othersinfo.class);
                groupMemberList.putExtra("member_no", myMemberNo);
                startActivityForResult(groupMemberList, HikingMemberListAdapter.REQUEST_CODE);
            }
        }); // fabShowMemberList.setOnClickListener END
        // [2.4] 등산 상태 변경 버튼
        btnChangeHikingState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (myHikingState) {
                    case 0:     // 등산 전
                        updateHikingState();
                        myHikingState = 1;
                        btnChangeHikingState.setVisibility(View.GONE);
                        btnChangeHikingState.setText("산행 종료");
                        requestMyHikingInfo();
                        userDataBox.setVisibility(View.VISIBLE);
                        break;
                    case 1:     // 등산 중
                        updateHikingState();
                        myHikingState = 2;
                        btnChangeHikingState.setText("결과 보기");
                    case 2:     // 등산 후
                        Intent afterHikingIntent = new Intent(getBaseContext(), AfterHikingActivity.class);
                        afterHikingIntent.putExtra("member_no", myMemberNo);
                        startActivity(afterHikingIntent);
                        break;
                }
            }
        }); // btnChangeHikingState.setOnClickListener END
        // [5] 무전 리스트
        /*showRecordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recordIntent = new Intent(getBaseContext(), RecordListActivity.class);
                startActivity(recordIntent);
            }
        }); // showRecordList.setOnClickListener END*/

        // [4] drawerLayout 을 클릭하면 무전 버튼 가시화
        /*drawerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecBtnVisible) {
                    btnSendRadio.setVisibility(View.GONE);
                } else {
                    btnSendRadio.setVisibility(View.VISIBLE);
                }
                isRecBtnVisible = !isRecBtnVisible;
            }
        }); // drawerLayout.setOnClickListener END*/
        // 무전 버튼에 리스너 달기
        /*btnSendRadio.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isSendingNow = true;
                walkieTalkie.sendStart();
                Toast.makeText(getBaseContext(), "무전 시작합니다", Toast.LENGTH_SHORT).show();
                return false;
            }
        }); // btnSendRadio.setOnLongClickListener END
        btnSendRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSendingNow) {
                    isSendingNow = false;
                    walkieTalkie.sendEnd();
                    Toast.makeText(getBaseContext(), "무전 종료합니다", Toast.LENGTH_SHORT).show();
                }
            }
        }); // btnSendRadio.setOnClickListener END*/

    } // initializeUI END

    private void sendPicture(Uri imgUri) {

        String imagePath = getRealPathFromURI(imgUri); // path 경로

        Log.d(TAG, "sendPicture: imagePath: " + imagePath);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
        imgViewLMemoImg.setImageBitmap(bitmap);//이미지 뷰에 비트맵 넣기

    } // sendPicture END

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    } // exifOrientationToDegrees END

    private String getRealPathFromURI(Uri contentUri) {

        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        @SuppressLint("Recycle")
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor != null ? cursor.getString(column_index) : null;
    } // getRealPathFromURI END

    private void getPictureForPhoto(Intent intent) {

        Bundle extras = intent.getExtras();

        Bitmap bitmap = (Bitmap) (extras != null ? extras.get("data") : null);
        if (bitmap != null) {
            bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
        }
        imgViewLMemoImg.setImageBitmap(bitmap);//이미지 뷰에 비트맵 넣기
    } // getPictureForPhoto END

    private File createImageFile() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        mImageCaptureName = timeStamp + ".png";

        return new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/" + mImageCaptureName);
    } // createImageFile END

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_CODE:
                    sendPicture(data.getData()); //갤러리에서 사진 가져오기.
                    break;
                case REQUEST_TAKE_PHOTO:
                    getPictureForPhoto(data); //카메라에서 사진 가져오기.
                    break;
                case HikingMemberListAdapter.REQUEST_CODE:

                    int     userNum     = data.getIntExtra("user_number", 0);     // 클릭된 맴버의 ID.
                    double  latitude    = data.getDoubleExtra("user_lat", 0.0d);  // 클릭된 맴버 위도.
                    double  longitude   = data.getDoubleExtra("user_lng", 0.0d);  // 클릭된 맴버 경도.

                    LatLng posToMove = new LatLng(latitude, longitude);

                    for (Marker marker : markers) {
                        if (marker.getTag() instanceof Member) {
                            if(((Member) marker.getTag()).member_no == userNum) {
                                marker.showInfoWindow();
                            }
                        }
                    }
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posToMove, 18));
                    break;
            }
        } else if (resultCode == Othersinfo.SHOW_ALL) {

            double  xStart  = Double.POSITIVE_INFINITY,
                    xEnd    = Double.NEGATIVE_INFINITY,
                    yStart  = Double.NEGATIVE_INFINITY,
                    yEnd    = Double.POSITIVE_INFINITY;

            for(Integer _key : mapItems.keySet()) {
                MapItem _item = mapItems.get(_key);

                if (_item instanceof Member) {
                    double lat = _item.getPosition().latitude;
                    double lng = _item.getPosition().longitude;

                    if (lat < xStart) xStart = lat;
                    if (lat > xEnd) xEnd = lat;
                    if (lng < yEnd) yEnd = lng;
                    if (lng > yStart) yStart = lng;
                }
            }

            double  xCenter = xStart + (xEnd - xStart) / 2,
                    yCenter = yEnd + (yStart - yEnd) / 2;

            LatLng center = new LatLng(xCenter, yCenter);

            DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
            int deviceWidth = (int) (displayMetrics.widthPixels / displayMetrics.density);

            double logUp    = Math.log((360 * deviceWidth) / (xEnd - xStart));
            double logBase  = Math.log(2);

            float _v = Float.valueOf(String.valueOf(logUp / logBase - 12));

            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, _v));
        }
    } // onActivityResult END

    @Override
    public void onLocationChanged(Location location) {

        myCurrentLocation = location;

        double userSpeed = Math.round(location.getSpeed() * 36d) / 10d;
        tvUserSpeed.setText(String.valueOf(userSpeed));

        double arriveWhen = (wholeDistance - hikedDistance) / userSpeed;

        // 도착 시간 계산.
        if (arriveWhen == Double.POSITIVE_INFINITY || arriveWhen == Double.NEGATIVE_INFINITY) {
            tvArriveWhen.setText("0.0");
        } else {
            int arriveWhenFormatted = (int) arriveWhen * 100;
            tvArriveWhen.setText(String.valueOf(arriveWhenFormatted));
        }

        // 내 마커 움직이기.
        myMarker.setPosition(new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude()));

        // 내 위치 버튼이 눌러져 있으면.
        if (isMyLocationBtnPressed) {
            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            // 내 위치로 화면을 이동.
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18));
        }
    } // onLocationChanged END

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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
