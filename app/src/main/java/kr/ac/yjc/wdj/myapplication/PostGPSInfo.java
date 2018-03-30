package kr.ac.yjc.wdj.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by SongSol on 2018-03-30.
 */

public class PostGPSInfo extends Activity{
    TextView textView;
    Button post_btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_gps_info);

        post_btn = findViewById(R.id.post);
        textView = findViewById(R.id.intent_value);
        Intent intent = getIntent();
        ArrayList<Double> post_gps = (ArrayList<Double>) intent.getSerializableExtra("get_gps");

        double latitude = post_gps.get(0);
        double longitude = post_gps.get(1);

        textView.setText("경도 : " + latitude + "\n위도 : " + longitude);

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


}
