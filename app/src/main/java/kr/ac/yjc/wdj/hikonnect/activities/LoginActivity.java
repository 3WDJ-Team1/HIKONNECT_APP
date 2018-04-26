package kr.ac.yjc.wdj.hikonnect.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.RecoverySystem;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

//import com.gc.materialdesign.views.ButtonRectangle;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import kr.ac.yjc.wdj.hikonnect.R;
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
 * @file        kr.ac.yjc.wdj.hikonnect.activities.MainActivity.java
 * @author      Jungyu Choi (wnsrb0147@gmail.com), Areum Lee (leear5799@gmail.com)
 * @since       2018-04-05
 * @brief       The Activity used when a user logs in
 */

public class LoginActivity extends AppCompatActivity  {
    private EditText                id,pw;
    //AVLoadingIndicatorView loginProgressBar;
    private Button                  login;
    ContentValues contentValues = new ContentValues();
//    HttpRequestConnection hrc = new HttpRequestConnection();
    private String                  result;
    private Handler                 handler;
    private Intent                  intent;
    private Toolbar                 myToolbar;
    private DrawerLayout            dlDrawer;
    private ActionBarDrawerToggle   dtToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        myToolbar   = (Toolbar) findViewById(R.id.my_toolbar);
        dlDrawer    = (DrawerLayout) findViewById(R.id.drawer_layout);

        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        dtToggle    = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
        dlDrawer.addDrawerListener(dtToggle);


        //loginProgressBar = findViewById(R.id.login_progress_bar);

        id = findViewById(R.id.id_editText);
        pw = findViewById(R.id.pw_editText);

        login = findViewById(R.id.signInBtn);

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
                            Log.d("LoginActivity", response.body().string());

                            intent = new Intent(LoginActivity.this, MainActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        dtToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        dtToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (dtToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
