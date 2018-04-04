package kr.ac.yjc.wdj.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.yjc.wdj.myapplication.APIs.HttpRequest.HttpRequestConnection;

/**
 * Created by SongSol on 2018-03-30.
 */

public class PostGPSInfo extends Activity{
    TextView textView,txtMsg;
    Button post_btn;
    HttpRequestConnection hrc = new HttpRequestConnection();
    String result;
    Handler handler;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_gps_info);

        txtMsg = findViewById(R.id.txtMsg);
        post_btn = findViewById(R.id.post);
        textView = findViewById(R.id.intent_value);
        Intent intent = getIntent();
        final ArrayList<Double> post_gps = (ArrayList<Double>) intent.getSerializableExtra("get_gps");

        final ContentValues contentValues = new ContentValues();
        contentValues.put("lat", post_gps.get(0));
        contentValues.put("lng", post_gps.get(1));

        // Save GPS Information
        double latitude = post_gps.get(0);
        double longitude = post_gps.get(1);

        textView.setText("경도 : " + latitude + "\n위도 : " + longitude);

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        result = hrc.request("http://192.168.0.7/test.php", contentValues);
                        Message msg = handler.obtainMessage();
                        handler.sendMessage(msg);
                    }
                }).start();
                handler = new Handler() {
                    public void handleMessage(Message msg) {
                        textView.setText(result);
                    }
                };
            }
        });
    }
}