package kr.ac.yjc.wdj.myapplication.groupdetail.fragment;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.ac.yjc.wdj.myapplication.R;
import kr.ac.yjc.wdj.myapplication.groupdetail.GMapFragment;

/**
 * Created by 강성은 on 2018-04-16.
 */

public class FragmentPlan extends Fragment{
    @BindView(R.id.scrollView)
    NestedScrollView mScrollView;

    public static FragmentPlan newInstance() {
        return new FragmentPlan();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*View view = new GMapFragment().onCreateView(getLayoutInflater(), container, null);
        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)fragmentManager.findFragmentById(R.id.groupPlanMap);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng SEOUL = new LatLng(37.56, 126.97);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(SEOUL);
                markerOptions.title("서울");
                markerOptions.snippet("한국의 수도");
                googleMap.addMarker(markerOptions);

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                googleMap.getUiSettings().setScrollGesturesEnabled(false);
            }
        });

        container.addView(view);

        return container;*/
        return null;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView);
    }
}
