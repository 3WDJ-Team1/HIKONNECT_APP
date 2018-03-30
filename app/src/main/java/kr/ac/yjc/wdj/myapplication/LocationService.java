package kr.ac.yjc.wdj.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by Kwon on 3/26/2018.
 */

public class LocationService {
    private final String TAG = "LocationService";

    private LocationManager lm;
    private LocationListener lListener;

    private Context activity;

    LocationService(Context context, LocationListener lListener) {
        // 위치 관리자 객체를 가져온다.
        this.lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        this.activity = context;
        this.lListener = lListener;
    }

    public void getLocation() {
        // 로케이션 퍼미션 체크
        if (ActivityCompat.checkSelfPermission(
                    this.activity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                        this.activity,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            )
        {
            return;
        }
        this.lm.requestLocationUpdates(
                LocationManager.PASSIVE_PROVIDER,
                100,
                1,
                lListener
        );
    }
}
