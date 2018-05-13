package kr.ac.yjc.wdj.hikonnect;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.apis.HttpRequest.HttpRequestConnection;


/**
 * @author  Jungyu Choi
 * @since   2018-04-11
 */
public class  Locationmemo extends Activity {

    int location_num;
    String titlestring, contentstring,path,writer,result;
    Handler handler;
    TextView content,title,writertv;
    Bitmap bitmap;
    ContentValues contentValues;
    HttpRequestConnection hrc;
    ImageView image1;
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
        writertv = findViewById(R.id.idtext);

        contentValues = new ContentValues();
        hrc = new HttpRequestConnection();

        Intent intent = getIntent();
        location_num= intent.getIntExtra("location_no",0);

        contentValues.put("location_no",location_num);
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = hrc.request(Environment.LARAVEL_HIKONNECT_IP+"/api/getLocationMemoDetail",contentValues);
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
                        //path       = jsonObject.getString("image_path");
                        writer      = jsonObject.getString("writer");
                        TedPermission.with(Locationmemo.this)
                                .setPermissionListener(permissionlistener)
                                .setRationaleMessage("사진을 보려면 권한이 필요함")
                                .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                                .check();

                        //path = path.replaceAll("\\/","/");
                        title.setText(titlestring);
                        content.setText(contentstring);
                        writertv.setText(writer);
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