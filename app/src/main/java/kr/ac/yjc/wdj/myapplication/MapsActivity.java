package kr.ac.yjc.wdj.myapplication;

/**
 * @file        kr.ac.yjc.wdj.myapplication.MapsActivity.java
 * @author      Sungeun Kang (kasueu0814@gmail.com), Beomsu Kwon (rnjs9957@gmail.com)
 * @since       2018-03-26
 * @brief       The Activity used while hiking
 * @see         kr.ac.yjc.wdj.myapplication.models.HikingPlan
 */

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import kr.ac.yjc.wdj.myapplication.models.HikingPlan;
import kr.ac.yjc.wdj.myapplication.models.Conf;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * @param googleMap     The object of GoogleMap which is ready.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // init this.mMap
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        String url = Conf.HTTP_ADDR + "/paths/438001301/1";

        HikingPlan.NetworkTask networkTask = new HikingPlan.NetworkTask(url, null, mMap);
        networkTask.execute();
    }
}
