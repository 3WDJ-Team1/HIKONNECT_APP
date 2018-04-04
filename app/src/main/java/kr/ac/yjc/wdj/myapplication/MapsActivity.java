package kr.ac.yjc.wdj.myapplication;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import kr.ac.yjc.wdj.myapplication.APIs.LocationService;
import kr.ac.yjc.wdj.myapplication.APIs.PermissionManager;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Uri mImageCaptureUri;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;


    ImageView imageView;
    EditText editText;
    Button okuru;
    LinearLayout linearLayout;
    TextView tv;
    ToggleButton tb;
    Button info_intent;
    double lng,lat;
    ArrayList<Double> post_gps = new ArrayList<>();
    PermissionManager pManager;
    String network;
    AlertDialog.Builder builder;


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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get GPS Information
        imageView = findViewById(R.id.imageView1);
        editText = findViewById(R.id.edittext);
        okuru = findViewById(R.id.okuru);
        tv = findViewById(R.id.textView2);
        tv.setText("미수신중");

        tb = findViewById(R.id.toggle1);
        info_intent = findViewById(R.id.info_intent);
        linearLayout = findViewById(R.id.imagelayout);

        final LocationService ls = new LocationService(getApplicationContext());

        okuru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    post_gps.add(lng);
                    post_gps.add(lat);
                    Intent intent = new Intent(getApplicationContext(),PostGPSInfo.class);
                    intent.putExtra("get_gps",post_gps);
                    startActivity(intent);
                }catch (SecurityException ex) {
                    ex.printStackTrace();
                }
                linearLayout.setVisibility(View.INVISIBLE);
                editText.setText("");
                imageView.setImageResource(R.color.colorPrimary);
            }
        });

        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(tb.isChecked()){
                        tv.setText("수신중..");
                        Log.v("돼라","ㅁㄴㅇㅁㄴㅇㅁㄴㅇ");
                        ls.getMyLocation(new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                Log.d("Location test",location.toString());
                                lng = location.getLongitude();
                                lat = location.getLatitude();
                                network = location.getProvider();
                                tv.setText("위도 : " + lat + "\n경도 : " + lng + "\n네트워크 종류 : " + network);

                                LatLng nl = new LatLng(lat, lng);
                                mMap.addMarker(new MarkerOptions().position(nl).title("Now Locate"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(nl));
                                mMap.setMaxZoomPreference(mMap.getMaxZoomLevel());
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
                                Log.v("GPS Check","false");
                                showAlertDialog();
                                Log.v("show 실행","ㅎㅎ");
                            }
                        });
                    }else{
                        tv.setText("미수신중");
                        ls.remove();
                    }
                }catch(SecurityException ex){
                }
            }
        });

        info_intent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                    }
                });
                alertDialog.show();
            }
        });
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode != RESULT_OK)
            return;

        switch(requestCode)
        {
            case PICK_FROM_ALBUM:
            {
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    //String name_Str = getImageNameToUri(data.getData());
                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap 	= MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    //배치해놓은 ImageView에 set
                    imageView.setImageBitmap(image_bitmap);

                } catch (FileNotFoundException e) {

                    // TODO Auto-generated catch block

                    e.printStackTrace();

                } catch (IOException e) {

                    // TODO Auto-generated catch block

                    e.printStackTrace();

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            }

            case PICK_FROM_CAMERA:
            {
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    //String name_Str = getImageNameToUri(data.getData());
                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap 	= MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    //배치해놓은 ImageView에 set
                    imageView.setImageBitmap(image_bitmap);

                } catch (FileNotFoundException e) {

                    // TODO Auto-generated catch block

                    e.printStackTrace();

                } catch (IOException e) {

                    // TODO Auto-generated catch block

                    e.printStackTrace();

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            }
            }
        }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
                        startActivityForResult(intent,1);
                        onRestart();
                        tv.setText("미수신중");
                        tb.setChecked(false);
                    }
                });
        builder.setNegativeButton("취소",null);
        builder.show();

        Log.v("GPS on","나머지 설정");
    }
}