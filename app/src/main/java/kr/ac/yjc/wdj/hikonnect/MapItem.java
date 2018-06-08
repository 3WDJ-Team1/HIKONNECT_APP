package kr.ac.yjc.wdj.hikonnect;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MapItem implements ClusterItem {
    protected LatLng location;

    public MapItem(LatLng location) {
        this.location = location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }
}
