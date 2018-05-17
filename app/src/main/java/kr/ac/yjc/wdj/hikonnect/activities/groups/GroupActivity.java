package kr.ac.yjc.wdj.hikonnect.activities.groups;


/**
 * @file        kr.ac.yjc.wdj.hikonnect.activities.GroupActivity.java
 * @author      Areum Lee (leear5799@gmail.com)
 * @since       2018-04-30
 * @brief       The Activity used that get grouplist
 */

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.myPage.UserGroupActivity;
import kr.ac.yjc.wdj.hikonnect.activities.myPage.UserProfileActivity;
import kr.ac.yjc.wdj.hikonnect.activities.session.SessionManager;
import kr.ac.yjc.wdj.hikonnect.apis.http_request.HttpRequestConnection;

public class GroupActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    SessionManager session;
    DrawerLayout drawer;
    Toolbar toolbar;
    HttpRequestConnection hrc = new HttpRequestConnection();
    String result;
    Handler handler;
    GroupAdapter list_adapter;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    private ArrayList<GroupListItem> listItems;
    String title;
    String writer;
    String groupUuid;
    String id;
    ContentValues contentValues = new ContentValues();
    int page;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_list_recyclerview);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> userId = session.getUserId();
        id = userId.get(SessionManager.KEY_ID);

        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        id = userId.get(SessionManager.KEY_ID);*/

        recyclerView = (RecyclerView) findViewById(R.id.group_list_recyclerview);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        loadRecyclerViewData();

        page = 10;

        /*listItems = new ArrayList<>();

        int ITEM_SIZE = 12;

        List<GroupListItem> items = new ArrayList<>();
        GroupListItem[] item = new GroupListItem[ITEM_SIZE];
        item[0] = new GroupListItem("송소리", "소리소리송소리","1");
        item[1] = new GroupListItem("뭐", "test", "2");
        item[2] = new GroupListItem("좀되라", "admin", "3");
        item[3] = new GroupListItem("송소리", "소리소리송소리", "4");
        item[4] = new GroupListItem("뭐", "test", "5");
        item[5] = new GroupListItem("좀되라", "admin", "6");
        item[6] = new GroupListItem("송소리", "소리소리송소리", "7");
        item[7] = new GroupListItem("뭐", "test", "8");
        item[8] = new GroupListItem("좀되라", "admin", "9");
        item[9] = new GroupListItem("송소리", "소리소리송소리", "10");
        item[10] = new GroupListItem("뭐", "test", "11");
        item[11] = new GroupListItem("좀되라", "admin", "12");

        for (int i = 0; i < ITEM_SIZE; i++) {
            items.add(item[i]);
        }*/

        //recyclerView.setAdapter(new GroupAdapter(getApplicationContext(), items));

    }

    public void loadRecyclerViewData() {
        contentValues.put("select", "");
        contentValues.put("input", "");
        contentValues.put("page", 10);

        new Thread(new Runnable() {
            @Override
            public void run() {
                result = hrc.request("http://172.26.1.145:8000/api/groupList", contentValues);
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        }).start();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    JSONArray array = new JSONArray(result);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        title = obj.getString("title");
                        writer = obj.getString("nickname");
                        groupUuid = obj.getString("uuid");

                        listItems.add(new GroupListItem(title, writer, groupUuid));
                    }

                    list_adapter = new GroupAdapter(GroupActivity.this, listItems);
                    recyclerView.setAdapter(list_adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        /*if (drawer.isDrawerOpen(GravityCompat.START)) {
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
            startActivity(new Intent(this, GroupActivity.class));
        } else if (id == R.id.my_groups) {
            startActivity(new Intent(this, UserGroupActivity.class));
        } else if (id == R.id.my_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
        } else if (id == R.id.log_out) {
            session.logOutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}