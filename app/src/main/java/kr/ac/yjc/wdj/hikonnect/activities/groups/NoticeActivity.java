package kr.ac.yjc.wdj.hikonnect.activities.groups;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.ac.yjc.wdj.hikonnect.Environment;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.apis.http_request.GHttpRequestConnection;
import kr.ac.yjc.wdj.hikonnect.apis.http_request.HttpRequestConnection;

public class NoticeActivity extends AppCompatActivity {
    GHttpRequestConnection hrc = new GHttpRequestConnection();
    String result;
    Handler handler;
    NoticeAdapter list_adapter;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    private ArrayList<NoticeListItem> listItems;
    String title;
    String content;
    String notice_created_at;
    String id;
    String groupUuid;
    ContentValues contentValues = new ContentValues();
    int page;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_list_recyclerview);

        recyclerView = (RecyclerView) findViewById(R.id.notice_list_recyclerview);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        //loadRecyclerViewData();

        page = 10;

        listItems = new ArrayList<>();

        int ITEM_SIZE = 12;

        List<NoticeListItem> items = new ArrayList<>();
        NoticeListItem[] item = new NoticeListItem[ITEM_SIZE];
        item[0] = new NoticeListItem("송소리", "소리소리송소리", "1");
        item[1] = new NoticeListItem("뭐", "test", "2");
        item[2] = new NoticeListItem("좀되라", "admin", "3");
        item[3] = new NoticeListItem("송소리", "소리소리송소리", "4");
        item[4] = new NoticeListItem("뭐", "test", "5");
        item[5] = new NoticeListItem("좀되라", "admin", "6");
        item[6] = new NoticeListItem("송소리", "소리소리송소리", "7");
        item[7] = new NoticeListItem("뭐", "test", "8");
        item[8] = new NoticeListItem("좀되라", "admin", "9");
        item[9] = new NoticeListItem("송소리", "소리소리송소리", "10");
        item[10] = new NoticeListItem("뭐", "test", "11");
        item[11] = new NoticeListItem("좀되라", "admin", "12");

        for (int i = 0; i < ITEM_SIZE; i++) {
            items.add(item[i]);
        }

        recyclerView.setAdapter(new NoticeAdapter(getApplicationContext(), items));

        Intent intent = getIntent();
        groupUuid = intent.getStringExtra("groupUuid");
    }

    public void loadRecyclerViewData() {
        contentValues.put("groupuuid", groupUuid);

        new Thread(new Runnable() {
            @Override
            public void run() {
                result = hrc.request(Environment.LARAVEL_HIKONNECT_IP + "/api/schedule", contentValues);
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
                        content = obj.getString("content");
                        notice_created_at = obj.getString("created_at");

                        listItems.add(new NoticeListItem(title, content, notice_created_at));
                    }

                    list_adapter = new NoticeAdapter(NoticeActivity.this, listItems);
                    recyclerView.setAdapter(list_adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

}
