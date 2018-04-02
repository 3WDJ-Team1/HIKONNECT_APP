package kr.ac.yjc.wdj.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by SongSol on 2018-03-30.
 */

public class PostGPSInfo extends Activity{
            TextView textView,txtMsg;
            Button post_btn;

            @Override
            protected void onCreate(@Nullable final Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.post_gps_info);

                txtMsg = findViewById(R.id.txtMsg);
        post_btn = findViewById(R.id.post);
        textView = findViewById(R.id.intent_value);
        Intent intent = getIntent();
        ArrayList<Double> post_gps = (ArrayList<Double>) intent.getSerializableExtra("get_gps");

        // Save GPS Information
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