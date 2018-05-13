package kr.ac.yjc.wdj.hikonnect.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * The Activity used when first app is run
 * @author  Areum Lee (leear5799@gmail.com)
 * @since   2018-04-24
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

        startActivity(new Intent(this, LoginActivity.class));
        //startActivity(new Intent(this, PreActivity.class));
        finish();
    }
}
