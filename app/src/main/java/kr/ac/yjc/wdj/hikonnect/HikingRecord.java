package kr.ac.yjc.wdj.hikonnect;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.ac.yjc.wdj.hikonnect.APIs.HttpRequest.HttpRequestConnection;

/**
 * Created by jungyu on 2018-04-25.
 */

public class HikingRecord extends Activity  {
    HttpRequestConnection hrc = new HttpRequestConnection();
    String result;
    TextView txtText,txtText2,txtText3;
    ContentValues contentValues = new ContentValues();
    Handler handler;
    String distance,speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.hiking_record);

        //UI 객체생성
        txtText = (TextView)findViewById(R.id.txtText);
        txtText2 = (TextView)findViewById(R.id.txtText2);
        txtText3 = (TextView)findViewById(R.id.txtText3);

        //데이터 가져오기
        Intent intent = getIntent();
        String user_id = intent.getStringExtra("name");
        txtText.setText("id:"+ user_id);
        txtText2.setText("속도");
        txtText3.setText("거리");

        contentValues.put("id", user_id);
        Log.i("@@@@@@@@@@@@@@@@@@@@@",user_id);

        /*new Thread(new Runnable() {

            @Override
            public void run() {
                result = hrc.request("http://hikonnect.ga:3000/",contentValues);
                Log.i("result", result);
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        }).start();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };*/
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
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
