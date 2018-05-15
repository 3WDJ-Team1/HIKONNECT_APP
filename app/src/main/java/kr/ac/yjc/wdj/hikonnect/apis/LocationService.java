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
 * @author  Beomsu Kwon
 * @since   2018-03-26
 */
public class LocationService{

    private final String TAG = "LocationService";

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 10;

    private static final long MIN_TIME_BW_UPDATES = 1000;

    private LocationManager lm;

    private Context context;

    private String provider;

    // LocationListener
    private final LocationListener ls = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.v("test","onLocationChanged : " + location);
/*                    String url = Conf.HTTP_ADDR +
                            HikingPlan.NNetworkTask = new HikingPlan.NNetworkTask();*/
            }
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
    };

    public LocationService(Context context) {
        // 위치 관리자 객체를 가져온다.
        this.lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.context = context;

        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        }
        else if (lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
            provider = LocationManager.PASSIVE_PROVIDER;
        }
        else {
            provider = LocationManager.GPS_PROVIDER;
        }
        Log.d(TAG, "Location Provier: " + provider);
    }

    public void setLocationListener(LocationListener locationListener) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        this.lm.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATE,
                locationListener,
                Looper.getMainLooper()
        );
    }

    public void sendMyLocation() {
        setLocationListener(ls);
    }


    public void remove() {
        lm.removeUpdates(ls);
    }
}