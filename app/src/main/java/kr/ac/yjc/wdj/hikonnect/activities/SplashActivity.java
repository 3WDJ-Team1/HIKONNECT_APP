package kr.ac.yjc.wdj.hikonnect.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import kr.ac.yjc.wdj.hikonnect.R;

/**
 * The Activity used when first app is run
 * @author  Areum Lee (leear5799@gmail.com)
 * @since   2018-04-24
 */
public class SplashActivity extends Activity{
    private final int DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startActivity(new Intent(this, LoginActivity.class));
        //startActivity(new Intent(this, PreActivity.class));
        finish();*/
        startLoading();
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                finish();
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        }, DELAY);
    }
}
