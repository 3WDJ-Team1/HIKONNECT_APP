package kr.ac.yjc.wdj.hikonnect.apis;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/*import kr.ac.yjc.wdj.hikonnect.models.Conf;
import kr.ac.yjc.wdj.hikonnect.models.HikingPlan;*/

/**
 * @author Beomsu Kwon
 * @since 2018-03-26
 */
public class LocationService {

    private final String TAG = "LocationService";

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 1;

    private static final long MIN_TIME_BW_UPDATES = 1000;

    private LocationManager lm;

    private Context context;

    public LocationService(Context context) {
        // 위치 관리자 객체를 가져온다.
        this.lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.context = context;
    }

    public void setLocationListener(LocationListener locationListener) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            return;

        this.lm.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATE,
                locationListener,
                Looper.getMainLooper()
        );
        this.lm.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATE,
                locationListener
        );
    }
}