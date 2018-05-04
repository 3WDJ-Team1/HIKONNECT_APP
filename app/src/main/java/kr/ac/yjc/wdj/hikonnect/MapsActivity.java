package kr.ac.yjc.wdj.hikonnect;

import android.*;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
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

import kr.ac.yjc.wdj.hikonnect.APIs.HttpRequest.HttpRequestConnection;
import kr.ac.yjc.wdj.hikonnect.APIs.LocationService;
import kr.ac.yjc.wdj.hikonnect.APIs.PermissionManager;
import kr.ac.yjc.wdj.hikonnect.Locationmemo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    String PROXIMITY_ALERT = "com.example.intent.action.PROXIMITY_ALERT";
    private GoogleMap mMap;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private BackPressClosHandler backPressClosHandler;
    private EditText content, editText, title;
    private Button post_btn, cancel;
    private ImageView imageView;
    private LinearLayout linearLayout;
    private String image_path = "File_path";
    private TextView tv;
    private double now_lat, now_lng, lat, lng, rlat, rlng, now_lat2, now_lng2;
    private PermissionManager pManager;
    private ContentValues contentValues = new ContentValues();
    private AlertDialog.Builder builder;
    private Bitmap image_bitmap;
    private HttpRequestConnection hrc = new HttpRequestConnection();
    private String positionuser, result, network, user_id, nickname, hiking_group, title_st, content_st = "";
    private Handler handler;
    private Uri uri;
    private PermissionListener permissionlistener = null;
    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton gpsbutton;
    LocationManager locationManager;
    ArrayList<PendingIntent> pendingIntentArrayList = new ArrayList<PendingIntent>();



    @Override
    public void onBackPressed() {
        //super.onBackPressed();
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



/*        // Firebase 푸시 메시지
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
        backPressClosHandler = new BackPressClosHandler(MapsActivity.this);
        gpsbutton = findViewById(R.id.gpsbutton);
        imageView = findViewById(R.id.imageView1);
        editText = findViewById(R.id.content);
        cancel = findViewById(R.id.cancel);
        tv = findViewById(R.id.textView2);
        content = findViewById(R.id.content);
        post_btn = findViewById(R.id.post_btn);
        tv.setText("미수신중");
        floatingActionMenu = findViewById(R.id.fabmenu);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        title = findViewById(R.id.title);
        linearLayout = findViewById(R.id.imagelayout);
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
        // Set Created_at, Updated_at
        final SimpleDateFormat[] sdfNow = {new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")};
        final String time = sdfNow[0].format(new Date(System.currentTimeMillis()));
        final LocationService ls = new LocationService(getApplicationContext());

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageResource(R.color.colorPrimary);
                editText.setText("");
                linearLayout.setVisibility(View.INVISIBLE);
            }
        });
        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
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
                            result = hrc.request("http://hikonnect.ga/api/test", contentValues);
                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                        }
                    }).start();
                    handler = new Handler() {
                        public void handleMessage(Message msg) {
                            tv.setText("위치메모 등록완료");
                            LatLng nl = new LatLng(now_lat, now_lng);
                        }
                    };
                } catch (SecurityException ex) {
                    ex.printStackTrace();
                }
                linearLayout.setVisibility(View.INVISIBLE);
                editText.setText("");
                content.setText("");
                imageView.setImageResource(R.color.colorPrimary);
            }
        });

        gpsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                        tv.setText("수신중..");
                        ls.getMyLocation(new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                now_lng = location.getLongitude();
                                now_lat = location.getLatitude();
                                network = location.getProvider();

                                LatLng nl = new LatLng(now_lat, now_lng);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(nl));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nl, 23));
                                tv.setText(network + "로 접속됨");


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
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
                    uri = data.getData();
                    image_path = getRealPathFromURI(uri);
                    //이미지 데이터를 비트맵으로 받아온다.
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    //배치해놓은 ImageView에 set
                    imageView.setImageBitmap(image_bitmap);
                    Log.v("이미지", imageView.toString());
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
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
                    imageView.setImageBitmap(image_bitmap);
                    linearLayout.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
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
        Timer timer = new Timer();
        handler = new Handler();



        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                ConnectivityManager manager = (ConnectivityManager) MapsActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (wifi.isConnected() || mobile.isConnected()) {
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

                    hrc = new HttpRequestConnection();
                    contentValues = new ContentValues();
                    contentValues.put("id", user_id);
                    contentValues.put("latitude", now_lat2);
                    contentValues.put("longitude", now_lng2);
               /* String temp = "{\"latitude\":"+ "\"" + now_lat2 +"\"" + "," + "\"longitude\":" + "\"" + now_lng2 + "\"" + "}";
                contentValues.put("temp", temp);*/


                    result = hrc.request("http://hikonnect.ga:8000/api/storesend", contentValues);
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);
                    handler = new Handler(Looper.getMainLooper()) {
                        public void handleMessage(Message msg) {

                            LatLng nl2 = new LatLng(now_lat2, now_lng2);
                            mMap.clear();


                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    positionuser = jsonObject.getString("userid");
                                    lat = jsonObject.getDouble("latitude");
                                    lng = jsonObject.getDouble("longitude");
                                    Log.i("!@$#@!%$!@%$!@%!#%!", positionuser);
                                    //위치메모 위치 필요 userid 필요
                                /*JSONObject jsonObject1 = new JSONObject("location");
                                lat         = jsonObject1.getDouble("latitude");
                                lng         = jsonObject1.getDouble("longitude");*/

                                    LatLng nl = new LatLng(lat, lng);

                                    if (positionuser.equals(user_id)) {
                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                .position(nl)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.me1)));
                                        marker.setTag(positionuser);
                                    } else {
                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                .position(nl)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_walk_black_24dp)));
                                        marker.setTag(positionuser);
                                    }
                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            if (marker.getTag().toString() == "locationmemo") {
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
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(nl2));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nl2, 22));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                }
                else {
                    Toast.makeText(MapsActivity.this,"모바일 네트워크 연결 끊김",Toast.LENGTH_SHORT).show();
                }
            }
        };
        timer.schedule(timerTask,0,5000);
        };


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

    // OkHttp Post
    OkHttpClient client = new OkHttpClient();
    public void requestPost(String url, String header, String body){

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
}
