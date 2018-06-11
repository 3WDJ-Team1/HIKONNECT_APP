package kr.ac.yjc.wdj.hikonnect.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.SupportMapFragment;

public class StaticMapFragment extends SupportMapFragment {
    public View mapView;
    public TouchableWrapper touchView;
    private StaticMapFragment.OnTouchListener listener;

    public static StaticMapFragment newInstance() {
        return new StaticMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        mapView = super.onCreateView(layoutInflater, viewGroup, bundle);

        touchView = new TouchableWrapper(getActivity());
        touchView.addView(mapView);
        return touchView;
    }

    public void setOnTouchListener(StaticMapFragment.OnTouchListener listener) {
        this.listener = listener;
    }

    public interface OnTouchListener {
        void onTouch();
    }

    public class TouchableWrapper extends FrameLayout {
        public TouchableWrapper(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (listener != null) {
                        listener.onTouch();
                    }
                    return true;
            }
            return super.dispatchTouchEvent(ev);
        }
    }
}
