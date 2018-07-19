package kr.ac.yjc.wdj.hikonnect.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.group_list.groups_list_main;
import kr.ac.yjc.wdj.hikonnect.activities.myPage.MyMenuActivity;
import kr.ac.yjc.wdj.hikonnect.activities.myPage.UserJoinedGroup;
import kr.ac.yjc.wdj.hikonnect.activities.myPage.UserProfileActivity;
import kr.ac.yjc.wdj.hikonnect.activities.myPage.UserRecordActivity;
import kr.ac.yjc.wdj.hikonnect.activities.session.SessionManager;
import kr.ac.yjc.wdj.hikonnect.adapters.MainActivityScheduleAdapter;
import kr.ac.yjc.wdj.hikonnect.apis.fcm_manager.TokenManager;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * The Activity used app's main page
 * @author  Areum Lee (leear5799@gmail.com)         drawer
 *          Sungeun Kang (kasueu0814@gmail.com)     툴바, 내부 페이지
 * @since   2018-04-24
 */
public class MainActivity extends AppCompatActivity
            implements NavigationView.OnNavigationItemSelectedListener{

    // 보여지는 Fragment
    private Fragment curFragment = new Fragment();

    ActionBarDrawerToggle   toggle;
    NavigationView          navigationView;
    SessionManager          session;
    DrawerLayout            drawer;
    TextView                txtView;
    Toolbar                 toolbar;
    String                  id;

    private static int      nowScheduleIndex;
    // 내부
    private LinearLayout    nowScheduleEmptyLayout;
    private RelativeLayout  nowScheduleLoading;

    private RelativeLayout                  nowScheduleViewPagerWrapper;
    private ViewPager                       scheduleViewPager;
    private MainActivityScheduleAdapter     scheduleAdapter;

    private Button          nowSchedulePrevBtn;
    private Button          nowScheduleNextBtn;

    private ImageView       nowScheduleImg;     // 현재 산행 진행 중인 그룹 사진
    private Button          btnStartHiking,     // 등산 시작 버튼
                            btnToGroupMenu,     // 그룹 메뉴 버튼
                            btnToMyMenu,        // 마이 메뉴 버튼
                            btnNowSchduleSearch;

    // HTTP request
    private OkHttpClient    client;

    // 로딩화면
    private LoadingDialog   loadingDialog;

    // 세션 유지
    private SharedPreferences   pref;

    // 가장 가까운 날짜의 일정 ID.
    private String              scheduleNum;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("loginData", MODE_PRIVATE);

        // UI 초기화
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> userId = session.getUserId();
        id = userId.get(SessionManager.KEY_ID);

        /*txtView = (TextView) findViewById(R.id.main_textview);
        txtView.setText(id);*/

        txtView = navigationView.getHeaderView(0).findViewById(R.id.main_textview);

        txtView.setText(pref.getString("user_name", ""));

        final CircularImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.nav_imageView);

        // OKHttpClient 초기화
        client = new OkHttpClient();

        // drawer에 있는 사진 받아오기
        new AsyncTask<Void, Integer, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    HttpUrl httpUrl = HttpUrl
                            .parse(Environments.NODE_HIKONNECT_IP + "/images/UserProfile/" + pref.getString("user_id", "") + ".jpg")
                            .newBuilder()
                            .build();

                    Request req = new Request.Builder().url(httpUrl).build();

                    Response res = client.newCall(req).execute();

                    InputStream is = res.body().byteStream();

                    return BitmapFactory.decodeStream(is);
                } catch (IOException ie) {
                    ie.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                if (bitmap != null) {

                    imageView.setImageBitmap(bitmap);

                } else {

                    BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(getBaseContext(), R.drawable.circle_solid_profile_512px);
                    Bitmap defaultImg = null;
                    if (drawable != null) {
                        defaultImg = drawable.getBitmap();
                    }

                    if (defaultImg != null) {
                        imageView.setImageBitmap(Bitmap.createScaledBitmap(defaultImg, 50, 50, true));
                    }

                }
            }
        }.execute();

        // 내부 페이지 UI 초기화
        initInnerPageUI();
        initFCMService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        item.setEnabled(false);

        if (id == R.id.groups) {
            startActivity(new Intent(this, groups_list_main.class));
        } else if (id == R.id.my_groups) {
            startActivity(new Intent(this, UserJoinedGroup.class));
        } else if (id == R.id.my_records) {
            startActivity(new Intent(this, UserRecordActivity.class));
        } else if (id == R.id.my_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
        } else if (id == R.id.log_out) {
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.apply();

            MapsActivityTemp.timer.cancel();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // session.logOutUser();
            // startActivity(new Intent(this, PreActivity.class));
        }

        item.setEnabled(true);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    /**
     * 내부 메인페이지 UI 초기화
     */
    private void initInnerPageUI() {

        nowScheduleEmptyLayout  = findViewById(R.id.nowScheduleEmptyLayout);
        nowScheduleLoading      = findViewById(R.id.nowScheduleLoading);

        nowScheduleViewPagerWrapper = findViewById(R.id.nowScheduleViewPagerWrapper);
        scheduleViewPager = findViewById(R.id.nowScheduleViewPager);
        scheduleAdapter = new MainActivityScheduleAdapter(getSupportFragmentManager(), pref);
        scheduleViewPager.setAdapter(scheduleAdapter);

        nowSchedulePrevBtn = findViewById(R.id.nowSchedulePrevBtn);
        nowScheduleNextBtn = findViewById(R.id.nowScheduleNextBtn);

        // btnStartHiking          = findViewById(R.id.btnStartHiking);      // 로딩 화면 초기화
        btnToGroupMenu          = findViewById(R.id.btnToGroupMenu);
        btnToMyMenu             = findViewById(R.id.btnToMyMenu);
        btnNowSchduleSearch     = findViewById(R.id.nowScheduleSearchGroup);

        loadingDialog       = new LoadingDialog(this);

        /*// 리스너 붙이기
        // 등산 시작 버튼 클릭 시 맵으로 이동
        btnStartHiking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStartHiking.setClickable(false);

                loadingDialog.show();
                Intent intent = new Intent(getBaseContext(), MapsActivityTemp.class);
                intent.putExtra("schedule_no", scheduleNum);
                startActivity(intent);
                finish();
            }
        });*/

        // 그룹 메뉴 버튼 누르면 그룹 메뉴로 이동
        btnToGroupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnToGroupMenu.setClickable(false);
                Intent intent = new Intent(getBaseContext(), groups_list_main.class);
                startActivity(intent);
                btnToGroupMenu.setClickable(true);
            }
        });

        // 일정 조회 누르면 다시 일정 조회.
        btnNowSchduleSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGroupsInfo(pref.getString("user_id", "null"));
            }
        });

        // 마이 메뉴 버튼 누르면 마이 메뉴로 이동
        btnToMyMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnToMyMenu.setClickable(false);
                Intent intent = new Intent(getBaseContext(), MyMenuActivity.class);
                startActivity(intent);
                btnToMyMenu.setClickable(true);
            }
        });

        nowSchedulePrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nowScheduleIndex > 0) {
                    scheduleViewPager.setCurrentItem(--nowScheduleIndex, true);
                }
            }
        });
        nowScheduleNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nowScheduleIndex < scheduleAdapter.getCount()) {
                    scheduleViewPager.setCurrentItem(++nowScheduleIndex, true);
                }
            }
        });

        getGroupsInfo(pref.getString("user_id", "null"));
    }

    private void initFCMService() {
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();
    }

    /**
     * 현재 유저가 참여하고 있는 그룹 찾기
     * @param userId    유저 아이디
     */
    @SuppressLint("StaticFieldLeak")
    private void getGroupsInfo(String userId) {
        final String loginId = userId;

        new AsyncTask<String, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                nowScheduleLoading.setVisibility(View.VISIBLE);
//                nowScheduleLayout.setVisibility(View.GONE);
                nowScheduleEmptyLayout.setVisibility(View.GONE);
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    RequestBody body = new FormBody.Builder()
                            .add("user_id", loginId)
                            .build();

                    Request request = new Request.Builder()
                            .url(Environments.LARAVEL_HIKONNECT_IP + "/api/getNowSchedule")
                            .post(body)
                            .build();

                    Response response = client.newCall(request).execute();

                    return response.body().string();
                } catch (IOException ie) {
                    Log.e("IOException", "IOException in MainActivity.getGroupsInfo()\n" + ie);
                }

                return null;
            }

            @SuppressLint("SimpleDateFormat")
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                nowScheduleLoading.setVisibility(View.GONE);

                try {

                    JSONParser jsonParser = new JSONParser();

                    JSONArray jsonArray = (JSONArray) jsonParser.parse(s);

                    if(jsonArray.isEmpty()) {
                        // 계획된 일정이 없다는 알림 표시.
                        nowScheduleEmptyLayout.setVisibility(View.VISIBLE);
                    } else {
                        scheduleAdapter = new MainActivityScheduleAdapter(getSupportFragmentManager(), pref);
                        scheduleViewPager.setAdapter(scheduleAdapter);
                        nowScheduleViewPagerWrapper.setVisibility(View.VISIBLE);
                    }
                } catch (ParseException | NullPointerException pse) {
                    Log.e(Environments.APP_TAG, "onPostExecute: ", pse);
                    nowScheduleEmptyLayout.setVisibility(View.VISIBLE);
                }/* catch (java.text.ParseException e) {
                    e.printStackTrace();
                }*/
            }
        }.execute();
    }
}

