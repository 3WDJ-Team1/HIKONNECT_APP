package kr.ac.yjc.wdj.hikonnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.klinker.android.sliding.SlidingActivity;

import java.util.ArrayList;

public class ClusterDetailActivity extends SlidingActivity {
    public static final int CLUSTER_CLICKED = 1;

    private static final String TAG = "HIKONNECT";

    private ArrayList<Integer>      memberIDs;
    private ArrayList<Integer>      lMemoIDs;

    @Override
    public void init(Bundle bundle) {
        getArgsFromParent();

        setTitle("Activity Title");
    }

    private void getArgsFromParent() {
        Intent fromParnet = getIntent();

        switch (fromParnet.getIntExtra("key", 0)) {
            case CLUSTER_CLICKED:
                memberIDs   = fromParnet.getIntegerArrayListExtra("memberIDs");
                lMemoIDs    = fromParnet.getIntegerArrayListExtra("lMemoIDs");

                Log.d(TAG, "Member IDs          : " + memberIDs);
                Log.d(TAG, "Location Memo IDs   : " + lMemoIDs);
                break;
        }
    }
}
