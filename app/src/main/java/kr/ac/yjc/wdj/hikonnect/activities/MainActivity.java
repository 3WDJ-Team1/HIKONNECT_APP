package kr.ac.yjc.wdj.hikonnect.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.wifi_p2p_test.WifiP2pTestingActivity;

/**
 * Created by Kwon on 4/14/2018.
 */

public class MainActivity extends Activity implements View.OnClickListener{

    private ArrayList<Button> btns = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btns.add((Button)findViewById(R.id.bottom_home_btn));
        btns.add((Button)findViewById(R.id.bottom_notice_btn));
        btns.add((Button)findViewById(R.id.bottom_grp_search_btn));
        btns.add((Button)findViewById(R.id.bottom_my_profile_btn));

        for (Button btn : btns) {
            btn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {

        Intent intent = null;

        switch(view.getId()) {
            case R.id.bottom_home_btn:
                intent = new Intent(this, MainActivity.class);
                break;
            case R.id.bottom_notice_btn:
                intent = new Intent(this, WifiP2pTestingActivity.class);
                break;
            case R.id.bottom_grp_search_btn:
                break;
            case R.id.bottom_my_profile_btn:
                intent = new Intent(this, LoginActivity.class);
                break;
        }

        if (intent == null) {
            Toast.makeText(this, "Something is wrong!!", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(intent);
    }
}
