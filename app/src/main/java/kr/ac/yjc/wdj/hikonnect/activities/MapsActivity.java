package kr.ac.yjc.wdj.hikonnect.activities;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.gun0912.tedpermission.PermissionListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.ac.yjc.wdj.hikonnect.AfterHikingActivity;
import kr.ac.yjc.wdj.hikonnect.BackPressClosHandler;
import kr.ac.yjc.wdj.hikonnect.Environment;
import kr.ac.yjc.wdj.hikonnect.HikingRecord;
import kr.ac.yjc.wdj.hikonnect.Locationmemo;
import kr.ac.yjc.wdj.hikonnect.Othersinfo;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.RecordListActivity;
import kr.ac.yjc.wdj.hikonnect.apis.HttpRequest.HttpRequestConnection;
import kr.ac.yjc.wdj.hikonnect.apis.LocationService;
import kr.ac.yjc.wdj.hikonnect.apis.PermissionManager;
import kr.ac.yjc.wdj.hikonnect.apis.walkietalkie.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 길이와 현재 fid를 담는 클래스입니다.
 * @author  Jungyu Choi
 * @author  bs Kwon
 * @since   2018-05
 */
class CrnidDistance {
    private double distance;
    private int currentid;

    public double getDistance() {
        return distance;
    }

    public int getCurrentid() {
        return currentid;
    }

    public CrnidDistance(double km, int cuid) {
        this.distance = km;
        this.currentid = cuid;
    }
}

//현재 위치와 fid를 담는 클래스입니다.
class LatLngCrnId/* implements ClusterItem */{

    private LatLng latLng;
    private int currentid;

    public LatLng getLatLng() {
        return latLng;
    }

    public int getCurrentid() {
        return currentid;
    }

    public LatLngCrnId(LatLng loca, int cuid) {
        this.currentid = cuid;
        this.latLng = loca;
    }


    public LatLng getPosition() {
        return latLng;
    }
}

