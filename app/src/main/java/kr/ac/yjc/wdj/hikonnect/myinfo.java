package kr.ac.yjc.wdj.hikonnect;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import kr.ac.yjc.wdj.hikonnect.APIs.HttpRequest.HttpRequestConnection;

/**
 * Created by jungyu on 2018-05-09.
 */

public class myinfo extends Activity {
    String result;
    TextView myText,myText2,myText3;
    String distance,speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.hiking_record);

        //UI 객체생성
        myText = (TextView)findViewById(R.id.myText);
        myText2 = (TextView)findViewById(R.id.myTex2);
        myText3 = (TextView)findViewById(R.id.myTex3);

        //데이터 가져오기
        Intent intent = getIntent();
        //String user_id = intent.getStringExtra("userid");
        Double velocity = intent.getDoubleExtra("velocity",0);
        Double distance = intent.getDoubleExtra("distance",0);
        //String alldistance = intent.getStringExtra("alldistance");
       // myText.setText("id:"+ user_id);
        myText2.setText("속도"+velocity+"m/s");
        myText3.setText("거리"+distance);



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
