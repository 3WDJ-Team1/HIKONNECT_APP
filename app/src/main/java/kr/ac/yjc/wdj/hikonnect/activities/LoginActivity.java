package kr.ac.yjc.wdj.hikonnect.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import kr.ac.yjc.wdj.hikonnect.Environment;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.apis.HttpRequest.HttpRequestConnection;

/**
 * @author  Jungyu Choi, Sungeun Kang
 * @since   2018-04-05
 */
public class LoginActivity extends Activity {

    EditText                id,
                            pw;
    TextView                tv;
    Button                  login;
    ProgressBar             progressBar;

    ContentValues           contentValues = new ContentValues();
    HttpRequestConnection   hrc = new HttpRequestConnection();

    String                  result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        id          = (EditText) findViewById(R.id.id_editText);
        pw          = (EditText) findViewById(R.id.pw_editText);
        tv          = (TextView) findViewById(R.id.tv);
        login       = (Button) findViewById(R.id.signInBtn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tv.setText("로그인 중...");
                contentValues.put("id", id.getText().toString());
                contentValues.put("pw", pw.getText().toString());
                progressBar.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        result = hrc.request(Environment.LARAVEL_HIKONNECT_IP + "/api/login_app", contentValues);

                        Log.d("HIKONNECT", "SIGN IN res: " + result);
                        if (Boolean.valueOf(result)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv.setText("로그인 성공");
                                    progressBar.setVisibility(View.VISIBLE);
                                }
                            });
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("id", id.getText().toString());
                            startActivity(intent);
                            finish();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv.setText("ID와 비밀번호를 확인해주세요.");
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
}
