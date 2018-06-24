package kr.ac.yjc.wdj.hikonnect;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import kr.ac.yjc.wdj.hikonnect.MapItem;

public class LocationMemo extends MapItem implements ClusterItem {

    public int no;
    public String title;
    public String contents;
    public Bitmap picture;
    public boolean wasShown = false;

    public LocationMemo(LatLng location, int no) {
        super(location);
        this.no = no;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }
}
