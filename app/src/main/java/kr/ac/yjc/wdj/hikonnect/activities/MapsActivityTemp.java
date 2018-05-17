package kr.ac.yjc.wdj.hikonnect.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Location;
import android.location.LocationListener;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.yjc.wdj.hikonnect.Environment;
import kr.ac.yjc.wdj.hikonnect.Locationmemo;
import kr.ac.yjc.wdj.hikonnect.Othersinfo;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.RecordListActivity;
import kr.ac.yjc.wdj.hikonnect.apis.LocationService;
import kr.ac.yjc.wdj.hikonnect.apis.PermissionManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class Member extends MapItem implements ClusterItem {

    int member_no;
    int userid;
    Bitmap profileImg;

    Member(LatLng location, int member_no) {
        super(location);
        this.member_no = member_no;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }
}

class LocationMemo extends MapItem implements ClusterItem {

    int no;

    LocationMemo(LatLng location, int no) {
        super(location);
        this.no = no;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }
}

class MapItem implements ClusterItem {
    protected LatLng location;

    MapItem(LatLng location) {
        this.location = location;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }
}

class HikingField {

    private int     fid;
    private double  fieldLength;
    private ArrayList<LatLng> routes = new ArrayList<>();

    public HikingField(int fid, double fieldLength) {
        this.fid            = fid;
        this.fieldLength    = fieldLength;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public int getFid() {
        return fid;
    }

    public void setFieldLength(double fieldLength) {
        this.fieldLength = fieldLength;
    }

    public double getFieldLength() {
        return fieldLength;
    }

    public ArrayList<LatLng> getRoutes() {
        return routes;
    }

    public void addLatLng(LatLng latLng) {
        routes.add(latLng);
    }
}

public class MapsActivityTemp extends FragmentActivity implements
        OnMapReadyCallback,
        OnMapClickListener,
        LocationListener {

    private final String                TAG = "HIKONNECT";

    private int                         myMemberNo = 1;

    // 지도, 위치.
    private GoogleMap                   gMap;
    private Location                    myCurrentLocation;
    private SupportMapFragment          mapFragment;
    private double                      hikedDistance = 0.0;

    // HTTP 통신.
    private OkHttpClient                okHttpClient = new OkHttpClient();

    // 등산 데이터.
    private ArrayList<HikingField>      hikingFields        = new ArrayList<>();
    private int                         hikingState         = 0;
    private int                         currentFID          = 0;
    private int                         currentPointInFID   = 0;
    private double                      wholeDistance       = 0.0;

    private LinkedList<LatLng>          allHikingRoute = new LinkedList<>();
    private HashMap<Integer, MapItem>   mapItems = new HashMap<>();
    private ArrayList<MarkerOptions>    markerOptions = new ArrayList<>();
    private ArrayList<Marker>           markers;
    private ClusterManager<MapItem>     myClusterManager;
    private Marker                      myMarker;

    // [1] UI 클래스
    // [1.1] 현재 상태 표시.
    private CardView            userDataBox;    // 자신의 현재 정보를 보여줄 CardView
    private TextView            tvUserSpeed;    // 현재 속도 TextView (값 -> km/h 기준)
    private TextView            tvDistance;     // 총 이동 거리 TextView (값 -> km 기준)
    private TextView            tvArriveWhen;   // 예상 도착 시간 TextView (값 -> 시간 기준)

    private CardView            otherUserDataBox;
    private TextView            tvOtherUserSpeed;
    private TextView            tvOtherUserDistance;
    private TextView            tvOtherUserArriveWhen;

    // [1.2] 위치 메모.
    private FloatingActionButton    fabWriteLocationMemo;

    // [1.2.1] 위치 메모 작성 팝업.
    private LinearLayout    linearLayoutLocationMemo;
    private EditText        edtTextLMemoTitle;
    private EditText        edtTextLMemoContent;
    private ImageView       imgViewLMemoImg;
    private Button          btnLMemoSendReq;
    private Button          btnLMemoCancel;

    // [1.3] 자기 위치 갱신 버튼.
    private FloatingActionButton    fabUpdateMyLocation;

    // [1.4] 그룹 맴버 리스트 버튼
    private FloatingActionButton    fabShowMemberList;

    // [1.5] 자기 정보 조회 버튼
    private FloatingActionButton    fabShowMyInfo;

    // [1.6] 무전
    private LinearLayout    drawerLayout;       // 무전 버튼을 넣어둘 레이아웃
    private Button          btnSendRadio;       // 무전 시작 버튼
    private ImageButton     showRecordList;     // 음성 녹음 리스트.

    // [1.7] 등산 상태 변경 버튼
    private Button          btnChangeHikingState;   // 등산 시작, 등산 끝 버튼.

    // [3] 상태 저장 변수.
    private boolean     isdataBoxVisible        = false;    // 현재 데이터 박스 상태
    private boolean     isRecBtnVisible         = false;    // 현재 녹음 버튼 상태
    private boolean     isRequestingLocation    = false;    // 위치 관리자 활성화 상태.

    private class MapItemRenderer extends DefaultClusterRenderer<MapItem> {

        static private final int BIT_MAP_SIZE = 200;
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        private Bitmap bitmap;

        public MapItemRenderer() {
            super(getApplicationContext(), gMap, myClusterManager);

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
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }
            if (item instanceof Member) {

                if (((Member) item).member_no == myMemberNo) {
                    markerOptions.visible(false);
                } else {
                    getUserProfileImg();
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                    roundedBitmapDrawable.setCornerRadius(100.0f);
                    roundedBitmapDrawable.setAntiAlias(true);

                    mImageView.setImageDrawable(roundedBitmapDrawable);
                    Bitmap icon = mIconGenerator.makeIcon();
                    markerOptions
                            .icon(BitmapDescriptorFactory.fromBitmap(icon));
                }
            }
        }

        private void getUserProfileImg() {
            try {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpUrl.Builder urlBuilder = HttpUrl
                                    .parse(/*Environment.NODE_HIKONNECT_IP*/"http://172.26.2.88:3000" + "/images/UserProfile/" + "test1" + ".jpg")
                                    .newBuilder();

                            String reqUrl = urlBuilder.build().toString();

                            Request req = new Request.Builder()
                                    .url(reqUrl)
                                    .build();

                            Response response = okHttpClient.newCall(req).execute();

                            if (response.isSuccessful()) {
                                InputStream is = response.body().byteStream();

                                Bitmap originBitmap = BitmapFactory.decodeStream(is);

                                bitmap = Bitmap.createScaledBitmap(originBitmap, BIT_MAP_SIZE, BIT_MAP_SIZE, true);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "run: ", e);
                        }
                    }
                });
                thread.start();
                thread.join();

            } catch (Exception e) {
                Log.e(TAG, "getUserProfileImg: ", e);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PermissionManager permissionManager = new PermissionManager(this);
        permissionManager.requestPermissions();

        setContentView(R.layout.activity_maps_temp);

        // [1] GoogleMaps Fragment 불러오기.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // [2] 위치 서비스 클래스 초기화.
        LocationService locationService = new LocationService(this);
        locationService.setLocationListener(this);
        // [3] HTTP 클래스 초기화.
        okHttpClient = new OkHttpClient();

        initializeUI();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        UiSettings mapUiSettings = gMap.getUiSettings();

        mapUiSettings.setMapToolbarEnabled(false);

        try {
            googleMap.setOnMapClickListener(this);

            myClusterManager = new ClusterManager<>(this, gMap);
            myClusterManager.setRenderer(new MapItemRenderer());
            myClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MapItem>() {
                @Override
                public boolean onClusterItemClick(MapItem mapItem) {
                    if (otherUserDataBox.getVisibility() == View.GONE) {
                        otherUserDataBox.setVisibility(View.VISIBLE);
                    } else {
                        otherUserDataBox.setVisibility(View.GONE);
                    }

                    if (mapItem instanceof LocationMemo) {
                        Log.d(TAG, "onClusterItemClick: no: " + ((LocationMemo) mapItem).no);
                    }
                    if (mapItem instanceof Member) {
                        Log.d(TAG, "onClusterItemClick: member_no: " + ((Member) mapItem).member_no);
                    }
                    return false;
                }
            });

            gMap.setOnMarkerClickListener(myClusterManager);
            gMap.setOnMapClickListener(this);
            gMap.setOnCameraIdleListener(myClusterManager);

            myMarker = gMap.addMarker(new MarkerOptions()
                    .position(new LatLng(0, 0))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)/*R.drawable.baseline_fiber_manual_record_black_18dp*/)
                    .alpha(0.8f)
                    .zIndex(1.0f));

            requestHikingRoute();

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {

                    updateHikingState();
                    updateCurrentFID();
                    paintMakers();
                    updateHikedDistance();

                    markers = new ArrayList<>(myClusterManager.getMarkerCollection().getMarkers());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (getDisToStartPoint() < 50) {
                                    btnChangeHikingState.setVisibility(View.VISIBLE);
                                } else {
                                    btnChangeHikingState.setVisibility(View.GONE);
                                }

                                myMarker.setPosition(new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude()));

                                if (hikingState != 0) {
                                    tvDistance.setText(String.valueOf(hikedDistance));
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "timerTask: ", e);
                            }
                        }
                    });
                }
            };

            Timer timer = new Timer();
            timer.schedule(timerTask, 0, 2000);

        } catch (Exception e) {
            Log.e(TAG, "onMapReady: ", e);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (isdataBoxVisible) {
            userDataBox.setVisibility(View.GONE);
            isdataBoxVisible = false;
        } else {
            userDataBox.setVisibility(View.VISIBLE);
            isdataBoxVisible = true;
        }
    }

    private void requestHikingRoute() throws NullPointerException {

        HttpUrl.Builder urlBuilder = HttpUrl
                .parse(Environment.NODE_HIKONNECT_IP + "/dummy/school")
                .newBuilder();

        String reqUrl = urlBuilder.build().toString();

        Request req = new Request.Builder()
                .url(reqUrl)
                .build();

        okHttpClient
                .newCall(req)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Request Hiking Route: ", e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MapsActivityTemp.this, "연결 상태 불량.\n네트워크 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            JSONParser parser = new JSONParser();
                            String serverRes = response.body().string();


                            JSONArray routeSet = (JSONArray) parser.parse(serverRes);
                            wholeDistance = 0.0;

                            for (Object idx : routeSet) {
                                JSONObject attributes = (JSONObject) ((JSONObject) idx).get("attributes");
                                JSONArray paths = (JSONArray) ((JSONArray) ((JSONObject) ((JSONObject) idx).get("geometry")).get("paths")).get(0);

                                int fid         = Integer.valueOf(attributes.get("FID").toString());
                                double fieldLength = Double.valueOf(attributes.get("PMNTN_LT").toString());

                                wholeDistance += fieldLength;

                                HikingField hikingField = new HikingField(fid, fieldLength);
                                for (Object _idx : paths) {
                                    double lat = Double.valueOf(((JSONObject) _idx).get("lat").toString());
                                    double lng = Double.valueOf(((JSONObject) _idx).get("lng").toString());

                                    hikingField.addLatLng(new LatLng(lat, lng));

                                    allHikingRoute.offer(new LatLng(lat, lng));
                                }

                                hikingFields.add(hikingField);
                            }

                            paintHikingRoute();
                        } catch (Exception e) {
                            Log.e(TAG, "Parse Server Res: ", e);
                        }
                    }
                });
    }

    private void paintHikingRoute() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (LatLng position : allHikingRoute) {
                    int idx = allHikingRoute.indexOf(position);

                    if (idx + 1 >= allHikingRoute.size()) break;

                    gMap.addPolyline(new PolylineOptions()
                            .add(allHikingRoute.get(idx), allHikingRoute.get(idx + 1))
                            .color(Color.RED)
                            .width(10));
                }

                gMap.moveCamera(CameraUpdateFactory.newLatLng(allHikingRoute.get(0)));
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(allHikingRoute.get(0), 19));
            }
        });
    }

    private void paintMakers() {
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
                        for (MarkerOptions val : markerOptions) {
                            gMap.addMarker(val);
                        }
                    }
                });
            }
        }).start();
    }

    private void updateHikingState() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    HttpUrl.Builder urlBuilder = HttpUrl
                            .parse(Environment.LARAVEL_HIKONNECT_IP + "/api/storesend")
                            .newBuilder();

                    final String reqUrl = urlBuilder.build().toString();

                    String memberNo     = String.valueOf(myMemberNo);
                    String latitude     = String.valueOf(0);
                    String longitude    = String.valueOf(0);
                    String velocity     = String.valueOf(0);
                    String distance     = String.valueOf(0);

                    if (hikingState != 0) {
                        latitude = String.valueOf(myCurrentLocation.getLatitude());
                        longitude = String.valueOf(myCurrentLocation.getLongitude());
                        velocity = String.valueOf(myCurrentLocation.getSpeed() * 3.6);
                        distance = String.valueOf(hikedDistance);
                    }

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
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "Http updateHikingState: ", e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MapsActivityTemp.this, "연결 상태 불량.\n네트워크 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                JSONParser parser = new JSONParser();

                                JSONObject result = (JSONObject) parser.parse(response.body().string());

                                JSONArray locationMemos = (JSONArray) result.get("location_memos");
                                JSONArray members = (JSONArray) result.get("members");

                                for (Object idx : locationMemos) {
                                    double latitude = Double.valueOf(((JSONObject) idx).get("latitude").toString());
                                    double longitude = Double.valueOf(((JSONObject) idx).get("longitude").toString());
                                    int no = Integer.valueOf(((JSONObject) idx).get("no").toString());

                                    LocationMemo locationMemo = new LocationMemo(new LatLng(latitude, longitude), no);

                                    mapItems.put(no, locationMemo);
                                    MarkerOptions mOptions = new MarkerOptions()
                                            .position(locationMemo.getPosition());
                                }
                                for (Object idx : members) {
                                    double latitude     = Double.valueOf(((JSONObject) idx).get("latitude").toString());
                                    double longitude    = Double.valueOf(((JSONObject) idx).get("longitude").toString());
                                    int memberNo        = Integer.valueOf(((JSONObject) idx).get("member_no").toString());

                                    Member member = new Member(new LatLng(latitude, longitude), memberNo);

                                    mapItems.put(memberNo, member);
                                    MarkerOptions mOptions = new MarkerOptions()
                                            .position(member.getPosition());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Post Response: ", e);
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Post request: ", e);
                }
            }
        }).start();
    }

    private void updateCurrentFID() {
        try {

            double distanceFromLoc = Double.POSITIVE_INFINITY;

            Location pathStartPoint = new Location("pathStartPoint");

            for (HikingField field : hikingFields) {
                int idx = hikingFields.indexOf(field);

                if (idx == currentFID - 1 || idx == currentFID || idx == currentFID + 1) {
                    for (LatLng _loc : field.getRoutes()) {
                        pathStartPoint.setLatitude(_loc.latitude);
                        pathStartPoint.setLongitude(_loc.longitude);

                        double distance = pathStartPoint.distanceTo(myCurrentLocation);
                        if (distance < distanceFromLoc) {
                            distanceFromLoc     = distance;
                            currentFID          = field.getFid();
                            currentPointInFID   = field.getRoutes().indexOf(_loc);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "updateCurrentFID: ", e);
        }
    }

    private double getDisToStartPoint() {
        try {
            LatLng startPoint = null;
            if (hikingState == 0 ) {
                startPoint = allHikingRoute.get(0);
            } else if (hikingState == 1){
                startPoint = allHikingRoute.getLast();
            }

           Location startPointLoc = new Location("startPoint");
           startPointLoc.setLatitude(startPoint.latitude);
           startPointLoc.setLongitude(startPoint.longitude);

           return startPointLoc.distanceTo(myCurrentLocation);

        } catch (Exception e) {

        }
        return -1.0d;
    }

    private void updateHikedDistance() {
        try {
            hikedDistance = 0.0;

            ArrayList<LatLng> userIn = hikingFields.get(currentFID).getRoutes();

            for (HikingField hikingField : hikingFields) {
                int idx             = hikingFields.indexOf(hikingField);
                int idxCurrentFID   = hikingFields.indexOf(hikingFields.get(currentFID));

                if (idx == 0) continue;
                if (idx > idxCurrentFID) break;

                hikedDistance += hikingFields.get(idx - 1).getFieldLength() * 1000;
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

                hikedDistance += loc1.distanceTo(loc2);
            }

            hikedDistance = Math.round(hikedDistance) / 1000d;
        } catch (Exception e) {
            Log.e(TAG, "updateHikedDistance: ", e);
        }
    }

    private void initializeUI() {
        // [1] Layout 초기화.
        // [1.1] 상태 표시 레이아웃.
        userDataBox     = (CardView) findViewById(R.id.userDataBox);     // Text View를 담을 부모 레이아웃.
        tvUserSpeed     = (TextView) findViewById(R.id.userSpeed);       // 유저의 현재 속도.
        tvDistance      = (TextView) findViewById(R.id.distance);        // 유저가 온 거리.
        tvArriveWhen    = (TextView) findViewById(R.id.arriveWhen);      // 유저의 예상 도착시간.

        otherUserDataBox        = (CardView) findViewById(R.id.otherUserDataBox);
        tvOtherUserSpeed        = (TextView) findViewById(R.id.otherUserSpeed);
        tvOtherUserDistance     = (TextView) findViewById(R.id.otherUserDistance);
        tvOtherUserArriveWhen   = (TextView) findViewById(R.id.otherUserArriveWhen);

        // [1.2] 위치 메모.
        fabWriteLocationMemo = (FloatingActionButton) findViewById(R.id.write_location_memo_btn);

        // [1.2.1] 위치 메모 작성 팝업.
        linearLayoutLocationMemo = (LinearLayout) findViewById(R.id.imagelayout);
        edtTextLMemoTitle = (EditText) findViewById(R.id.l_memo_title);
        edtTextLMemoContent = (EditText) findViewById(R.id.l_memo_contnets_edttxt);
        imgViewLMemoImg = (ImageView) findViewById(R.id.l_memo_img);
        btnLMemoCancel  = (Button)  findViewById(R.id.l_memo_cancel_btn);
        btnLMemoSendReq = (Button) findViewById(R.id.loc_memo_store_btn);

        // [1.3] 자기 위치 갱신 버튼.
        fabUpdateMyLocation = (FloatingActionButton) findViewById(R.id.update_loc_btn);

        // [1.4] 그룹 맴버 리스트 버튼
        fabShowMemberList = (FloatingActionButton) findViewById(R.id.show_member_list_btn);

        // [1.5] 무전 레이아웃.
        drawerLayout    = (LinearLayout) findViewById(R.id.drawer);          // Hidden 레이아웃 활성/비활성 버튼.
        btnSendRadio    = (Button) findViewById(R.id.sendRecordData);  // 무전 보내기 버튼
        showRecordList  = (ImageButton) findViewById(R.id.showRecordList);

        // [1.6] 등산 상태 변경 버튼
        btnChangeHikingState = (Button) findViewById(R.id.change_h_status_btn);

        // [2] 이벤트 리스너 등록
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
        btnLMemoSendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo
            }
        });
        btnLMemoCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtTextLMemoTitle.setText("");
                edtTextLMemoContent.setText("");
                imgViewLMemoImg.setImageBitmap(null);
                linearLayoutLocationMemo.setVisibility(View.INVISIBLE);
            }
        });
        // [2.2] 자기 위치 갱신 버튼
        fabUpdateMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LatLng currentLatLng = new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());

                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 19));
                } catch (Exception e) {
                    Log.e(TAG, "onClick: ", e);
                }
            }
        });
        // [2.3] 그룹 맴버 리스트 버튼
        fabShowMemberList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groupMemberList = new Intent(MapsActivityTemp.this, Othersinfo.class);
                groupMemberList.putExtra("member_no", myMemberNo);
                startActivity(groupMemberList);
            }
        });
        showRecordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recordIntent = new Intent(getBaseContext(), RecordListActivity.class);
                startActivity(recordIntent);
            }
        });
        // [2.4] 등산 상태 변경 버튼
        btnChangeHikingState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (hikingState) {
                    case 0:     // 등산 전
                        hikingState = 1;
                        btnChangeHikingState.setVisibility(View.GONE);
                        btnChangeHikingState.setText("산행 종료");
                        break;
                    case 1:     // 등산 중
                        hikingState = 2;
                        btnChangeHikingState.setVisibility(View.GONE);
                        break;
                    case 2:     // 등산 후
                        hikingState = 3;
                        btnChangeHikingState.setVisibility(View.GONE);
                        break;
                }
            }
        });
        // [5] 무전 리스트
        showRecordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recordIntent = new Intent(getBaseContext(), RecordListActivity.class);
                startActivity(recordIntent);
            }
        });
        // [4] drawerLayout 을 클릭하면 무전 버튼 가시화
        drawerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecBtnVisible) {
                    btnSendRadio.setVisibility(View.GONE);
                } else {
                    btnSendRadio.setVisibility(View.VISIBLE);
                }
                isRecBtnVisible = !isRecBtnVisible;
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {
        myCurrentLocation = location;

        double userSpeed = Math.round(location.getSpeed() * 36d) / 10d;
        tvUserSpeed.setText(String.valueOf(userSpeed));

        Log.d(TAG, "onLocationChanged: userSpeed: " + userSpeed);
        Log.d(TAG, "onLocationChanged: 1: " + String.valueOf(wholeDistance - hikedDistance));
        Log.d(TAG, "onLocationChanged: 2: " + String.valueOf((wholeDistance - hikedDistance) / userSpeed));
        Log.d(TAG, "onLocationChanged: 3: " + String.valueOf(Math.round((wholeDistance - hikedDistance) / userSpeed * 1000d) / 1000d));

        double arriveWhen = (wholeDistance - hikedDistance) / userSpeed;

        if (arriveWhen == Double.POSITIVE_INFINITY) {
            tvArriveWhen.setText("0.0");
        } else {
            double arriveWhenFormatted = Math.round(arriveWhen * 100d) / 100d;
            tvArriveWhen.setText(String.valueOf(arriveWhenFormatted));
        }
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
}