//메인 클래스 입니다.
public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        OnClickListener,
        View.OnLongClickListener{

    private final String TAG = "HIKONNECT";

    private final static int DOUB_TO_STR_IDX = 5;

    // UI 변수
    private TextView                txtViewAltitude;
    private TextView                txtViewAvgSpeed;
    private TextView                txtViewRank;
    private EditText                edtTxtLocMemoContents;
    private EditText                edtTxtLocMemoTitle;
    private Button                  btnWriteReqLocMemo;
    private Button                  btnWriteCancelLocMemo;
    private Button                  btnChangeHikingState;
    private ImageView               imgViewLocMemo;
    private LinearLayout            layoutWriteLocMemo;
    private FloatingActionMenu      fabMenuWriteLocMemo;
    private FloatingActionButton    fabBtnLocMemoPic;
    private FloatingActionButton    fabBtnLocMemoNoPic;
    private FloatingActionButton    btnUpdateLocation;
    private FloatingActionButton    btnShowUserInfo;

    private ImageButton             btnToRecordList;    // 녹음 리스트 액티비티로 전환하는 버튼

    private LocationService         locationService;    // 위치 데이터 처리 클래스.
    // <-- 추가
    // UI 변수
    private CardView                userDataBox;    // 자신의 현재 정보를 보여줄 CardView
    private TextView                tvUserSpeed,    // 현재 속도 TextView (값 -> km/h 기준)
                                    tvDistance,     // 총 이동 거리 TextView (값 -> km 기준)
                                    tvArriveWhen;   // 예상 도착 시간 TextView (값 -> 시간 기준)
    private LinearLayout            drawerLayout;   // 무전 버튼을 넣어둘 레이아웃
    private Button                  btnSendRadio;  // 무전 시작 버튼
    //  -->

    //속도 관련 데이터
    private double              hikedDistance = 0;
    private double              crtAltitude;
    private LatLng              crtPos      = new LatLng(0, 0);
    private LatLng              past_pos    = new LatLng(0, 0);

    // GoogleMaps
    private GoogleMap                           mMap;
    private ArrayList<ArrayList<LatLngCrnId>>   hikingRoutes    = new ArrayList<>();
    private ArrayList<CrnidDistance>            crnidDistances  = new ArrayList<>();
    private ArrayList<Marker>                   markers         = new ArrayList<>();

    // 데이터 관련 변수
    private int                 myMemberNo;
    private int                 location_no;
    private int                 HIKING_STATUS   = 0;
    private String              image_path      = "File_path";

    private int                 positionuser    = 0;
    private int                 myCurrentFid    = 0;
    private String              http_response;
    private Float               distance;

    // HTTP 클래스.
    private OkHttpClient        httpClient = new OkHttpClient();

    // 구글맵 관련 데이터
//    private ClusterManager<LatLngCrnId>     myClusterManager;
    private double                          velocity    = 0;
    private int                             crtRoute;
    private int                             crtPosInRoute;

    // 상수
    private static final int PICK_FROM_CAMERA   = 0;
    private static final int PICK_FROM_ALBUM    = 1;

    // 핸들러
    private BackPressClosHandler    backPressClosHandler;

    // 퍼미션
    private PermissionManager       pManager;
    private PermissionListener      permissionlistener = null;

    // 기타
    private ContentValues           contentValues   = new ContentValues();
    private HttpRequestConnection   hrc             = new HttpRequestConnection();
    private AlertDialog.Builder     builder;
    private Bitmap                  image_bitmap;
    private Uri                     uri;
    private WalkieTalkie            walkieTalkie;   // 무전 객체
    private boolean                 isSendingNow;   // 현재 무전을 전송중인지

    private Timer       timer;
    private TimerTask   timerTask;

    private LinkedList<Location> userLocations = new LinkedList<>();

    //<-- 추가
    private boolean isdataBoxVisible    = false;    // 현재 데이터 박스 상태
    private boolean isRecBtnVisible     = false;    // 현재 녹음 버튼 상태
    // -->

    @Override
    public void onBackPressed() {
        if (layoutWriteLocMemo.getVisibility() == View.VISIBLE) {
            hidePopup();
        } else {
            backPressClosHandler.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_temp);

        final String user_id = getIntent().getExtras().getString("id");

        // 퍼미션 관리자 생성
        pManager = new PermissionManager(this);
        // 퍼미션 검사 수행
        pManager.checkPermissions();
        // 퍼미션 권한 요청
        pManager.requestPermissions();

        // 무전 객체 초기화
        walkieTalkie = new WalkieTalkie();

        // Location Service 초기화.
        locationService = new LocationService(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        backPressClosHandler = new BackPressClosHandler(MapsActivity.this);

//        txtViewAltitude         = (TextView)                findViewById(R.id.altitude_txtview);
//        txtViewAvgSpeed         = (TextView)                findViewById(R.id.avg_speed_txtview);
//        txtViewRank             = (TextView)                findViewById(R.id.rank_txtview);
        layoutWriteLocMemo      = (LinearLayout)            findViewById(R.id.imagelayout);
        edtTxtLocMemoTitle      = (EditText)                findViewById(R.id.title);
        imgViewLocMemo          = (ImageView)               findViewById(R.id.l_memo_img);
        edtTxtLocMemoContents   = (EditText)                findViewById(R.id.l_memo_contnets_edttxt);
        btnWriteCancelLocMemo   = (Button)                  findViewById(R.id.l_memo_cancel_btn);
        btnChangeHikingState    = (Button)                  findViewById(R.id.change_h_status_btn);
        btnWriteReqLocMemo      = (Button)                  findViewById(R.id.loc_memo_store_btn);
//        fabMenuWriteLocMemo     = (FloatingActionMenu)      findViewById(R.id.write_l_memo_fabmenu);
//        fabBtnLocMemoPic        = (FloatingActionButton)    findViewById(R.id.l_memo_with_pic_fabbtn);
//        fabBtnLocMemoNoPic      = (FloatingActionButton)    findViewById(R.id.l_memo_without_pic_fabbtn);
        btnUpdateLocation       = (FloatingActionButton)    findViewById(R.id.update_loc_btn);
        btnShowUserInfo         = (FloatingActionButton)    findViewById(R.id.show_member_list_btn);
        btnToRecordList         = (ImageButton)             findViewById(R.id.showRecordList);

        Log.d(TAG, "getMyMemNo: param: id:" + user_id);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    contentValues = new ContentValues();
                    contentValues.put("user_id", user_id);

                    http_response = hrc.request(Environment.LARAVEL_HIKONNECT_IP + "/api/getMemberNoByUserId", contentValues);
                    Log.i("http_response", http_response);

                    JSONArray jsonArray = new JSONArray(http_response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        myMemberNo = jsonObject.getInt("member_no");
                        Log.d("member_no", String.valueOf(myMemberNo));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "getMemNo: ", e);
                }
            }
        }).start();

        initUI();

