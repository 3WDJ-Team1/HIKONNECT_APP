package kr.ac.yjc.wdj.hikonnect.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.wifi_p2p_test.WifiP2pTestingActivity;

/**
 * @file        kr.ac.yjc.wdj.hikonnect.activities.MainActivity.java
 * @author      Areum Lee (leear5799@gmail.com)
 * @since       2018-04-24
 * @brief       The Activity used app's main page
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}