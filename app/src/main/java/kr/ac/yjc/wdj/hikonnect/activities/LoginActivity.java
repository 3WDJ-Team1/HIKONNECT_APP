package kr.ac.yjc.wdj.hikonnect.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author  Jungyu Choi, Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-04-05
 */
public class LoginActivity extends Activity {

    EditText        id,
                    pw;
    TextView        tv;
    Button          login;
    ProgressBar     progressBar;

    ContentValues   contentValues   = new ContentValues();
    OkHttpClient    hrc             = new OkHttpClient();

    String          result;

    private SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_designed);

        pref = getSharedPreferences("loginData", MODE_PRIVATE);

        // 로그인 검사 후 액티비티 전환.
        if (isLogin()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade, R.anim.hold);
        }

        id          = (EditText)    findViewById(R.id.id_editText);
        pw          = (EditText)    findViewById(R.id.pw_editText);
        tv          = (TextView)    findViewById(R.id.tv);
        login       = (Button)      findViewById(R.id.signInBtn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tv.setText("로그인 중...");
                contentValues.put("id", id.getText().toString());
                contentValues.put("pw", pw.getText().toString());
                login.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // JSON 객체 생성
                            String jsonObj = "{" +
                                    "\"idv\":\"" + id.getText().toString() + "\"," +
                                    "\"pwv\":\"" + pw.getText().toString() + "\"" +
                                    "}";

                            Log.d("json", jsonObj);

                            // 리퀘스트 바디 생성
                            RequestBody body = RequestBody.create(Environments.JSON, jsonObj);

                            // 리퀘스트 객체 생성
                            Request request = new Request.Builder()
                                    .url(Environments.LARAVEL_HIKONNECT_IP + "/api/loginprocess")
                                    .post(body)
                                    .build();

                            // 전송
                            Response result = hrc.newCall(request).execute();
                            // Log.d("HIKONNECT", "SIGN IN res: " + result.body().string());

                            String resultJson = result.body().string();

                            // 결과 값이 "false" --> 아이디 없음
                            if (resultJson.equals("\"false\"")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv.setText("ID를 확인해주세요.");
                                        progressBar.setVisibility(View.GONE);
                                        login.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else if (resultJson.equals("\"pwfalse\"")) {
                                // 결과 값이 "pwfalse" --> 비밀번호 틀림
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv.setText("비밀번호를 확인해주세요.");
                                        progressBar.setVisibility(View.GONE);
                                        login.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv.setText("로그인 성공");
                                    }
                                });

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                // 데이터 저장
                                try {
                                    JSONObject jObj = new JSONObject(resultJson);

                                    // 어플리케이션 저장공간에 로그인 정보 저장.
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("user_id",             jObj.getString("userid"));
                                    editor.putString("user_password",       jObj.getString("password"));
                                    editor.putString("user_name",           jObj.getString("nickname"));
                                    editor.putString("user_phone",          jObj.getString("phone"));
                                    editor.putString("user_gender",         jObj.getInt("gender") == 0 ? "남자" : "여자");
                                    editor.putString("user_age_group",      jObj.getString("age_group"));
                                    editor.putString("user_open_scope",     jObj.getString("scope"));
                                    editor.putString("user_profile_url",    jObj.getString("profile"));
                                    editor.apply();

                                } catch (JSONException je) {
                                    je.printStackTrace();
                                }

                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.fade, R.anim.hold);
                            }

                        } catch (IOException ie) {
                            ie.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    private boolean isLogin() {
        SharedPreferences pref = getSharedPreferences("loginData", MODE_PRIVATE);
        String userID = pref.getString("user_id", "");

        if (userID.equals("")) {
            return false;
        }
        return true;
    }
}
