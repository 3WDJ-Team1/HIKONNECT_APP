package kr.ac.yjc.wdj.hikonnect.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import kr.ac.yjc.wdj.hikonnect.AfterHikingActivity;
import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.Locationmemo;
import kr.ac.yjc.wdj.hikonnect.Othersinfo;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.RecordListActivity;
import kr.ac.yjc.wdj.hikonnect.adapters.HikingMemberListAdapter;
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

/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Draws up to four other drawables.
 */
class MultiDrawable extends Drawable {

    private final List<Drawable> mDrawables;

    public MultiDrawable(List<Drawable> drawables) {
        mDrawables = drawables;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mDrawables.size() == 1) {
            mDrawables.get(0).draw(canvas);
            return;
        }
        int width = getBounds().width();
        int height = getBounds().height();

        canvas.save();
        canvas.clipRect(0, 0, width, height);

        if (mDrawables.size() == 2 || mDrawables.size() == 3) {
            // Paint left half
            canvas.save();
            canvas.clipRect(0, 0, width / 2, height);
            canvas.translate(-width / 4, 0);
            mDrawables.get(0).draw(canvas);
            canvas.restore();
        }
        if (mDrawables.size() == 2) {
            // Paint right half
            canvas.save();
            canvas.clipRect(width / 2, 0, width, height);
            canvas.translate(width / 4, 0);
            mDrawables.get(1).draw(canvas);
            canvas.restore();
        } else {
            // Paint top right
            canvas.save();
            canvas.scale(.5f, .5f);
            canvas.translate(width, 0);
            mDrawables.get(1).draw(canvas);

            // Paint bottom right
            canvas.translate(0, height);
            mDrawables.get(2).draw(canvas);
            canvas.restore();
        }

        if (mDrawables.size() >= 4) {
            // Paint top left
            canvas.save();
            canvas.scale(.5f, .5f);
            mDrawables.get(0).draw(canvas);

            // Paint bottom left
            canvas.translate(0, height);
            mDrawables.get(3).draw(canvas);
            canvas.restore();
        }

        canvas.restore();
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @SuppressLint("WrongConstant")
    @Override
    public int getOpacity() {
        return 0;
    }
}

class Member extends MapItem implements ClusterItem {

    int     member_no;
    String  userID;
    String  nickname;
    Bitmap  profileImg;
    String  hikingStartedAt;
    double  avgSpeed;
    int     rank;

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
    String title;
    String contents;
    Bitmap picture;

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

