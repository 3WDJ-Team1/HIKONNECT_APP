package kr.ac.yjc.wdj.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dd.processbutton.ProcessButton;
import com.gc.materialdesign.views.ButtonRectangle;
import com.google.android.gms.tasks.OnCompleteListener;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import kr.ac.yjc.wdj.myapplication.APIs.HttpRequest.HttpRequestConnection;

/**
 * Created by jungyu on 2018-04-05.
 */

public class LoginActivity extends Activity  {
    String url = "http://172.26.1.80:8000/api/";
    EditText id,pw;
    TextView tv;
    ButtonRectangle login;
    ContentValues contentValues = new ContentValues();
    HttpRequestConnection hrc = new HttpRequestConnection();
    String result;
    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        id = findViewById(R.id.idtext);
        pw = findViewById(R.id.pwtext);
        tv = findViewById(R.id.tv);
        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    contentValues.put("id",id.getText().toString());
                    contentValues.put("pw",pw.getText().toString());
                    Log.i("넣엇다","요");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("넣엇다","쓰레드 시작");
                            result = hrc.request("http://172.26.1.80:8000/api/login_app", contentValues);
                            Log.i("넣엇다","불러오고");
                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                        }
                    }).start();
                    handler = new Handler() {
                        public void handleMessage(Message msg) {
                        switch (result) {
                            case "true":
                                Intent intent = new Intent(LoginActivity.this,MapsActivity.class);
                                intent.putExtra("id",id.getText().toString());
                                startActivity(intent);
                                finish();
                                break;
                            case "false":
                                tv.setText("존재하지 않는 아이디이거나 비밀번호가 다릅니다.");
                        }
                        }
                    };
                }catch (SecurityException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
