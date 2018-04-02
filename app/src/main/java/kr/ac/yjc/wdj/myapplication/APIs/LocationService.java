package kr.ac.yjc.wdj.myapplication.APIs;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Provider;

/*import kr.ac.yjc.wdj.myapplication.models.Conf;
import kr.ac.yjc.wdj.myapplication.models.HikingPlan;*/

/**
 * Created by Kwon on 3/26/2018.
 */

public class LocationService{

    private final String TAG = "LocationService";

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 10;

    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    private LocationManager lm;

    private Context context;

    private String provider;

    public LocationService(Context context) {
        // 위치 관리자 객체를 가져온다.
        this.lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        this.context = context;
    }

    public void getMyLocation(LocationListener locationListener) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true) {
            Log.v("GPS 없음", "없어ㅏ ㅠㅠ");
            provider = LocationManager.NETWORK_PROVIDER;
        }
        else if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) == true){
            Log.v("네트워크 없음","없어ㅏ ㅠㅠ");
            provider = LocationManager.GPS_PROVIDER;
        }
        else {
            Log.v("둘다 없음", "없어ㅏㅠㅠ");
            provider = LocationManager.GPS_PROVIDER;
        }


        this.lm.requestLocationUpdates(
                provider,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATE,
                locationListener
        );
    }

    public void sendMyLocation() {

        getMyLocation(new LocationListener() {
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
        });

    }
}