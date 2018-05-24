package kr.ac.yjc.wdj.hikonnect.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.UsersData;
import kr.ac.yjc.wdj.hikonnect.activities.group_list.groups_list_main;
import kr.ac.yjc.wdj.hikonnect.activities.groups.GroupActivity;
import kr.ac.yjc.wdj.hikonnect.activities.myPage.UserGroupActivity;
import kr.ac.yjc.wdj.hikonnect.activities.session.SessionManager;
import kr.ac.yjc.wdj.hikonnect.activities.user.UserProfileActivity;
import kr.ac.yjc.wdj.hikonnect.adapters.MainPageAdapter;
import kr.ac.yjc.wdj.hikonnect.beans.Group;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * The Activity used app's main page
 * @author  Areum Lee (leear5799@gmail.com), Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-04-24
 */
public class MainActivity extends AppCompatActivity
            implements NavigationView.OnNavigationItemSelectedListener{
    ActionBarDrawerToggle   toggle;
    NavigationView          navigationView;
    SessionManager          session;
    LinearLayout            linearLayout;
    DrawerLayout            drawer;
    TextView                txtView;
    Toolbar                 toolbar;
    String                  user;
    String                  id;

    // 내부
    private TextView        nowScheduleTitle, NowScheduleContent; // 현재 산행 진행 중인 그룹 제목, 내용
    private ImageView       nowScheduleImg; // 현재 산행 진행중인 그룹 사진
    private Button          btnStartHiking; // 등산 시작 버튼
    private RecyclerView    rvNowJoined, rvNowRecruit;  // 현재 참여한 그룹, 현재 모집중인 그룹
    private ProgressBar     progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> userId = session.getUserId();
        id = userId.get(SessionManager.KEY_ID);

        /*txtView = (TextView) findViewById(R.id.main_textview);
        txtView.setText(id);*/

        txtView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.main_textview);

        txtView.setText(UsersData.USER_NAME);

        final CircularImageView imageView = (CircularImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_imageView);

        new AsyncTask<Void, Integer, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();

                    HttpUrl httpUrl = HttpUrl
                            .parse(Environments.NODE_HIKONNECT_IP + "/images/UserProfile/" + UsersData.USER_ID + ".jpg")
                            .newBuilder()
                            .build();

                    Request req = new Request.Builder().url(httpUrl).build();

                    Response res = okHttpClient.newCall(req).execute();

                    InputStream is = res.body().byteStream();

                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    return bitmap;
                } catch (IOException ie) {
                    ie.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                imageView.setImageBitmap(bitmap);
            }
        }.execute();

        //changeTxtName(id);

        // 내부 페이지 UI 초기화
        initInnerPageUI();
    }

   /* private void changeTxtName(String id) {
        txtView = (TextView) findViewById(R.id.main_textview);
        txtView.setText(id);
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            startActivity(new Intent(this, groups_list_main.class));
        } /*else if (id == R.id.my_groups) {
            startActivity(new Intent(this, UserGroupActivity.class));
        }*/ else if (id == R.id.my_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
        } else if (id == R.id.log_out) {
            session.logOutUser();
            //startActivity(new Intent(this, PreActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    /**
     * 내부 메인페이지 UI 초기화
     */
    private void initInnerPageUI() {
        final View contentMain = findViewById(R.id.layout_app_bar_main).findViewById(R.id.layout_content_main);

        // 뷰 붙이기
        nowScheduleImg      = (ImageView) contentMain.findViewById(R.id.nowScheduleImg);
        nowScheduleTitle    = (TextView) contentMain.findViewById(R.id.nowScheduleTitle);
        NowScheduleContent  = (TextView) contentMain.findViewById(R.id.nowScheduleContent);
        btnStartHiking      = (Button) contentMain.findViewById(R.id.btnStartHiking);
        rvNowJoined         = (RecyclerView) contentMain.findViewById(R.id.rvNowJoined);
        rvNowJoined.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
        rvNowRecruit        = (RecyclerView) contentMain.findViewById(R.id.rvNowRecruit);
        rvNowRecruit.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));

        progressBar         = (ProgressBar) contentMain.findViewById(R.id.startHikingProgressBar);

        // 어댑터 붙이기
        // TODO 데이터 http 로 받아와서 넣기
        ArrayList<Group> dataList = new ArrayList<>();
        dataList.add(new Group("test1"));
        dataList.add(new Group("test2"));
        dataList.add(new Group("test3"));
        dataList.add(new Group("test4"));
        dataList.add(new Group("test5"));
        dataList.add(new Group("test6"));

        rvNowJoined.setAdapter(new MainPageAdapter(dataList, R.layout.main_list_item));
        rvNowRecruit.setAdapter(new MainPageAdapter(dataList, R.layout.main_list_item));

        // 리스너 붙이기
        // 등산 시작 버튼 클릭 시 맵으로 이동
        btnStartHiking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MapsActivityTemp.class);
                intent.putExtra("id", UsersData.USER_ID);
                progressBar.setVisibility(View.VISIBLE);
                startActivity(intent);
                finish();
            }
        });
    }
}

