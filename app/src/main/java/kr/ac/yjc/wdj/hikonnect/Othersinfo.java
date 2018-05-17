package kr.ac.yjc.wdj.hikonnect;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.ac.yjc.wdj.hikonnect.activities.MapsActivity;
import kr.ac.yjc.wdj.hikonnect.adapters.HikingMemberListAdapter;
import kr.ac.yjc.wdj.hikonnect.adapters.MemberListAdapter;
import kr.ac.yjc.wdj.hikonnect.apis.HttpRequest.HttpRequestConnection;
import kr.ac.yjc.wdj.hikonnect.beans.GroupUserInfoBean;
import kr.ac.yjc.wdj.hikonnect.beans.HikingMemberListBean;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jungyu on 2018-05-02.
 */

public class Othersinfo extends Activity {

    // UI 변수
    private RecyclerView                    listView;   // 검색을 보여줄 리스트변수
    private EditText                        editSearch; // 검색어를 입력할 Input 창

    // 데이터 변수
    private HikingMemberListAdapter         adapter;    // 리스트뷰에 연결할 아답터
    private ArrayList<HikingMemberListBean> dataList,
                                            tempList;
    private String                          groupId;
    private int                             scheduleNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        initUI();
        initData();

    }

    // 검색을 수행하는 메소드
    public void search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        dataList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            dataList.addAll(tempList);
        }
        // 문자 입력을 할때..
        else
        {
            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0;i < tempList.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (tempList.get(i).getNickname().toLowerCase().contains(charText))
                {
                    // 검색된 데이터를 리스트에 추가한다.
                    dataList.add(tempList.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }

    /**
     * 멤버 데이터 받아오기
     * @param groupId       그룹 아이디
     * @param scheduleNo    스케줄 번호
     */
    private void initMembersData(String groupId, int scheduleNo) {

        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url(Environment.LARAVEL_SOL_SERVER + "/getHikingMembers/" + params[0] + "/" + params[1])
                            .build();

                    Response response = client.newCall(request).execute();

                    return response.body().string();
                } catch (IOException ie) {
                    ie.printStackTrace();
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {

                    JSONArray jsonArray = new JSONArray(s);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);

                        HikingMemberListBean userInfo = new HikingMemberListBean(
                                object.getInt("member_no"),
                                object.getString("nickname"),
                                object.getDouble("latitude"),
                                object.getDouble("longitude")
                        );

                        dataList.add(userInfo);
                        tempList.add(userInfo);
                    }

                    adapter.notifyDataSetChanged();
                } catch (JSONException je) {
                    je.printStackTrace();
                }

            }
        }.execute(groupId, scheduleNo + "");
    }

    /**
     * UI 초기화
     */
    private void initUI() {
        editSearch  = (EditText)        findViewById(R.id.editSearch);
        listView    = (RecyclerView)    findViewById(R.id.listView);

        // input창에 검색어를 입력시 "addTextChangedListener" 이벤트 리스너를 정의한다.
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                search(text);
            }
        });
    }

    /**
     * 데이터 초기화
     */
    private void initData() {
        Intent intent = getIntent();

        groupId     = intent.getStringExtra("groupId");
        scheduleNo  = intent.getIntExtra("scheduleNo", 0);
        dataList    = new ArrayList<>();
        tempList    = new ArrayList<>();

        // 리스트에 연동될 아답터를 생성한다.
        adapter     = new HikingMemberListAdapter(R.layout.member_list_schedule, dataList, this);

        // 리스트뷰에 아답터를 연결한다.
        listView.setAdapter(adapter);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

        initMembersData(groupId, scheduleNo);
    }
}