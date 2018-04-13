package kr.ac.yjc.wdj.hikonnect.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RecoverySystem;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.apis.http_request.HttpRequestConnection;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

/**
 * Created by jungyu on 2018-04-05.
 */

public class LoginActivity extends Activity  {
    EditText id,pw;
    AVLoadingIndicatorView loginProgressBar;
    ButtonRectangle login;
    ContentValues contentValues = new ContentValues();
    HttpRequestConnection hrc = new HttpRequestConnection();
    String result;
    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        loginProgressBar = findViewById(R.id.login_progress_bar);

        id = findViewById(R.id.id_text);
        pw = findViewById(R.id.pw_text);

        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    contentValues.put("id",id.getText().toString());
                    contentValues.put("pw",pw.getText().toString());

                    OkHttpClient client = new OkHttpClient();

                    URL url = new URL("http://hikonnect.ga/api/loginprocess");

                    RequestBody body = new FormBody.Builder()
                        .add("idv", id.getText().toString())
                        .add("pwv", pw.getText().toString())
                        .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();

//                    loginProgressBar.setVisibility(View.VISIBLE);

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.d("Login activity", response.body().string());

                            Intent intent = new Intent(LoginActivity.this, MapsActivity1.class);
                            intent.putExtra("id", id.getText().toString());
                            startActivity(intent);

                            finish();
                        }
                    });
                } catch (SecurityException ex) {
                    ex.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
