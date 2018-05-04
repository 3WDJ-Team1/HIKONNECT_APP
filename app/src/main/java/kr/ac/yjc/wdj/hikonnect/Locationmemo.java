package kr.ac.yjc.wdj.hikonnect;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.APIs.HttpRequest.HttpRequestConnection;

/**
 * Created by jungyu on 2018-04-11.
 */

public class                                                                                                                                                                          Locationmemo extends Activity {

    String titlestring, contentstring;
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
    PermissionListener permissionlistener = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_memo);

        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(Locationmemo.this, "권한 허가", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(Locationmemo.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        title = findViewById(R.id.gettitle);
        image1 = findViewById(R.id.image1);
        content = findViewById(R.id.getcontent);
        contentValues = new ContentValues();
        hrc = new HttpRequestConnection();
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("latitude",0);
        lng = intent.getDoubleExtra("longitude",0);
        contentValues.put("lat",lat);
        contentValues.put("lng",lng);
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = hrc.request("http://hikonnect.ga/api/position",contentValues);
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
                        titlestring     =  jsonObject.getString("title");
                        contentstring     =  jsonObject.getString("content");
                        path       = jsonObject.getString("image_path");
                        TedPermission.with(Locationmemo.this)
                                .setPermissionListener(permissionlistener)
                                .setRationaleMessage("사진을 보려면 권한이 필요함")
                                .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                                .check();

                        //path = path.replaceAll("\\/","/");
                        title.setText(titlestring);
                        content.setText(contentstring);
                        File imgFile = new File(path);
                        if(imgFile.exists()){
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            image1.setImageBitmap(myBitmap);
                        }
                        else
                            image1.setImageResource(R.color.colorPrimary);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        };
    }
    public void mOnClose2(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

}