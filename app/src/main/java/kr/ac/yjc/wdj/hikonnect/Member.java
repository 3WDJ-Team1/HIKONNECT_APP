package kr.ac.yjc.wdj.hikonnect;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Member extends MapItem implements ClusterItem {

    public int      member_no;
    public String   userID;
    public String   nickname;
    public Bitmap   profileImg;
    public double   hikedDistance;
    public String   hikingStartedAt;
    public double   avgSpeed;
    public int      rank;

    public Member(LatLng location, int member_no) {
        super(location);
        this.member_no = member_no;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }
}