//        btnShowMyInfo.setOnClickListener(this);
        btnChangeHikingState.setOnClickListener(this);
        fabBtnLocMemoNoPic.setOnClickListener(this);
        btnShowUserInfo.setOnClickListener(this);
        btnWriteCancelLocMemo.setOnClickListener(this);
        btnWriteReqLocMemo.setOnClickListener(this);
        btnUpdateLocation.setOnClickListener(this);
        fabBtnLocMemoPic.setOnClickListener(this);

        // 무전 버튼에 리스너 달기
        btnSendRadio.setOnClickListener(this);
        btnSendRadio.setOnLongClickListener(this);

        btnToRecordList.setOnClickListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                try {
                    //Get Image_path
                    uri = data.getData();
                    image_path = getRealPathFromURI(uri);
                    //이미지 데이터를 비트맵으로 받아온다.
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    //배치해놓은 ImageView에 set
                    imgViewLocMemo.setImageBitmap(image_bitmap);
                    Log.v("이미지", imgViewLocMemo.toString());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case PICK_FROM_CAMERA: {
                try {
                    //Get Image_path
                    uri = data.getData();
                    image_path = getRealPathFromURI(uri);
                    //이미지 데이터를 비트맵으로 받아온다.
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    //배치해놓은 ImageView에 set
                    imgViewLocMemo.setImageBitmap(image_bitmap);
                    layoutWriteLocMemo.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * UI 초기화
     */
    private void initUI() {
        userDataBox     = (CardView)        findViewById(R.id.userDataBox);
        tvUserSpeed     = (TextView)        findViewById(R.id.userSpeed);
        tvDistance      = (TextView)        findViewById(R.id.distance);
        tvArriveWhen    = (TextView)        findViewById(R.id.arriveWhen);
        drawerLayout    = (LinearLayout)    findViewById(R.id.drawer);
        btnSendRadio    = (Button)          findViewById(R.id.sendRecordData);

        // drawerLayout 을 클릭하면 무전 버튼 가시화
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (isdataBoxVisible) {
                    userDataBox.setVisibility(View.GONE);
                } else {
                    userDataBox.setVisibility(View.VISIBLE);
                }
                isdataBoxVisible = !isdataBoxVisible;
            }
        });
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                hikedDistance = 0;

                updateLocation();
                updateCurrentFID();

                Location locationStartEnd = new Location("point A");
                ArrayList<LatLngCrnId> rolypoly = new ArrayList<>();
                rolypoly = hikingRoutes.get(hikingRoutes.size() - 1);

                if (HIKING_STATUS == 0) {
                    locationStartEnd.setLatitude(hikingRoutes.get(0).get(0).getLatLng().latitude);
                    locationStartEnd.setLongitude(hikingRoutes.get(0).get(0).getLatLng().longitude);
                } else if (HIKING_STATUS == 1) {
                    // 무전 받아오기 시작
                    walkieTalkie.receiveStart();

                    for(int idx = 0; idx < crtPosInRoute; idx++) {
                        Location _location1 = new Location("loc1");
                        _location1.setLatitude(hikingRoutes.get(crtRoute).get(idx).getLatLng().latitude);
                        _location1.setLongitude(hikingRoutes.get(crtRoute).get(idx).getLatLng().longitude);

                        Location _location2 = new Location("loc2");
                        _location2.setLatitude(hikingRoutes.get(crtRoute).get(idx + 1).getLatLng().latitude);
                        _location2.setLongitude(hikingRoutes.get(crtRoute).get(idx + 1).getLatLng().longitude);

                        hikedDistance += _location1.distanceTo(_location2);
                    }

                    for (int idx = 0; idx < myCurrentFid; idx++) {
                        hikedDistance += crnidDistances.get(idx).getDistance();
                    }

                    locationStartEnd.setLatitude(rolypoly.get(rolypoly.size() - 1).getLatLng().latitude);
                    locationStartEnd.setLongitude(rolypoly.get(rolypoly.size() - 1).getLatLng().longitude);
                }

                hrc = new HttpRequestConnection();
                contentValues = new ContentValues();

                contentValues.put("distance",   hikedDistance);
                contentValues.put("member_no",  myMemberNo);
                contentValues.put("latitude",   crtPos.latitude);
                contentValues.put("longitude",  crtPos.longitude);
                contentValues.put("velocity",   velocity);

                http_response = hrc.request(Environment.LARAVEL_HIKONNECT_IP + "/api/storesend", contentValues);

                Location locationB = new Location("point B");

                locationB.setLatitude(crtPos.latitude);
                locationB.setLongitude(crtPos.longitude);

                distance = locationStartEnd.distanceTo(locationB);

                if (distance < 50) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnChangeHikingState.setVisibility(View.VISIBLE);
                        }
                    });
                }

                markers = new ArrayList<>();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int a = 0; a < hikingRoutes.size(); a++) {
                                for (int b = 0; b < hikingRoutes.get(a).size() - 1; b++) {
                                    mMap.addPolyline(new PolylineOptions()
                                            .add(hikingRoutes.get(a).get(b).getLatLng(), hikingRoutes.get(a).get(b + 1).getLatLng())
                                            .color(Color.RED)
                                            .width(10));
                                }
                            }

                            for (Marker marker : markers) {
                                marker.remove();
                            }

                            JSONObject first        = new JSONObject(http_response);
                            JSONArray jsonArray     = first.getJSONArray("members");
                            JSONArray jsonArray2    = first.getJSONArray("location_memos");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                positionuser = jsonObject.getInt("member_no");
                                double lat = jsonObject.getDouble("latitude");
                                double lng = jsonObject.getDouble("longitude");

                                final LatLng user_pos = new LatLng(lat, lng);

                                if (positionuser == myMemberNo) {
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(user_pos)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.me1)));
                                    marker.setTag(positionuser);
                                    marker.setSnippet("people");
                                    markers.add(marker);
                                } else {
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(user_pos)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_walk_black_24dp)));
                                    marker.setTag(positionuser);
                                    marker.setSnippet("people");
                                    markers.add(marker);
                                }
                            }

                            for (int j = 0; j < jsonArray2.length(); j++) {
                                JSONObject jsonObject = jsonArray2.getJSONObject(j);

                                location_no = jsonObject.getInt("no");
                                double lat_location = jsonObject.getDouble("latitude");
                                double lng_location = jsonObject.getDouble("longitude");

                                final LatLng nl2 = new LatLng(lat_location, lng_location);

                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(nl2)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bookmark_black_24dp)));
                                marker.setTag(location_no);
                                marker.setSnippet("locationmemo");
                                markers.add(marker);

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        double lat;
                                        double lng;
                                        if (marker.getSnippet().equals("people")) {
                                            Intent intent1 = new Intent(MapsActivity.this, HikingRecord.class);

                                            lat = marker.getPosition().latitude;
                                            lng = marker.getPosition().longitude;

                                            intent1.putExtra("member_no", (Integer) marker.getTag());
                                            intent1.putExtra("latitude", lat);
                                            intent1.putExtra("longitude", lng);
                                            startActivity(intent1);
                                        } else if (marker.getSnippet().equals("locationmemo")) {
                                            Intent intent1 = new Intent(MapsActivity.this, Locationmemo.class);

                                            lat = marker.getPosition().latitude;
                                            lng = marker.getPosition().longitude;

                                            intent1.putExtra("location_no", location_no);
                                            intent1.putExtra("latitude", lat);
                                            intent1.putExtra("longitude", lng);
                                            startActivity(intent1);
                                        }
                                        return false;
                                    }
                                });
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Paint marker error", e);
                        }
                    }
                });
            }
        };

