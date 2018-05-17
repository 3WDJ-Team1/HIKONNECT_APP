package kr.ac.yjc.wdj.hikonnect.activities.groups;

import android.content.ContentValues;
import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.session.SessionManager;
import kr.ac.yjc.wdj.hikonnect.apis.http_request.GHttpRequestConnection;
import kr.ac.yjc.wdj.hikonnect.apis.http_request.HttpRequestConnection;

/**
 * Created by LEE AREUM on 2018-05-08.
 */

public class ScheduleActivity extends AppCompatActivity {
   /* GHttpRequestConnection hrc = new GHttpRequestConnection();
    String result;
    Handler handler;
    ScheduleAdapter list_adapter;
    RecyclerView recyclerView;
    SessionManager session;
    LinearLayoutManager layoutManager;
    private List<ScheduleListItem> listItems;
    String title;
    String leader;
    String id;
    String groupUuid;
    ContentValues contentValues = new ContentValues();
    int page;
    Button joinBtn, groupPageBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_list_recyclerview);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> userId = session.getUserId();
        id = userId.get(SessionManager.KEY_ID);

        *//*toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        id = userId.get(SessionManager.KEY_ID);*//*

        Intent intent = getIntent();
        groupUuid = intent.getStringExtra("groupUuid");
        Log.d("groupuuid", groupUuid);

        recyclerView = (RecyclerView) findViewById(R.id.sRecyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        page = 10;

        loadRecyclerViewData();

        *//*int ITEM_SIZE = 5;

        List<ScheduleListItem> items = new ArrayList<>();
        ScheduleListItem[] item = new ScheduleListItem[ITEM_SIZE];
        item[0] = new ScheduleListItem("일정입니다", "admin");
        item[1] = new ScheduleListItem("가자", "admin");
        item[2] = new ScheduleListItem("가자고", "test");
        item[3] = new ScheduleListItem("ㅇㄹ", "test");
        item[4] = new ScheduleListItem("test입니다", "test");

        for (int i = 0; i < ITEM_SIZE; i++) {
            items.add(item[i]);
        }

        recyclerView.setAdapter(new ScheduleAdapter(getApplicationContext(), items));*//*
    }

    public void loadRecyclerViewData() {
        contentValues.put("groupuuid", groupUuid);

        new Thread(new Runnable() {
            @Override
            public void run() {
                result = hrc.request("http://172.25.1.204:8000/api/schedule", contentValues);
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        }).start();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    JSONArray array = new JSONArray(result);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject o = array.getJSONObject(i);
                        title = o.getString("title");
                        leader = o.getString("leader");
                        ScheduleListItem item = new ScheduleListItem(title, leader);
                        listItems.add(item);
                    }
                    list_adapter = new ScheduleAdapter(getApplicationContext(), listItems);
                    recyclerView.setAdapter(list_adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    @Override
    public void onBackPressed() {
        *//*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*//*
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
*/
    /*@Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.groups) {
            startActivity(new Intent(this, GroupActivity.class));
        } else if (id == R.id.my_groups) {
            startActivity(new Intent(this, UserGroupActivity.class));
        } else if (id == R.id.my_records) {
            startActivity(new Intent(this, UserRecordActivity.class));
        } else if (id == R.id.my_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
        } else if (id == R.id.log_out) {
            //session.logOutUser();
            //startActivity(new Intent(this, PreActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }*/
}
