package kr.ac.yjc.wdj.myapplication.APIs;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

public class PermissionManager {

    private static String TAG = "PermissionManager";

    public static final int PERMISSIONS_REQUEST_CODE = 1;

    private FragmentActivity mActivity;

    public String[] permissions = {
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    public PermissionManager(FragmentActivity activity) {
        this.mActivity = activity;

    }

    public Map<String, Integer> checkPermissions() {

        Map<String, Integer> checkResult = new HashMap<>();

        for(String i : permissions) {
            checkResult.put(i, ContextCompat.checkSelfPermission(mActivity, i));
        }

        return checkResult;
    }

    public boolean requestPermissions() {

        ActivityCompat.requestPermissions(
                mActivity,
                permissions,
                PERMISSIONS_REQUEST_CODE);
        return false;
    }

}
