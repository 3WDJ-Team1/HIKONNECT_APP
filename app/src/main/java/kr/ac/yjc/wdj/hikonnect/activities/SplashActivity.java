package kr.ac.yjc.wdj.hikonnect.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @file        kr.ac.yjc.wdj.hikonnect.activities.SplashActivity.java
 * @author      Areum Lee (leear5799@gmail.com)
 * @since       2018-04-24
 * @brief       The Activity used when first app is run
 */

public class SplashActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startActivity(new Intent(this, PreActivity.class));
        //startActivity(new Intent(this, PreActivity.class));
        finish();
    }
}
