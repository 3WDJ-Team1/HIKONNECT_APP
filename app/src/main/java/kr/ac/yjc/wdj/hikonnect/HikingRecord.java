package kr.ac.yjc.wdj.hikonnect;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import kr.ac.yjc.wdj.hikonnect.apis.HttpRequest.HttpRequestConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author  Jungyu Choi
 * @since   2018-04-25
 */
public class HikingRecord extends Activity  {
    HttpRequestConnection   hrc = new HttpRequestConnection();
    String                  result,nickname,profile,hiking_group;
    int                     ranking;
    double                  distancee,velocity,my_distance,all_distance;

    TextView                txtText,txtText2,txtText3,txtText4,rank,txt5,txtText6;
    ContentValues           contentValues = new ContentValues();
    Handler                 handler;
    String                  distance,speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.hiking_record);

        //UI 객체생성
        txtText     = (TextView)findViewById(R.id.txtText);
        rank        = (TextView)findViewById(R.id.rank);
        txtText2    = (TextView)findViewById(R.id.txtText2);
        txtText3    = (TextView)findViewById(R.id.txtText3);
        txtText4    = (TextView)findViewById(R.id.txtText4);
        txt5        = (TextView)findViewById(R.id.txtText5);
        txtText6    = (TextView)findViewById(R.id.txtText6);


        //데이터 가져오기
        Intent  intent      = getIntent();
        my_distance        = intent.getDoubleExtra("my_distance",-1);
        all_distance        = intent.getDoubleExtra("all_distance",0)*1000;
        final int     member_no   = intent.getIntExtra("member_no",0);



        contentValues.put("member_no", member_no);
        Log.d("member_no@#", String.valueOf(member_no)+my_distance+all_distance);
        new Thread(new Runnable() {

            @Override
            public void run() {
                result = hrc.request(Environment.LARAVEL_HIKONNECT_IP+"/api/getMemberDetail",contentValues);
                Log.i("result", result);
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        }).start();
        handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                try {
                    JSONArray jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        nickname            = jsonObject.getString("nickname");
                        hiking_group        = jsonObject.getString("hiking_group");
                        distancee           = jsonObject.getDouble("distance");
                        velocity            = jsonObject.getDouble("velocity");
                        ranking             =  jsonObject.getInt("rank");

                        String velo     = String.format("%.1f",velocity);
                        txtText.setText("닉네임:"+ nickname);
                        rank    .setText("순위:"+ranking);
                        txtText2.setText("그룹:" + hiking_group);
                        double result_distance1 = Math.abs(my_distance - distancee);
                        String  result_distance2 = String.format("%.1f",result_distance1);
                        txtText3.setText("거리:"+ distancee);
                        txtText4.setText("속도:"+velo+"m/s");
                        if (my_distance != -1 )
                        txtText6.setText("나와의 거리:"+result_distance2);
                        txt5.setText("퍼센트:"+String.format("%.1f",(distancee/all_distance)*100));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

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
