package kr.ac.yjc.wdj.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;

import kr.ac.yjc.wdj.myapplication.APIs.HttpRequest.HttpRequestConnection;

/**
 * Created by jungyu on 2018-04-11.
 */

public class Locationmemo extends Activity {

    TextView title;
    Handler handler;
    TextView content;
    Double lat,lng;
    Bitmap bitmap;
    ContentValues contentValues;
    HttpRequestConnection hrc;
    String result;
    ImageView image1;
    String path;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_memo);

        title = findViewById(R.id.gettitle);
        image1 = findViewById(R.id.image1);
        content = findViewById(R.id.getcontent);
        contentValues = new ContentValues();
        hrc = new HttpRequestConnection();
        Intent intent = getIntent();
        title.setText(intent.getStringExtra("title"));
        content.setText(intent.getStringExtra("content"));
        lat = intent.getDoubleExtra("latitude",0);
        lng = intent.getDoubleExtra("longitude",0);
        contentValues.put("lat",lat);
        contentValues.put("lng",lng);
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = hrc.request("http://172.25.1.9:8000/api/position",contentValues);
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        }).start();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        path       = jsonObject.getString("image_path");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

       File imgFile = new  File("/storage/emulated/0/DCIM/Camera/20180410_035545.jpg");
      if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
           image1.setImageBitmap(myBitmap);
       }
    }
}
