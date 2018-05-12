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
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.gun0912.tedpermission.TedPermission;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import kr.ac.yjc.wdj.hikonnect.apis.walkietalkie.WalkieTalkie;
import kr.ac.yjc.wdj.hikonnect.myinfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//길이와 현재 fid를 담는 클래스입니다.
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
class LatLngCrnId {

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
}

/**
 *
 * @author Jungyu Choi(), Sungeun Kang (kasueu0814@gmail.com)
 */
public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        View.OnClickListener {

    // UI 변수
    private EditText                content,
                                    editText,
                                    title;
    private Button                  post_btn,
                                    cancel,
                                    status,
                                    btnToRecordList;
    private ImageView               imageView;
    private LinearLayout            linearLayout;
    private TextView                tv;
    private FloatingActionMenu      floatingActionMenu;
    private FloatingActionButton    fab1,
                                    fab2,
                                    gpsbutton,
                                    user_info_button,
                                    myinfo_button,
                                    btnSendRadio;   // 무전 버튼

    // GoogleMaps
    private GoogleMap                           mMap;
    private ArrayList<ArrayList<LatLngCrnId>>   polylinegroup   = new ArrayList<>();
    private ArrayList<CrnidDistance>            crnidDistances  = new ArrayList<>();
    private ArrayList<LatLngCrnId>              polyline;
    private Marker[]                            markers;

    // 데이터 관련 변수
    private int                 STATUS_HIKING   = 1;
    private int                 absolutevalue   = 0;
    private ArrayList<String>   name            = new ArrayList<String>();
    private String              image_path      = "File_path";
    // <-- 속도 관련 데이터
    private double              all_distance    = 0;
    private double              hiking_distance = 0;
    private LatLng[]            speed           = new LatLng[2];
    // -->
    private int                 my_current_id   = 0;
    private String              positionuser, result, network, user_id, nickname,
                                hiking_group, title_st, content_st = "";
    private double              now_lat, now_lng, lat, lng, rlat, rlng,
                                now_lat2, now_lng2;
    private Float               distance;


    // 위치 서비스.
    LocationService             locationService;
    // 구글맵 관련 데이터
    private double  velocity    = 0;
    private double  minimum     = 0;
    private int     minimum_group_poly;
    private int     minimum_poly;

    // 상수
    private static final int PICK_FROM_CAMERA   = 0;
    private static final int PICK_FROM_ALBUM    = 1;

    // 핸들러
    private BackPressClosHandler backPressClosHandler;
    private Handler                 handler;

    // 퍼미션
    private PermissionManager       pManager;
    private PermissionListener      permissionlistener = null;

    // 기타
    private ContentValues           contentValues   = new ContentValues();
    private HttpRequestConnection   hrc             = new HttpRequestConnection();
    private AlertDialog.Builder     builder;
    private Bitmap                  image_bitmap;
    private Uri                     uri;
    private boolean                 tf              = true;
    private WalkieTalkie            walkieTalkie;   // 무전 객체
    private boolean                 isSendingNow;   // 현재 무전을 전송중인지


    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if (linearLayout.getVisibility() == View.VISIBLE) {
            imageView.setImageResource(R.color.colorPrimary);
            editText.setText("");
            linearLayout.setVisibility(View.INVISIBLE);
        } else {
            backPressClosHandler.onBackPressed();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        /*// Firebase 푸시 메시지
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();*/

        // 퍼미션 관리자 생성
        pManager = new PermissionManager(this);
        // 퍼미션 검사 수행
        Map<String, Integer> checkResult = pManager.checkPermissions();
        // 퍼미션 권한 요청

        pManager.requestPermissions();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get GPS Information
        markers                 = new Marker[1000];
        backPressClosHandler    = new BackPressClosHandler(MapsActivity.this);

        // UI init
        status              = findViewById(R.id.status);
        gpsbutton           = findViewById(R.id.gpsbutton);
        imageView           = findViewById(R.id.imageView1);
        editText            = findViewById(R.id.content);
        post_btn            = findViewById(R.id.post_btn);
        cancel              = findViewById(R.id.cancel);
        tv                  = findViewById(R.id.textView2);
        tv.setText("미수신중");
        status              = findViewById(R.id.status);
        content             = findViewById(R.id.content);
        fab1                = findViewById(R.id.fab1);
        fab2                = findViewById(R.id.fab2);
        floatingActionMenu  = findViewById(R.id.fabmenu);
        myinfo_button       = findViewById(R.id.myinfo_button);
        user_info_button    = findViewById(R.id.user_info_button);
        title               = findViewById(R.id.title);
        linearLayout        = findViewById(R.id.imagelayout);
        // 무전 버튼 초기화
        btnSendRadio        = (FloatingActionButton) findViewById(R.id.sendRecordData);
        // 녹음 리스트 이동 버튼 초기화
        btnToRecordList     = (Button) findViewById(R.id.showRecordList);

        // data init
        speed[0]    = new LatLng(0,0);
        speed[1]    = new LatLng(0,0);


        locationService = new LocationService(getApplicationContext());

        final Intent intent = getIntent();
        user_id = intent.getExtras().getString("id");

        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MapsActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MapsActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        myinfo_button.setOnClickListener(this);
        status.setOnClickListener(this);
        fab2.setOnClickListener(this);
        user_info_button.setOnClickListener(this);
        cancel.setOnClickListener(this);
        post_btn.setOnClickListener(this);
        gpsbutton.setOnClickListener(this);
        fab1.setOnClickListener(this);

        isSendingNow = false;

        // 무전 객체 초기화
        walkieTalkie = new WalkieTalkie();
        // 무전 받아오기 시작
        walkieTalkie.receiveStart();
        // 무전 버튼에 리스너 달기
        btnSendRadio.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                if (!isSendingNow) {
                    isSendingNow = true;
                    walkieTalkie.sendStart();
                    Toast.makeText(getBaseContext(), "무전 시작합니다", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        btnSendRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSendingNow) {
                    isSendingNow = false;
                    walkieTalkie.sendEnd();
                    Toast.makeText(getBaseContext(), "무전 종료합니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 녹음 리스트 액티비티로 이동할 리스너 붙이기
        btnToRecordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recordIntent = new Intent(getBaseContext(), RecordListActivity.class);
                startActivity(recordIntent);
            }
        });
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
                    uri         = data.getData();
                    image_path  = getRealPathFromURI(uri);
                    //이미지 데이터를 비트맵으로 받아온다.
                    image_bitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(),
                            data.getData()
                    );
                    //배치해놓은 ImageView에 set
                    imageView.setImageBitmap(image_bitmap);
                    Log.v("이미지", imageView.toString());
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
                    uri         = data.getData();
                    image_path  = getRealPathFromURI(uri);
                    //이미지 데이터를 비트맵으로 받아온다.
                    image_bitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(),
                            data.getData()
                    );
                    //배치해놓은 ImageView에 set
                    imageView.setImageBitmap(image_bitmap);
                    linearLayout.setVisibility(View.VISIBLE);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        requestGet(Environment.NODE_LOCAL_IP + "/dummy/school", null);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int k = 0; k < crnidDistances.size(); k++) {
            all_distance += crnidDistances.get(k).getDistance();
        }

        for (int a = 0; a < polylinegroup.size(); a++) {
            for (int b = 0; b < polylinegroup.get(a).size() - 1; b++) {
                mMap.addPolyline(new PolylineOptions()
                        .add(polylinegroup.get(a).get(b).getLatLng(), polylinegroup.get(a).get(b + 1).getLatLng())
                        .color(Color.RED)
                        .width(10));
            }
        }
        //error
        Log.d("TEST", polylinegroup.size() + "");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(polylinegroup.get(0).get(0).getLatLng()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(polylinegroup.get(0).get(0).getLatLng(), 23));

        Timer       timer       = new Timer();
        handler                 = new Handler();
        TimerTask   timerTask   = new TimerTask() {
            @Override
            public void run() {
                hiking_distance = 0;
                name.clear();
                //requestGet("http://172.26.2.38:3000/paths/113200104",null);
                final LocationService locationService = new LocationService(getApplicationContext());
                locationService.getMyLocation(new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        now_lng2 = location.getLongitude();
                        now_lat2 = location.getLatitude();
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

                speed[0] = speed[1];
                speed[1] = new LatLng(now_lat2, now_lng2);
                Location locations = new Location("poin1");
                locations.setLatitude(speed[0].latitude);
                locations.setLongitude(speed[0].longitude);

                Location locationp = new Location("poin2");
                locationp.setLatitude(speed[1].latitude);
                locationp.setLongitude(speed[1].longitude);

                velocity = locations.distanceTo(locationp) / 10;
                Log.d("velocity", String.valueOf(velocity));

                for (int a = my_current_id - 1; a <= my_current_id + 1; a++) {
                    int start = 0;
                    if (a == -1)
                        continue;
                    if (a < polylinegroup.size()) {
                        for (int b = 0; b < polylinegroup.get(a).size(); b++) {
                            Location location1 = new Location("point1");
                            location1.setLatitude(polylinegroup.get(a).get(b).getLatLng().latitude);
                            location1.setLongitude(polylinegroup.get(a).get(b).getLatLng().longitude);

                            Location location2 = new Location("point2");
                            location2.setLatitude(now_lat2);
                            location2.setLongitude(now_lng2);

                            double kedistance = location1.distanceTo(location2);
                            if (start == 0)
                                minimum = kedistance;
                            else if (minimum > kedistance) {
                                minimum = kedistance;
                                minimum_group_poly = a;
                                minimum_poly = b;
                                Log.d("!@##$", String.valueOf(minimum) + minimum_group_poly + String.valueOf(minimum_poly));
                            }
                            start++;
                        }
                    }
                }

                my_current_id = minimum_group_poly;
                Log.d("current", String.valueOf(my_current_id));

                if (STATUS_HIKING == 1) {
                    Location locationnow = new Location("now");
                    locationnow.setLatitude(polylinegroup.get(minimum_group_poly).get(0).getLatLng().latitude);
                    locationnow.setLongitude(polylinegroup.get(minimum_group_poly).get(0).getLatLng().longitude);

                    Location locationnow2 = new Location("now2");
                    locationnow2.setLatitude(polylinegroup.get(minimum_group_poly).get(minimum_poly).getLatLng().latitude);
                    locationnow2.setLongitude(polylinegroup.get(minimum_group_poly).get(minimum_poly).getLatLng().longitude);

                    hiking_distance += locationnow.distanceTo(locationnow2);

                    for (int h = 0; h < my_current_id; h++) {
                        hiking_distance += crnidDistances.get(h).getDistance() * 1000;
                    }
                }

                //hiking_distance;
                Log.d("@@@@@@hiking_distance:", String.valueOf(hiking_distance));

                hrc             = new HttpRequestConnection();
                contentValues   = new ContentValues();
                contentValues.put("id", user_id);
                contentValues.put("latitude", now_lat2);
                contentValues.put("longitude", now_lng2);

                //TODO 서버 쪽으로 바꾸기
                result = hrc.request(Environment.LARAVEL_LOCAL_IP + "/api/storesend", contentValues);
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
                handler = new Handler(Looper.getMainLooper()) {
                    public void handleMessage(Message msg) {

                        Location locationA= new Location("point A");

                        /*  if(STATUS_HIKING == 1) {
                            LatLngCrnId latLngCrnId2 = polylinegroup.get(0).get(0);
                            locationA.setLatitude(polylinegroup.get(0).get(0).getLatLng().latitude);
                            locationA.setLatitude(polylinegroup.get(0).get(0).getLatLng().longitude);
                            }
                            else {
                                ArrayList<LatLngCrnId> rolypoly = new ArrayList<LatLngCrnId>();
                                rolypoly = polylinegroup.get(polylinegroup.size()-1);
                                locationA.setLatitude(rolypoly.get(rolypoly.size()-1).getLatLng().latitude);
                                locationA.setLatitude(rolypoly.get(rolypoly.size()-1).getLatLng().longitude);
                            }*/

                        locationA.setLatitude(35.896844);
                        locationA.setLongitude(128.621261);

                        Location locationB = new Location("point B");

                        locationB.setLatitude(now_lat2);
                        locationB.setLongitude(now_lng2);

                        distance = locationA.distanceTo(locationB);
                        Log.d("dadadasdasdasdasd", distance.toString());

                        if (distance < 100) {
                            status.setVisibility(View.VISIBLE);
                        }


                        for (int i = 0; i < absolutevalue; i++) {
                            markers[i].remove();
                        }
                        absolutevalue = 0;


                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                positionuser = jsonObject.getString("userid");
                                lat = jsonObject.getDouble("latitude");
                                lng = jsonObject.getDouble("longitude");
                                Log.i("!@$#@!%$!@%$!@%!#%!", positionuser);

                                LatLng nl = new LatLng(lat, lng);

                                if (positionuser.equals(user_id)) {
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(nl)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.me1)));
                                    marker.setTag(positionuser);
                                    markers[absolutevalue] = marker;
                                    absolutevalue++;

                                } else {
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(nl)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_walk_black_24dp)));
                                    marker.setTag(positionuser);
                                    markers[absolutevalue] = marker;
                                    absolutevalue++;
                                }
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        if (marker.getTag().toString().equals("locationmemo")) {
                                            Intent intent1 = new Intent(MapsActivity.this, Locationmemo.class);
                                            rlat = marker.getPosition().latitude;
                                            rlng = marker.getPosition().longitude;
                                            intent1.putExtra("latitude", rlat);
                                            intent1.putExtra("longitude", rlng);
                                            startActivity(intent1);
                                        } else {
                                            Intent intent1 = new Intent(MapsActivity.this, HikingRecord.class);
                                            rlat = marker.getPosition().latitude;
                                            rlng = marker.getPosition().longitude;
                                            intent1.putExtra("name", marker.getTag().toString());
                                            intent1.putExtra("latitude", rlat);
                                            intent1.putExtra("longitude", rlng);
                                            startActivity(intent1);

                                        }
                                        return false;
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                tf = false;
            }
        };
        timer.schedule(timerTask, 0, 10000);
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
                        tv.setText("미수신중");
                    }
                });
        builder.setNegativeButton("취소", null);
        builder.show();

        Log.v("GPS on", "나머지 설정");
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

    // OkHttp Get
    OkHttpClient client2 = new OkHttpClient();

    public void requestGet(String url, String searchKey) {

        //URL에 포함할 Query문 작성 Name&Value
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addEncodedQueryParameter("searchKey", searchKey);
        String requestUrl = urlBuilder.build().toString();

        //Query문이 들어간 URL을 토대로 Request 생성
        Request request = new Request.Builder().url(requestUrl).build();

        //만들어진 Request를 서버로 요청할 Client 생성
        //Callback을 통해 비동기 방식으로 통신을 하여 서버로부터 받은 응답을 어떻게 처리 할 지 정의함
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("error", "Connect Server Error is " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Log.d("aaaa", "Response Body is " + response.body().string());
                String resultr = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(resultr);
                    /*JSONObject json = new JSONObject(resultr);
                    JSONObject attributes = json.getJSONObject("attributes");
                    String paths      = attributes.getString("paths");
                    Log.d("asdasdas",paths);*/

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONObject att = jsonObject.getJSONObject("geometry");
                        JSONObject attvl = jsonObject.getJSONObject("attributes");
                        JSONArray path = att.getJSONArray("paths");

                        double km = attvl.getDouble("PMNTN_LT");
                        int fid = attvl.getInt("FID");
                        CrnidDistance crnidDistance = new CrnidDistance(km, fid);
                        crnidDistances.add(crnidDistance);
                        for (int j = 0; j < path.length(); j++) {
                            polyline = new ArrayList<LatLngCrnId>();
                            JSONArray path2 = path.getJSONArray(j);
                            for (int k = 0; k < path2.length(); k++) {
                                JSONObject a = path2.getJSONObject(k);
                                Double lat = a.getDouble("lat");
                                Double lng = a.getDouble("lng");
                                LatLngCrnId latLngCrnId = new LatLngCrnId(new LatLng(lat, lng), fid);
                                polyline.add(latLngCrnId);
                            }
                            polylinegroup.add(polyline);
                        }
                    }
                } catch (JSONException e) {
                    Log.d("error", e.toString());
                    e.printStackTrace();
                }
            }
        });
    }

    // OkHttp Post
    OkHttpClient client = new OkHttpClient();

    public void requestPost(String url, String header, String body) {

        //Request Body에 서버에 보낼 데이터 작성
        RequestBody requestBody = new FormBody.Builder().add("header", header).add("body", body).build();

        //작성한 Request Body와 데이터를 보낼 url을 Request에 붙임
        Request request = new Request.Builder().url(url).post(requestBody).build();

        //request를 Client에 세팅하고 Server로 부터 온 Response를 처리할 Callback 작성
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("error", "Connect Server Error is " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("aaaa", "Response Body is " + response.body().string());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.post_btn:

                // Set Created_at, Updated_at
                String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                        .format(new Date(System.currentTimeMillis()));

                contentValues.put("user_id", user_id);
                contentValues.put("lat", now_lat);
                contentValues.put("lng", now_lng);
                contentValues.put("title", title.getText().toString());
                contentValues.put("content", content.getText().toString());
                contentValues.put("image_path", image_path);
                contentValues.put("created_at", time);
                contentValues.put("updated_at", time);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        result = hrc.request(Environment.LARAVEL_LOCAL_IP + "/api/test", contentValues);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText("위치메모 등록완료");
                                LatLng nl = new LatLng(now_lat, now_lng);
                            }
                        });
                    }
                }).start();
                linearLayout.setVisibility(View.INVISIBLE);
                editText.setText("");
                content.setText("");
                imageView.setImageResource(R.color.colorPrimary);
                break;
            case R.id.cancel:
                imageView.setImageResource(R.color.colorPrimary);
                editText.setText("");
                linearLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.status:
                if (STATUS_HIKING == 1) {
                    status.setVisibility(View.INVISIBLE);
                    status.setText("등산완료");
                    STATUS_HIKING = 0;
                }
                break;
            case R.id.fab1:
                TedPermission.with(MapsActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setRationaleMessage("사진을 보려면 권한이 필요함")
                        .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                        .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();
                floatingActionMenu.close(true);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);

                alertDialog.setPositiveButton("사진 등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, PICK_FROM_ALBUM);
                        linearLayout.setVisibility(View.VISIBLE);
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
                            // Do nothing for now
                        }
                    }
                });

                alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        linearLayout.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.INVISIBLE);
                    }
                });

                alertDialog.show();
                break;
            case R.id.fab2:
                floatingActionMenu.close(true);
                AlertDialog.Builder ad = new AlertDialog.Builder(MapsActivity.this);

                ad.setPositiveButton("글쓰기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imageView.setVisibility(View.INVISIBLE);
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                });
                ad.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                ad.show();
                break;
            case R.id.gpsbutton:
                try {
                    locationService.getMyLocation(new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

                            now_lng = location.getLongitude();
                            now_lat = location.getLatitude();
                            network = location.getProvider();

                            LatLng nl = new LatLng(now_lat, now_lng);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(nl));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nl, 23));


                               /* // Get All Location Memo
                                contentValues.put("id", user_id);
                                Log.i("@@@@@@@@@@@@@@@@@@@@@",user_id);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        result = hrc.request("http://172.26.2.200:8000/api/getlm",contentValues);
                                        Log.i("result", result);
                                        Message msg = handler.obtainMessage();
                                        handler.sendMessage(msg);
                                    }
                                }).start();
                                handler = new Handler() {
                                    public void handleMessage(Message msg) {
                                        try {
                                            JSONArray jsonArray = new JSONArray(result);
                                            for(int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                lat         = jsonObject.getDouble("latitude");
                                                lng         = jsonObject.getDouble("longitude");
                                                title_st    = jsonObject.getString("title");
                                                content_st  = jsonObject.getString("content");
                                                LatLng nl   = new LatLng(lat, lng);
                                                Marker marker = mMap.addMarker(new MarkerOptions()
                                                        .position(nl)
                                                        .title(title_st)
                                                        .snippet(content_st)
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bookmark_black_24dp)));
                                                marker.setTag("locationmemo");

                                                // Send PushMessage
                                                requestPost("http://hikonnect.ga/api/send", "위치메모가 있습니다.", "내용 확인 바랍니다.");
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };*/
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {

                        }

                        @Override
                        // GPS OFF
                        public void onProviderDisabled(String s) {
                            Log.v("GPS Check", "false");
                            showAlertDialog();
                        }
                    });

                } catch (SecurityException ex) {
                }
                break;
            case R.id.user_info_button:
                Intent intent = new Intent(MapsActivity.this, Othersinfo.class);
                intent.putExtra("userid", user_id);
                startActivity(intent);
                break;
            case R.id.myinfo_button:
                Intent intent1 = new Intent(MapsActivity.this, myinfo.class);
                intent1.putExtra("userid", user_id);
                intent1.putExtra("velocity", velocity);
                intent1.putExtra("distance", hiking_distance);
                intent1.putExtra("alldistance", all_distance);
                startActivity(intent1);
                break;
            case R.id.sendRecordData:
                break;
        }
    }
}