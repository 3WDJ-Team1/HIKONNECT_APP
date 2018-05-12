package kr.ac.yjc.wdj.hikonnect;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;

import kr.ac.yjc.wdj.hikonnect.APIs.HttpRequest.HttpRequestConnection;

/**
 * Created by jungyu on 2018-04-05.
 */

public class LoginActivity extends Activity {

    private String          laravel_local_ip,
                            node_local_ip;
    EditText                id,
                            pw;
    TextView                tv;
    ButtonRectangle         login;
    ProgressBar             progressBar;

    ContentValues           contentValues = new ContentValues();
    HttpRequestConnection   hrc = new HttpRequestConnection();

    String                  result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        laravel_local_ip    = getString(R.string.laravel_local_server_ip);
        node_local_ip       = getString(R.string.node_local_server_ip);

        id          = (EditText) findViewById(R.id.idtext);
        pw          = (EditText) findViewById(R.id.pwtext);
        tv          = (TextView) findViewById(R.id.tv);
        login       = (ButtonRectangle) findViewById(R.id.login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tv.setText("로그인 중...");
                contentValues.put("id", id.getText().toString());
                contentValues.put("pw", pw.getText().toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        result = hrc.request(getString(R.string.laravel_local_server_ip)+ "/api/login_app", contentValues);

                        Log.d("HIKONNECT", "SIGN IN res: " + result);
                        if (Boolean.valueOf(result)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv.setText("로그인 성공");
                                    progressBar.setVisibility(View.VISIBLE);
                                }
                            });
                            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                            intent.putExtra("id", id.getText().toString());
                            startActivity(intent);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv.setText("ID와 비밀번호를 확인해주세요.");
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
}
