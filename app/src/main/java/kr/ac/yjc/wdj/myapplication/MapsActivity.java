package kr.ac.yjc.wdj.myapplication;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    /**
     * @class       GetHikingPathTask
     */
    private class GetHikingPathTask extends AsyncTask<URL, Integer, PolylineOptions> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected PolylineOptions doInBackground(URL... urls) {
            PolylineOptions polylineOptions = new PolylineOptions().geodesic(true).color(Color.RED);
            List<LatLng>    pathPoints      = new ArrayList<>();

            return polylineOptions;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(PolylineOptions polylineOptions) {
            super.onPostExecute(polylineOptions);
            mMap.addPolyline(polylineOptions);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        try {
            URL routeOfHiking = new URL("http://hikonnect.ga:3000/paths/115000801");
            new GetHikingPathTask().execute(routeOfHiking);
            mMap.addPolyline(new PolylineOptions().geodesic(true)
                    .add(new LatLng(-33.866, 151.195))  // Sydney
                    .add(new LatLng(-18.142, 178.431))  // Fiji
                    .add(new LatLng(21.291, -157.821))  // Hawaii
                    .add(new LatLng(37.423, -122.091))
                    .color(Color.RED)// Mountain View
            );
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        }
    }
}
