package kr.ac.yjc.wdj.hikonnect.activities.user;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.group.GroupActivity;
import kr.ac.yjc.wdj.hikonnect.activities.session.SessionManager;
import kr.ac.yjc.wdj.hikonnect.apis.http_request.HttpRequestConnection;

public class UserProfileActivity extends AppCompatActivity
            implements NavigationView.OnNavigationItemSelectedListener{
    ActionBarDrawerToggle   toggle;
    NavigationView          navigationView;
    DrawerLayout            drawer;
    Toolbar                 toolbar;
    ImageView               userProfileImg;
    TextView                idTxt, nicknameTxt, phoneTxt, ageTxt, genderTxt;
    SessionManager          session;
    String                  id, nickname, phoneNum, age, gender;
    HttpRequestConnection hrc = new HttpRequestConnection();
    String result;
    Handler handler;
    ContentValues contentValues = new ContentValues();
    Button home;
    SharedPreferences pref = getSharedPreferences("loginData", MODE_PRIVATE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        pref = getSharedPreferences("loginData", MODE_PRIVATE);

        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/

        //loadRecyclerViewData();

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> userId = session.getUserId();
        id = userId.get(SessionManager.KEY_ID);

        userProfileImg = (ImageView) findViewById(R.id.user_profile_img);

        idTxt       = (TextView) findViewById(R.id.user_id_txt);
        nicknameTxt = (TextView) findViewById(R.id.user_nickname_txt);
        phoneTxt    = (TextView) findViewById(R.id.user_phoneNum_txt);
        ageTxt      = (TextView) findViewById(R.id.user_age_txt);
        genderTxt   = (TextView) findViewById(R.id.user_gender_txt);

//        home        = (Button) findViewById(R.id.back_to_main_);

        // 세션의 id값으로 설정
        idTxt.setText(id);

        /*nicknameTxt.setText("test");
        phoneTxt.setText("010-0000-0000");
        ageTxt.setText("10대");
        genderTxt.setText("여자");*/

        /*home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });*/
    }
    public void loadRecyclerViewData() {
        contentValues.put("userid", id);

        new Thread(new Runnable() {
            @Override
            public void run() {
                result = hrc.request(Environments.LARAVEL_HIKONNECT_IP + "/api/userinfo", contentValues);
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        }).start();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    JSONArray array = new JSONArray(result);

                    JSONObject obj = array.getJSONObject(0);
                    nickname = obj.getString("nickname");
                    phoneNum = obj.getString("phone");
                    age      = obj.getString("age_group");
                    gender   = obj.getString("gender");

                    nicknameTxt.setText(nickname);
                    phoneTxt.setText(phoneNum);
                    ageTxt.setText(age);
                    genderTxt.setText(gender);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.groups) {
            //startActivity(new Intent(this, GroupActivity.class));
        } /*else if (id == R.id.my_groups) {
            startActivity(new Intent(this, UserGroupActivity.class));
        }*/ else if (id == R.id.my_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
        } else if (id == R.id.log_out) {

            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
//            session.logOutUser();
            //startActivity(new Intent(this, PreActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
