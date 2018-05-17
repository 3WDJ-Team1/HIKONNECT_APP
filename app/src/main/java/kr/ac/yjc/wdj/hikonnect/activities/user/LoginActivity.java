package kr.ac.yjc.wdj.hikonnect.activities.user;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.net.URL;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.MainActivity;
import kr.ac.yjc.wdj.hikonnect.activities.session.SessionManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @file        kr.ac.yjc.wdj.hikonnect.activities.MainActivity.java
 * @author      Areum Lee (leear5799@gmail.com)
 * @since       2018-04-24
 * @brief       The Activity used when a user logs in
 */

public class LoginActivity extends AppCompatActivity {
    ActionBarDrawerToggle       dtToggle;
    SessionManager session;
    RelativeLayout              rLayout;
    DrawerLayout                dlDrawer;
    EditText                    id, pw;
    Toolbar                     myToolbar;
    Button                      login, cancle;
    String                      user;

    ContentValues contentValues = new ContentValues();

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

        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
        dlDrawer.addDrawerListener(dtToggle);

        session = new SessionManager(getApplicationContext());

        id      = findViewById(R.id.id_editText);
        pw      = findViewById(R.id.pw_editText);

        login   = findViewById(R.id.signInBtn);
        cancle  = findViewById(R.id.cancelBtn);

        rLayout = (RelativeLayout) findViewById(R.id.login_layout);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    contentValues.put("id",id.getText().toString());
                    contentValues.put("pw",pw.getText().toString());

                    OkHttpClient client = new OkHttpClient();

                    URL url = new URL("http://172.26.1.5:8000/api/login_app");

                    RequestBody body = new FormBody.Builder()
                            .add("id", id.getText().toString())
                            .add("pw", pw.getText().toString())
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            user = id.getText().toString();

                            session.createLogInSession(user);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);

                            finish();
                        }
                    });

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        // 화면 터치 시, 키보드 자판 없어짐
        rLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imManager.hideSoftInputFromWindow(login.getWindowToken(), 0);
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
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