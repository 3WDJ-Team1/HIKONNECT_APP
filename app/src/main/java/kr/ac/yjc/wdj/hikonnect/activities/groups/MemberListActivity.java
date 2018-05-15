package kr.ac.yjc.wdj.hikonnect.activities.groups;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import java.util.List;

import kr.ac.yjc.wdj.hikonnect.Environment;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.session.SessionManager;
import kr.ac.yjc.wdj.hikonnect.apis.http_request.HttpRequestConnection;


public class MemberListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    SessionManager session;
    DrawerLayout drawer;
    Toolbar toolbar;
    HttpRequestConnection hrc = new HttpRequestConnection();
    String result;
    Handler handler;
    MemberAdapter list_adapter;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    private ArrayList<MemberListItem> listItems;
    String nickname;
    String groupUuid;
    String id;
    ContentValues contentValues = new ContentValues();
    int page;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_list_recyclerview);

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

        session = new SessionManager(getApplicationContext());*/

        recyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        loadRecyclerViewData();

        page = 10;

        /*listItems = new ArrayList<>();

        int ITEM_SIZE = 12;

        List<MemberListItem> items = new ArrayList<>();
        MemberListItem[] item = new MemberListItem[ITEM_SIZE];
        item[0] = new MemberListItem(R.drawable.ic_emoticon,"송소리");
        item[1] = new MemberListItem(R.drawable.ic_emoticon,"뭐");
        item[2] = new MemberListItem(R.drawable.ic_emoticon,"좀되라");
        item[3] = new MemberListItem(R.drawable.ic_emoticon,"송소리");
        item[4] = new MemberListItem(R.drawable.ic_emoticon,"뭐");
        item[5] = new MemberListItem(R.drawable.ic_emoticon,"좀되라");
        item[6] = new MemberListItem(R.drawable.ic_emoticon,"송소리");
        item[7] = new MemberListItem(R.drawable.ic_emoticon,"뭐");
        item[8] = new MemberListItem(R.drawable.ic_emoticon,"좀되라");
        item[9] = new MemberListItem(R.drawable.ic_emoticon,"송소리");
        item[10] = new MemberListItem(R.drawable.ic_emoticon,"뭐");
        item[11] = new MemberListItem(R.drawable.ic_emoticon,"좀되라");

        for (int i = 0; i < ITEM_SIZE; i++) {
            items.add(item[i]);
        }

        recyclerView.setAdapter(new MemberAdapter(getApplicationContext(), items));
*/
        Intent intent = getIntent();
        groupUuid = intent.getStringExtra("groupUuid");
    }

    public void loadRecyclerViewData() {
        contentValues.put("groupuuid", groupUuid);

        new Thread(new Runnable() {
            @Override
            public void run() {
                result = hrc.request(Environment.LARAVEL_HIKONNECT_IP + "/api/list_group/", contentValues);
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
                        nickname = obj.getString("nickname");

                        listItems.add(new MemberListItem(R.drawable.ic_emoticon,nickname));
                    }

                    list_adapter = new MemberAdapter(MemberListActivity.this, listItems);
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
        return false;
    }
}