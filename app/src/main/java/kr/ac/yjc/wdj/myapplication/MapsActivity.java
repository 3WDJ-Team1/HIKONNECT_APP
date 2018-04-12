package kr.ac.yjc.wdj.myapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

import kr.ac.yjc.wdj.myapplication.APIs.HttpRequest.HttpRequestConnection;
//import kr.ac.yjc.wdj.myapplication.wireless.Wireless_main;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final long START_TIME_IN_MILLIS = 180000;
    private TextView textView;
    private FloatingActionButton mic;
    private static CountDownTimer mCountDownTimer;
    private long mTimeLeftInnMilles = START_TIME_IN_MILLIS;
    private String result;
    private boolean timerSwitch = true;
    private Handler handler;
    private HttpRequestConnection req = new HttpRequestConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mic = (FloatingActionButton) findViewById(R.id.mic);
        textView = (TextView) findViewById(R.id.textView_countdown);
        mic = findViewById(R.id.mic);

        mic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(timerSwitch) startTimer();
                return false;
            }
        });
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!timerSwitch) stopTimer();
            }
        });
    }

    private void startTimer() {
        mTimeLeftInnMilles = START_TIME_IN_MILLIS;
        textView.setVisibility(View.VISIBLE);
        // mic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_off_black_24dp));
        mCountDownTimer = new CountDownTimer(mTimeLeftInnMilles, 1000) {
            @Override
            public void onTick(long l) {
                final long loong = l;
                mTimeLeftInnMilles = l;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                sign();

            }
        }.start();
        timerSwitch = false;
    }
    // 타이머 멈춤
    private void stopTimer()    {
        sign();
        timerSwitch = true;
    }
    private void sign()  {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setMessage("Are you sure?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setNegativeButton("CANCEL", null);
        builder.show();
        textView.setVisibility(View.INVISIBLE);
        mCountDownTimer.cancel();
    }
    private void updateCountDownText()  {
        int minutes = (int) (mTimeLeftInnMilles / 1000) / 60;
        int seconds = (int) (mTimeLeftInnMilles / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textView.setText(timeLeftFormatted);
    }
    public void sendData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = req.request("http://172.25.1.167:8000/group/0/10", null);
            }
        }).start();
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}