    public void setLocation(LatLng location) {
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

    HikingField(int fid, double fieldLength) {
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

    private final String                TAG                 = "HIKONNECT";
    private final int                   GALLERY_CODE        = 1110;
    private final int                   CAMERA_CODE         = 1111;
    private final int                   REQUEST_TAKE_PHOTO  = 1112;

    private int                         myMemberNo = 2;

    // 지도, 위치.
    private GoogleMap                   gMap;
    private Location                    myCurrentLocation;
    private double                      hikedDistance = 0.0;

    // HTTP 통신.
    private OkHttpClient                okHttpClient = new OkHttpClient();

    // 위치 메모
    private Uri photoUri;
    private String currentPhotoPath;    //실제 사진 파일 경로
    String mImageCaptureName;           //이미지 이름

    // 등산 데이터.
    private ArrayList<HikingField>      hikingFields        = new ArrayList<>();
    private int myHikingState = 0;
    private int                         currentFID          = 0;
    private int                         currentPointInFID   = 0;
    private double                      wholeDistance       = 0.0;

    private ArrayList<LatLng>           allHikingRoute = new ArrayList<>();
    private HashMap<Integer, MapItem>   mapItems = new HashMap<>();
    private ClusterManager<MapItem>     myClusterManager;
    private MarkerManager               myMarkerManager;
    private ArrayList<Marker>           markers = new ArrayList<>();
    private Marker                      myMarker;

    // [1] UI 클래스
    // [1.1] 현재 상태 표시.
    private CardView            userDataBox;    // 자신의 현재 정보를 보여줄 CardView
    private TextView            tvUserSpeed;    // 현재 속도 TextView (값 -> km/h 기준)
    private TextView            tvDistance;     // 총 이동 거리 TextView (값 -> km 기준)
    private TextView            tvArriveWhen;   // 예상 도착 시간 TextView (값 -> 시간 기준)
    private TextView            tvUserRank;
    private ProgressBar         progressBar;
    private TextView            tvHikingProgress;

    private CardView            otherUserDataBox;
    private TextView            TvOtherUserNickname;
    private TextView            tvOtherUserSpeed;
    private TextView            tvOtherUserDistance;
    private TextView            tvOtherUserArriveWhen;
    private TextView            tvOtherUserRank;

    // [1.2] 위치 메모.
    private FloatingActionButton    fabWriteLocationMemo;

    // [1.2.1] 위치 메모 작성 팝업.
    private LinearLayout    linearLayoutLocationMemo;
    private EditText        edtTextLMemoTitle;
    private EditText        edtTextLMemoContent;
    private ImageView       imgViewLMemoImg;
    private Button          btnLMemoSendReq;
    private Button          btnLMemoCancel;
    private Uri             imageUri;       // 사진 Uri.

    // [1.3] 자기 위치 갱신 버튼.
    private FloatingActionButton    fabUpdateMyLocation;

    // [1.4] 그룹 맴버 리스트 버튼
    private FloatingActionButton    fabShowMemberList;

    // [1.6] 무전
    private LinearLayout    drawerLayout;       // 무전 버튼을 넣어둘 레이아웃
    private Button          btnSendRadio;       // 무전 시작 버튼
    private ImageButton     showRecordList;     // 음성 녹음 리스트.

    // [1.7] 등산 상태 변경 버튼
    private Button          btnChangeHikingState;   // 등산 시작, 등산 끝 버튼.

    // [3] 상태 저장 변수.
    private int         hikingProgress          = 0;

    private SharedPreferences   pref;

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
                mImageView.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_baseline_bookmarks_24px));
                Bitmap icon = mIconGenerator.makeIcon();
                markerOptions
                        .icon(BitmapDescriptorFactory.fromBitmap(icon));
            }
            if (item instanceof Member) {

                if (((Member) item).member_no == myMemberNo) {
                    markerOptions.visible(false);
                } else {
                    if (((Member) item).profileImg == null) {
                        mImageView.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.default_profile));
                        Bitmap icon = mIconGenerator.makeIcon();
                        markerOptions
                                .icon(BitmapDescriptorFactory.fromBitmap(icon));
                    } else  {
                        Bitmap profileImg = ((Member) item).profileImg;
                        profileImg = Bitmap.createScaledBitmap(profileImg, 150, 150, true);
                        mImageView.setImageBitmap(profileImg);
                        Bitmap icon = mIconGenerator.makeIcon();
                        markerOptions
                                .icon(BitmapDescriptorFactory.fromBitmap(icon));
                    }
                }
            }
        }

        @Override
        protected void onClusterItemRendered(MapItem clusterItem, Marker marker) {
            marker.setTag(clusterItem);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = getSharedPreferences("loginData", MODE_PRIVATE);

        PermissionManager permissionManager = new PermissionManager(this);
        permissionManager.requestPermissions();

        setContentView(R.layout.activity_maps_temp);

        Intent intent = getIntent();
//        String userid = "admi";
        String userid = intent.getStringExtra("id");
        getMemberNoByUserID(userid);

        // [1] GoogleMaps Fragment 불러오기.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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
                public boolean onClusterItemClick(final MapItem mapItem) {
                    if (mapItem instanceof LocationMemo) {
                        Log.d(TAG, "onClusterItemClick: no: " + ((LocationMemo) mapItem).no);

                        Intent intent1 = new Intent(MapsActivityTemp.this, Locationmemo.class);
                        intent1.putExtra("location_no", ((LocationMemo) mapItem).no);
                        intent1.putExtra("latitude", mapItem.getPosition().latitude);
                        intent1.putExtra("longitude", mapItem.getPosition().longitude);
                        startActivity(intent1);
                    } else if (mapItem instanceof Member) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TvOtherUserNickname.setText("");
                                tvOtherUserSpeed.setText("0.0");
                                tvOtherUserDistance.setText("0.00");
                                tvOtherUserArriveWhen.setText("0.0");
                                tvOtherUserRank.setText("0");
                            }
                        });
                        new Thread(new Runnable() {
                            String nickname;
                            double distance;
                            double velocity;
                            int rank;

                            @Override
                            public void run() {
                                try {
                                    HttpUrl.Builder urlBuilder = HttpUrl
                                            .parse(Environments.LARAVEL_HIKONNECT_IP + "/api/getMemberDetail")
                                            .newBuilder();

                                    String reqUrl = urlBuilder.build().toString();

                                    String member_no = String.valueOf(((Member) mapItem).member_no);
                                    RequestBody reqBody = new FormBody.Builder()
                                            .add("member_no", member_no)
                                            .build();

                                    Request req = new Request.Builder()
                                            .url(reqUrl)
                                            .post(reqBody)
                                            .build();

                                    Response response = okHttpClient.newCall(req).execute();

                                    JSONParser parser = new JSONParser();

                                    JSONObject result = (JSONObject) ((JSONArray) parser.parse(response.body().string())).get(0);

                                    nickname    = String.valueOf(result.get("nickname"));
                                    distance    = Double.valueOf(result.get("distance").toString());
                                    distance    = Math.round(Math.abs(distance - hikedDistance) * 100d) / 100d;
                                    velocity    = Double.valueOf(result.get("velocity").toString());
                                    rank        = Integer.valueOf(result.get("rank").toString());

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            TvOtherUserNickname.setText(nickname);
                                            tvOtherUserSpeed.setText(String.valueOf(velocity));
                                            tvOtherUserDistance.setText(String.valueOf(distance));
                                            tvOtherUserRank.setText(String.valueOf(rank));
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.e(TAG, "onClusterItemClick: ", e);
                                }
                            }
                        }).start();

                        otherUserDataBox.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onClusterItemClick: member_no: " + ((Member) mapItem).member_no);
                    }

                    return false;
                }
            });

            myMarkerManager = myClusterManager.getMarkerManager();

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

                    updateHikingInfo();
                    updateCurrentFID();
                    paintMarkers();
                    updateHikedDistance();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                switch (myHikingState) {
                                    case 0:
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        break;
                                }
                                if (getDisToStartPoint() < 50) {
                                    if (myHikingState == 0 || myHikingState == 1)
                                        btnChangeHikingState.setVisibility(View.VISIBLE);
                                } else {
                                    btnChangeHikingState.setVisibility(View.GONE);
                                }

                                if (myCurrentLocation != null) {
                                    myMarker.setPosition(new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude()));
                                }

                                if (myHikingState != 0) {
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
            new Thread(new Runnable() {
                int rank;
                @Override
                public void run() {
                    try {

                        HttpUrl httpUrl = HttpUrl
                                .parse(Environments.LARAVEL_HIKONNECT_IP + "/api/getMemberDetail")
                                .newBuilder()
                                .build();

                        RequestBody reqBody = new FormBody.Builder()
                                .add("member_no", String.valueOf(myMemberNo))
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
                    } catch (Exception e) {
                        Log.e(TAG, "get my info", e);
                    }
                }
            }).start();

            userDataBox.setVisibility(View.VISIBLE);
            isdataBoxVisible = true;
        }
        otherUserDataBox.setVisibility(View.GONE);
    }

    private void requestHikingRoute() throws NullPointerException {

        HttpUrl.Builder urlBuilder = HttpUrl
                .parse(Environments.NODE_HIKONNECT_IP + "/dummy/school")
                .newBuilder();

        String reqUrl = urlBuilder.build().toString();

        Request req = new Request.Builder()
                .url(reqUrl)
                .build();

        okHttpClient
                .newCall(req)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e(TAG, "Request Hiking Route: ", e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MapsActivityTemp.this, "연결 상태 불량.\n네트워크 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        try {
                            JSONParser parser = new JSONParser();
                            String serverRes = response.body().string();


                            JSONArray routeSet = (JSONArray) parser.parse(serverRes);
                            wholeDistance = 0.0;

                            for (Object idx : routeSet) {
                                JSONObject attributes = (JSONObject) ((JSONObject) idx).get("attributes");
                                JSONArray paths = (JSONArray) ((JSONArray) ((JSONObject) ((JSONObject) idx).get("geometry")).get("paths")).get(0);

                                int fid             = Integer.valueOf(attributes.get("FID").toString());
                                double fieldLength  = Double.valueOf(attributes.get("PMNTN_LT").toString());

                                wholeDistance += fieldLength;

                                HikingField hikingField = new HikingField(fid, fieldLength);
                                for (Object _idx : paths) {
                                    double lat = Double.valueOf(((JSONObject) _idx).get("lat").toString());
                                    double lng = Double.valueOf(((JSONObject) _idx).get("lng").toString());

                                    hikingField.addLatLng(new LatLng(lat, lng));

                                    if (allHikingRoute.size() > 0) {
                                        if (allHikingRoute.get(allHikingRoute.size() - 1).equals(new LatLng(lat, lng))) {
                                            continue;
                                        }
                                    }
                                    allHikingRoute.add(new LatLng(lat, lng));
                                }

                                hikingFields.add(hikingField);
                            }

                            paintHikingRoute();
                        } catch (NullPointerException npe) {
                            Log.e(TAG, "onResponse: ", npe);
                        } catch (ParseException pse) {
                            Log.e(TAG, "Parse Server Res: ", pse);
                        }
                    }
                });
    }

    private void getMemberNoByUserID(String userID) {

        HttpUrl.Builder urlBuilder = HttpUrl
                .parse(Environments.LARAVEL_HIKONNECT_IP + "/api/getMemberNoByUserId")
                .newBuilder();

        final String reqUrl = urlBuilder.build().toString();

        RequestBody requestBody = new FormBody.Builder()
                .add("user_id", userID)
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

                    int memNo       = Integer.valueOf(((JSONObject)result.get(0)).get("member_no").toString());
                    int hikingState = Integer.valueOf(((JSONObject)result.get(0)).get("hiking_state").toString());

                    myMemberNo = memNo;
                    myHikingState = hikingState;

                } catch (Exception e) {
                    Log.e(TAG, "onResponse: ", e);
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
                            marker.setPosition(((MapItem)marker.getTag()).getPosition());
                        }

                        myClusterManager.cluster();
                    }
                });
            }
        }).start();
    }

    private void updateHikingInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    HttpUrl.Builder urlBuilder = HttpUrl
                            .parse(Environments.LARAVEL_HIKONNECT_IP + "/api/storesend")
                            .newBuilder();

                    final String reqUrl = urlBuilder.build().toString();

                    String memberNo     = String.valueOf(myMemberNo);
                    String latitude     = String.valueOf(0);
                    String longitude    = String.valueOf(0);
                    String velocity     = String.valueOf(0);
                    String distance     = String.valueOf(0);

                    if (myHikingState != 0) {
                        latitude    = String.valueOf(myCurrentLocation.getLatitude());
                        longitude   = String.valueOf(myCurrentLocation.getLongitude());
                        velocity    = String.valueOf(myCurrentLocation.getSpeed() * 3.6);
                        distance    = String.valueOf(hikedDistance);
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
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.e(TAG, "Http updateHikingInfo: ", e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MapsActivityTemp.this, "연결 상태 불량.\n네트워크 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
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
            if (myHikingState == 0 ) {
                startPoint = allHikingRoute.get(0);
            } else if (myHikingState == 1){
                startPoint = allHikingRoute.get(allHikingRoute.size() -1 );
            }

           Location startPointLoc = new Location("startPoint");
           startPointLoc.setLatitude(startPoint.latitude);
           startPointLoc.setLongitude(startPoint.longitude);

           return startPointLoc.distanceTo(myCurrentLocation);

        } catch (Exception e) {
            Log.e(TAG, "getDisToStartPoint: ", e);
        }
        return 0.0d;
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

                hikedDistance += hikingFields.get(idx - 1).getFieldLength() * 100;
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

            hikedDistance = Math.round(hikedDistance) / 100d;

            hikingProgress = (int) (hikedDistance / wholeDistance * 100);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvHikingProgress.setText(String.valueOf(hikingProgress));
                    progressBar.setProgress(hikingProgress);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "updateHikedDistance: ", e);
        }
    }

    private void updateHikingState() {
        try {
            HttpUrl httpUrl = HttpUrl.parse(Environments.LARAVEL_HIKONNECT_IP + "/api/updateHikingState").newBuilder().build();

            RequestBody reqBody = new FormBody.Builder()
                    .add("member_no", String.valueOf(myHikingState))
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

                    Log.d(TAG, "Update Hiking State: res: " + result);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "updateHikingState: ", e);
        }
    }

    private void initializeUI() {
        // [1] Layout 초기화.
        // [1.1] 상태 표시 레이아웃.
        userDataBox                 = (CardView) findViewById(R.id.userDataBox);     // Text View를 담을 부모 레이아웃.
        tvUserSpeed                 = (TextView) findViewById(R.id.userSpeed);       // 유저의 현재 속도.
        tvDistance                  = (TextView) findViewById(R.id.distance);        // 유저가 온 거리.
        tvArriveWhen                = (TextView) findViewById(R.id.arriveWhen);      // 유저의 예상 도착시간.
        tvUserRank                  = (TextView) findViewById(R.id.userRank);
        progressBar                 = (ProgressBar) findViewById(R.id.seekBar);
        tvHikingProgress            = (TextView) findViewById(R.id.seekBarProgress);

        otherUserDataBox            = (CardView) findViewById(R.id.otherUserDataBox);
        TvOtherUserNickname         = (TextView) findViewById(R.id.otherUserNickname);
        tvOtherUserSpeed            = (TextView) findViewById(R.id.otherUserSpeed);
        tvOtherUserDistance         = (TextView) findViewById(R.id.otherUserDistance);
        tvOtherUserArriveWhen       = (TextView) findViewById(R.id.otherUserArriveWhen);
        tvOtherUserRank             = (TextView) findViewById(R.id.otherUserRank);

        // [1.2] 위치 메모.
        fabWriteLocationMemo        = (FloatingActionButton) findViewById(R.id.write_location_memo_btn);

        // [1.2.1] 위치 메모 작성 팝업.
        linearLayoutLocationMemo    = (LinearLayout) findViewById(R.id.imagelayout);
        edtTextLMemoTitle           = (EditText) findViewById(R.id.l_memo_title);
        edtTextLMemoContent         = (EditText) findViewById(R.id.l_memo_contnets_edttxt);
        imgViewLMemoImg             = (ImageView) findViewById(R.id.l_memo_img);
        btnLMemoCancel              = (Button)  findViewById(R.id.l_memo_cancel_btn);
        btnLMemoSendReq             = (Button) findViewById(R.id.loc_memo_store_btn);

        // [1.3] 자기 위치 갱신 버튼.
        fabUpdateMyLocation         = (FloatingActionButton) findViewById(R.id.update_loc_btn);

        // [1.4] 그룹 맴버 리스트 버튼
        fabShowMemberList           = (FloatingActionButton) findViewById(R.id.show_member_list_btn);

        // [1.5] 무전 레이아웃.
        drawerLayout                = (LinearLayout) findViewById(R.id.drawer);          // Hidden 레이아웃 활성/비활성 버튼.
        btnSendRadio                = (Button) findViewById(R.id.sendRecordData);  // 무전 보내기 버튼
        showRecordList              = (ImageButton) findViewById(R.id.showRecordList);

        // [1.6] 등산 상태 변경 버튼
        btnChangeHikingState        = (Button) findViewById(R.id.change_h_status_btn);

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

                BitmapDrawable bitmapDrawable = (BitmapDrawable) imgViewLMemoImg.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] bytesImg = bos.toByteArray();

                HttpUrl httpUrl = HttpUrl
                        .parse(Environments.NODE_HIKONNECT_IP + "/location/regLocation")
                        .newBuilder()
                        .build();

                RequestBody reqBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("schedule_no",     "1")
                        .addFormDataPart("hiking_group",    "57a89f8f-4dc8-11e8-82cb-42010a9200af")
                        .addFormDataPart("title",           edtTextLMemoTitle.getText().toString())
                        .addFormDataPart("content",         edtTextLMemoContent.getText().toString())
                        .addFormDataPart("writer",          pref.getString("user_id", ""))
                        .addFormDataPart("picture",         "true")
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
                startActivityForResult(groupMemberList, HikingMemberListAdapter.REQUEST_CODE);
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
                switch (myHikingState) {
                    case 0:     // 등산 전
                        myHikingState = 1;
                        btnChangeHikingState.setVisibility(View.GONE);
                        btnChangeHikingState.setText("산행 종료");
                        userDataBox.setVisibility(View.VISIBLE);
                        updateHikingState();
                        break;
                    case 1:     // 등산 중
                        myHikingState = 2;
                        updateHikingState();
                        btnChangeHikingState.setText("산행!!");
                    case 2:     // 등산 후
                        Intent afterHikingIntent = new Intent(getBaseContext(), AfterHikingActivity.class);
                        afterHikingIntent.putExtra("member_no", myMemberNo);
                        startActivity(afterHikingIntent);
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

    private void sendPicture(Uri imgUri) {

        String imagePath = getRealPathFromURI(imgUri); // path 경로

        Log.d(TAG, "sendPicture: imagePath: " + imagePath);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
        imgViewLMemoImg.setImageBitmap(bitmap);//이미지 뷰에 비트맵 넣기

    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }

    private void getPictureForPhoto(Intent intent) {

        Bundle extras = intent.getExtras();

        Bitmap bitmap = (Bitmap)extras.get("data");
        bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
        imgViewLMemoImg.setImageBitmap(bitmap);//이미지 뷰에 비트맵 넣기
    }

    private File createImageFile() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mImageCaptureName = timeStamp + ".png";

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/"
                + mImageCaptureName);
        currentPhotoPath = storageDir.getAbsolutePath();

        return storageDir;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: result code : " + resultCode);
        Log.d(TAG, "onActivityResult: request code: " + requestCode);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_CODE:
                    sendPicture(data.getData()); //갤러리에서 가져오기
                    break;
                case REQUEST_TAKE_PHOTO:
                    getPictureForPhoto(data); //카메라에서 가져오기
                    break;
                case HikingMemberListAdapter.REQUEST_CODE:
                    Intent intent = data;
                    double latitude = intent.getDoubleExtra("user_lat", 0.0d);
                    double longitude = intent.getDoubleExtra("user_lng", 0.0d);
                    gMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                    break;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        myCurrentLocation = location;

        double userSpeed = Math.round(location.getSpeed() * 36d) / 10d;
        tvUserSpeed.setText(String.valueOf(userSpeed));

        /*Log.d(TAG, "onLocationChanged: userSpeed: " + userSpeed);
        Log.d(TAG, "onLocationChanged: 1: " + String.valueOf(wholeDistance - hikedDistance));
        Log.d(TAG, "onLocationChanged: 2: " + String.valueOf((wholeDistance - hikedDistance) / userSpeed));
        Log.d(TAG, "onLocationChanged: 3: " + String.valueOf(Math.round((wholeDistance - hikedDistance) / userSpeed * 1000d) / 1000d));*/

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
