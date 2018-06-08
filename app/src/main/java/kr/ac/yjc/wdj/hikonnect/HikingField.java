package kr.ac.yjc.wdj.hikonnect;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class HikingField {

    private int     fid;
    private double  fieldLength;
    private ArrayList<LatLng> routes = new ArrayList<>();

    public HikingField(int fid, double fieldLength) {
        this.fid            = fid;
        this.fieldLength    = fieldLength;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public int getFid() {
        return fid;
    }

    public void setFieldLength(double fieldLength) {
        this.fieldLength = fieldLength;
    }

    public double getFieldLength() {
        return fieldLength;
    }

    public ArrayList<LatLng> getRoutes() {
        return routes;
    }

    public void addLatLng(LatLng latLng) {
        routes.add(latLng);
    }
}
