package kr.ac.yjc.wdj.hikonnect.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.yjc.wdj.hikonnect.Environment;
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

    private int member_no;

    Member(LatLng location, int member_no) {
        super(location);
        this.member_no = member_no;
    }

    public void setMember_no(int member_no) {
        this.member_no = member_no;
    }

    public int getMember_no() {
        return member_no;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }
}

class LocationMemo extends MapItem implements ClusterItem {

    private int no;

    LocationMemo(LatLng location, int no) {
        super(location);
        this.no = no;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
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

    // 지도, 위치.
    private GoogleMap                   gMap;
    private Location                    myCurrentLocation;
    private SupportMapFragment          mapFragment;
    private double                      hikedDistance = 0.0;

    // HTTP 통신.
    private OkHttpClient                okHttpClient = new OkHttpClient();

    // 등산 데이터.
    private ArrayList<HikingField>      hikingFields        = new ArrayList<>();
    private int                         currentFID          = 0;
    private int                         currentPointInFID   = 0;

    private LinkedList<LatLng>          allHikingRoute = new LinkedList<>();
    private HashMap<Integer, MapItem>   mapItems = new HashMap<>();
    private ArrayList<MarkerOptions>    markerOptions = new ArrayList<>();
    private ArrayList<Marker>           markers = new ArrayList<>();
    private ClusterManager<MapItem>     myClusterManager;
    private Marker                      myMarker;

    // [1] UI 클래스
    // [1.1] 현재 상태 표시.
    private CardView            userDataBox;    // 자신의 현재 정보를 보여줄 CardView
    private TextView            tvUserSpeed;    // 현재 속도 TextView (값 -> km/h 기준)
    private TextView            tvDistance;     // 총 이동 거리 TextView (값 -> km 기준)
    private TextView            tvArriveWhen;   // 예상 도착 시간 TextView (값 -> 시간 기준)

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
    // [3] 상태 저장 변수.
    private boolean     isdataBoxVisible        = false;    // 현재 데이터 박스 상태
    private boolean     isRecBtnVisible         = false;    // 현재 녹음 버튼 상태
    private boolean     isRequestingLocation    = false;    // 위치 관리자 활성화 상태.

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
            myMarker = gMap.addMarker(new MarkerOptions()
                    .position(new LatLng(0, 0))
                    .zIndex(1.0f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            googleMap.setOnMapClickListener(this);

            myClusterManager = new ClusterManager<>(this, gMap);
            myClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MapItem>() {
                @Override
                public boolean onClusterItemClick(MapItem mapItem) {
                    Log.d(TAG, "onClusterClick: CLICKED!");
                    if (gMap.getCameraPosition().zoom <= 25)
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapItem.getPosition(), gMap.getCameraPosition().zoom + 1));
                    return false;
                }
            });
            gMap.setOnCameraIdleListener(myClusterManager);

            requestHikingRoute();

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {

                    updateHikingState();
                    updateCurrentFID();
                    paintMakers();
                    updateHikedDistance();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                myMarker.setPosition(new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude()));
                                tvDistance.setText(String.valueOf(hikedDistance));
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
            userDataBox.setVisibility(View.INVISIBLE);
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
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            JSONParser parser = new JSONParser();
                            String serverRes = response.body().string();


                            JSONArray routeSet = (JSONArray) parser.parse(serverRes);

                            for (Object idx : routeSet) {
                                JSONObject attributes = (JSONObject) ((JSONObject) idx).get("attributes");
                                JSONArray paths = (JSONArray) ((JSONArray) ((JSONObject) ((JSONObject) idx).get("geometry")).get("paths")).get(0);

                                int fid         = Integer.valueOf(attributes.get("FID").toString());
                                double fieldLength = Double.valueOf(attributes.get("PMNTN_LT").toString());

                                HikingField hikingField = new HikingField(fid, fieldLength);
                                Log.d(TAG, "onResponse: fid: " + fid);
                                for (Object _idx : paths) {
                                    double lat = Double.valueOf(((JSONObject) _idx).get("lat").toString());
                                    double lng = Double.valueOf(((JSONObject) _idx).get("lng").toString());

                                    hikingField.addLatLng(new LatLng(lat, lng));

                                    allHikingRoute.offer(new LatLng(lat, lng));
                                    Log.d(TAG, "onResponse: lat: " + lat + ", lng: " + lng);
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

                    String memberNo    = String.valueOf(2);
                    String latitude     = String.valueOf(myCurrentLocation.getLatitude());
                    String longitude    = String.valueOf(myCurrentLocation.getLongitude());
                    String velocity     = String.valueOf(myCurrentLocation.getSpeed());
                    String distance     = String.valueOf(hikedDistance);

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
                                    double latitude = Double.valueOf(((JSONObject) idx).get("latitude").toString());
                                    double longitude = Double.valueOf(((JSONObject) idx).get("longitude").toString());
                                    int memberNo = Integer.valueOf(((JSONObject) idx).get("member_no").toString());

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
        userDataBox = (CardView) findViewById(R.id.userDataBox);     // Text View를 담을 부모 레이아웃.
        tvUserSpeed = (TextView) findViewById(R.id.userSpeed);       // 유저의 현재 속도.
        tvDistance = (TextView) findViewById(R.id.distance);        // 유저가 온 거리.
        tvArriveWhen = (TextView) findViewById(R.id.arriveWhen);      // 유저의 예상 도착시간.

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
                groupMemberList.putExtra("member_no", 1);
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

        tvUserSpeed.setText(String.valueOf(location.getSpeed()));
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
