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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import kr.ac.yjc.wdj.hikonnect.Environment;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.UsersData;
import kr.ac.yjc.wdj.hikonnect.apis.HttpRequest.HttpRequestConnection;
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

    ContentValues   contentValues = new ContentValues();
    OkHttpClient    hrc = new OkHttpClient();

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
                        try {
                            // JSON 객체 생성
                            String jsonObj = "{" +
                                    "\"idv\":\"" + id.getText().toString() + "\"," +
                                    "\"pwv\":\"" + pw.getText().toString() + "\"" +
                                    "}";

                            Log.d("json", jsonObj);

                            // 리퀘스트 바디 생성
                            RequestBody body = RequestBody.create(Environment.JSON, jsonObj);

                            // 리퀘스트 객체 생성
                            Request request = new Request.Builder()
                                    .url(Environment.LARAVEL_SOL_SERVER + "/loginprocess")
                                    .post(body)
                                    .build();

                            // 전송
                            Response result = hrc.newCall(request).execute();
//                            Log.d("HIKONNECT", "SIGN IN res: " + result.body().string());

                            String resultJson = result.body().string();

                            Log.d("result", resultJson);

                            // 결과 값이 "false" --> 아이디 없음
                            if (resultJson.equals("\"false\"")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv.setText("ID를 확인해주세요.");
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            } else if (resultJson.equals("\"pwfalse\"")) {
                                // 결과 값이 "pwfalse" --> 비밀번호 틀림
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv.setText("비밀번호를 확인해주세요.");
                                        progressBar.setVisibility(View.GONE);
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

                                    UsersData.USER_ID       = jObj.getString("userid");
                                    UsersData.USER_PASSWORD = jObj.getString("password");
                                    UsersData.USER_NAME     = jObj.getString("nickname");
                                    UsersData.PHONE         = jObj.getString("phone");
                                    UsersData.GENDER        = jObj.getInt("gender") == 0 ? "남자" : "여자";
                                    UsersData.AGE_GROUP     = jObj.getInt("age_group");
                                    UsersData.OPEN_SCOPE    = jObj.getInt("scope");
                                    UsersData.PROFILE_URL   = jObj.getString("profile");

                                } catch (JSONException je) {
                                    je.printStackTrace();
                                }

                                startActivity(intent);
                                finish();
                            }

                        } catch (IOException ie) {
                            ie.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}