//        myClusterManager.addItem();

        requestGet(Environment.NODE_HIKONNECT_IP + "/dummy/school", null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Connection Failure: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resultr = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(resultr);

                    for (int _idx = 0; _idx < jsonArray.length(); _idx++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(_idx);

                        JSONArray paths = jsonObject.getJSONObject("geometry").getJSONArray("paths");
                        JSONObject attr = jsonObject.getJSONObject("attributes");

                        double  fieldLength     = attr.getDouble("PMNTN_LT");
                        int     fid             = attr.getInt("FID");

                        crnidDistances.add(new CrnidDistance(fieldLength, fid));

                        for (int __idx = 0; __idx < paths.length(); __idx++) {
                            ArrayList<LatLngCrnId> polyline_path = new ArrayList<>();
                            JSONArray __path = paths.getJSONArray(__idx);
                            for (int k = 0; k < __path.length(); k++) {
                                JSONObject ___pos = __path.getJSONObject(k);
                                double lat = ___pos.getDouble("lat");
                                double lng = ___pos.getDouble("lng");
                                LatLngCrnId latLngCrnId = new LatLngCrnId(new LatLng(lat, lng), fid);
                                polyline_path.add(latLngCrnId);
                            }
                            hikingRoutes.add(polyline_path);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int a = 0; a < hikingRoutes.size(); a++) {
                                    for (int b = 0; b < hikingRoutes.get(a).size() - 1; b++) {
                                        mMap.addPolyline(new PolylineOptions()
                                                .add(hikingRoutes.get(a).get(b).getLatLng(), hikingRoutes.get(a).get(b + 1).getLatLng())
                                                .color(Color.RED)
                                                .width(10));
                                    }
                                }
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(hikingRoutes.get(0).get(0).getLatLng()));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hikingRoutes.get(0).get(0).getLatLng(), 19));
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                timer.schedule(timerTask, 0, 5000);
            }
        });
    }

    public void updateCurrentFID() {

        Location locCrtPos = new Location("crtPos");
        locCrtPos.setLatitude(crtPos.latitude);
        locCrtPos.setLongitude(crtPos.longitude);

        float hikedPeriod = (float) 0.0;

        Log.d(TAG, "userLocations Size: " + userLocations.size());
        userLocations.offer(locCrtPos);
        if (userLocations.size() > 10) {
            userLocations.poll();
        }
        if (userLocations.size() > 1){
            for (Location _loc : userLocations) {
                int idx = userLocations.indexOf(_loc);
                if (idx == 0) continue;

                hikedPeriod += _loc.distanceTo(userLocations.get(idx - 1));
            }
        }

        velocity = (hikedPeriod / userLocations.size()) * 1000;


        double  distanceWithPos = Double.POSITIVE_INFINITY;
        Location routeStartPoint = new Location("routeStartPoint");

        try {
            for (ArrayList<LatLngCrnId> route : hikingRoutes) {
                int idx = hikingRoutes.indexOf(route);

                if (idx == myCurrentFid -1 || idx == myCurrentFid || idx == myCurrentFid + 1) {
                    for (LatLngCrnId __var : route) {
                        routeStartPoint.setLatitude(__var.getLatLng().latitude);
                        routeStartPoint.setLongitude(__var.getLatLng().longitude);

                        double distance = routeStartPoint.distanceTo(locCrtPos);

                        if (distance < distanceWithPos) {
                            distanceWithPos = distance;
                            myCurrentFid = hikingRoutes.indexOf(route);
                            crtPosInRoute = route.indexOf(__var);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "get current fid: ", e);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO 바꿔주세영
//                txtViewAvgSpeed.setText(String.valueOf(velocity));
//                txtViewAltitude.setText(String.valueOf(crtAltitude));
//                txtViewRank.setText();
            }
        });
    }

    public void showAlertDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS ON");
        builder.setMessage("GPS가 켜져있지 않습니다.");
        builder.create();
        builder.setPositiveButton("설정",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Settings();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivityForResult(intent, 1);
                        onRestart();
                    }
                });
        builder.setNegativeButton("취소", null);
        builder.show();

        Log.v("GPS on", "나머지 설정");
    }

    public void updateLocation() {
        locationService.setLocationListener(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                double alt = location.getAltitude();

                past_pos = crtPos;
                crtPos = new LatLng(lat, lng);
                crtAltitude = alt;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });
    }

    private void hidePopup() {
        layoutWriteLocMemo.setVisibility(View.INVISIBLE);
        edtTxtLocMemoContents.setText("");
        imgViewLocMemo.setImageResource(R.color.colorPrimary);
        return;
    }

    //Get Image's RealPath
    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }

    // OkHttp Get request.
    public void requestGet(String url, String searchKey, Callback callback) {

        //URL에 포함할 Query문 작성 Name&Value
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addEncodedQueryParameter("searchKey", searchKey);
        String requestUrl = urlBuilder.build().toString();

        //Query문이 들어간 URL을 토대로 Request 생성
        Request request = new Request.Builder().url(requestUrl).build();

        //만들어진 Request를 서버로 요청할 Client 생성
        //Callback을 통해 비동기 방식으로 통신을 하여 서버로부터 받은 응답을 어떻게 처리 할 지 정의함
        httpClient.newCall(request).enqueue(callback);
    }

    // OkHttp Post
    public void requestPost(String url, String header, String body, Callback callback) {

        //Request Body에 서버에 보낼 데이터 작성
        RequestBody requestBody = new FormBody.Builder().add("header", header).add("body", body).build();

        //작성한 Request Body와 데이터를 보낼 url을 Request에 붙임
        Request request = new Request.Builder().url(url).post(requestBody).build();

        //request를 Client에 세팅하고 Server로 부터 온 Response를 처리할 Callback 작성
        httpClient.newCall(request).enqueue(callback);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.loc_memo_store_btn:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ContentValues contentValues2 = new ContentValues();

//                        contentValues2.put("schedule_no",    );
//                        contentValues2.put("hiking_group",   );
//                        contentValues2.put("title",          );
//                        contentValues2.put("writer",         );
//                        contentValues2.put("picture",        );
//                        contentValues2.put("latitude",       );
//                        contentValues2.put("longitude",      );
//
//                        contentValues2.put("member_no", myMemberNo);
//                        contentValues2.put("lat", crtPos.latitude);
//                        contentValues2.put("lng", crtPos.longitude);
//                        contentValues2.put("edtTxtLocMemoTitle", edtTxtLocMemoTitle.getText().toString());
//                        contentValues2.put("edtTxtLocMemoContents", edtTxtLocMemoContents.getText().toString());
//                        contentValues2.put("image_path", image_path);
//
//                        http_response = hrc.request(Environment.LARAVEL_HIKONNECT_IP + "/api/storeLocationMemo", contentValues2);
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                txtViewSysMsg.setText("위치 메모 등록 완료.");
//                                hidePopup();
//                            }
//                        });
                    }
                }).start();
                break;
            case R.id.l_memo_cancel_btn:
                hidePopup();
                break;
            case R.id.change_h_status_btn:
                switch (HIKING_STATUS) {
                    case 0:
                        HIKING_STATUS = 1;
                        btnChangeHikingState.setVisibility(View.INVISIBLE);
                        btnChangeHikingState.setText("등산완료");
                        break;
                    case 1:
                        btnChangeHikingState.setVisibility(View.INVISIBLE);
                        HIKING_STATUS = 2;
                        Intent afterHikingIntent = new Intent(getBaseContext(), AfterHikingActivity.class);
                        afterHikingIntent.putExtra("member_no", myMemberNo);
                        startActivity(afterHikingIntent);
                        break;
                    default:
                        break;
                }
                break;
            /*case R.id.l_memo_with_pic_fabbtn:
                fabMenuWriteLocMemo.close(true);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);

                alertDialog.setPositiveButton("사진 등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, PICK_FROM_ALBUM);
                        layoutWriteLocMemo.setVisibility(View.VISIBLE);
                    }
                });
                alertDialog.setNeutralButton("사진 찍기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
                        try {
                            intent.putExtra("return-data", true);
                            startActivityForResult(Intent.createChooser(intent,
                                    "Complete action using"), PICK_FROM_CAMERA);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
                alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        layoutWriteLocMemo.setVisibility(View.VISIBLE);
                        imgViewLocMemo.setVisibility(View.INVISIBLE);
                    }
                });
                alertDialog.show();
                break;*/
            /*case R.id.l_memo_without_pic_fabbtn:
                fabMenuWriteLocMemo.close(true);
                AlertDialog.Builder ad = new AlertDialog.Builder(MapsActivity.this);

                ad.setPositiveButton("글쓰기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imgViewLocMemo.setVisibility(View.INVISIBLE);
                        layoutWriteLocMemo.setVisibility(View.VISIBLE);
                    }
                });
                ad.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // todo 취소 버튼 누를 때 동작 정의.
                    }
                });
                ad.show();
                break;
            case R.id.update_loc_btn:
                updateLocation();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(crtPos));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(crtPos, 19));
                break;
            case R.id.show_member_list_btn:
                Intent intent = new Intent(MapsActivity.this, Othersinfo.class);
                intent.putExtra("my_num", myMemberNo);
                startActivity(intent);
                break;*/
            /*case R.id.show_my_info_btn:
            Intent intent1 = new Intent(MapsActivity.this, HikingRecord.class);
                intent1.putExtra("member_no", myMemberNo);
                startActivity(intent1);
                break;*/
            case R.id.sendRecordData:
                if (isSendingNow) {
                   isSendingNow = false;
                   walkieTalkie.sendEnd();
                   Toast.makeText(getBaseContext(), "무전 종료합니다", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.showRecordList:
                Intent recordIntent = new Intent(getBaseContext(), RecordListActivity.class);
                startActivity(recordIntent);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!isSendingNow) {
            isSendingNow = true;
            walkieTalkie.sendStart();
            Toast.makeText(getBaseContext(), "무전 시작합니다", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public String double2String(double num) {
        String str = String.valueOf(num);

        int dotIdx = str.indexOf(".");

        str = str.substring(dotIdx + 2);

        return str;
    }
}
