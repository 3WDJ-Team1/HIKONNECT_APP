package kr.ac.yjc.wdj.hikonnect.activities.myPage;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.LoadingDialog;
import kr.ac.yjc.wdj.hikonnect.activities.group_list.GroupListItem;
import kr.ac.yjc.wdj.hikonnect.adapters.JoinedGroupListAdapter;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 유저가 참여하고 있는 그룹 리스트업 액티비티
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-26
 */
public class UserJoinedGroup extends AppCompatActivity {

    // UI 변수
    private         ImageButton                 btnGoBack;      // 툴바에 있는 뒤로가기 버튼
    private         RecyclerView                rvJoinedGroup;  // 참여한 그룹 리스트 리사이클러 뷰
    private         LoadingDialog               loadingDialog;  // 로딩 다이얼로그

    // 데이터 변수
    private         ArrayList<GroupListItem>    groupList;      // 사용자가 참여하고 있는 그룹 정보 리스트

    // 세선
    private         SharedPreferences           preferences;    // 사용자 정보를 가지고 있는 preference

    // 어댑터
    private         JoinedGroupListAdapter      adapter;        // 리사이클러 뷰에 연결될 어댑터

    // OkHttp
    private         OkHttpClient                client;

    // 상수
    private final   String                      JOINED_GROUP_LOG_TAG  = "JOINEDGROUP";  // 로그캣 출력 태그

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_group_app_bar);

        loadingDialog = new LoadingDialog(this);

        initData();
        initUI();
    }

    /**
     * UI 변수 초기화
     */
    private void initUI() {

        btnGoBack       = (ImageButton)     findViewById(R.id.btnGoBack);
        rvJoinedGroup   = (RecyclerView)    findViewById(R.id.rvJoinedGroup);

        // 뒤로가기 버튼에 리스너 장착
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 리사이클러 뷰 레이아웃 매니저 장착
        rvJoinedGroup.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

        // 리사이클러 뷰에 어댑터 장착
        rvJoinedGroup.setAdapter(adapter);

    }

    /**
     * 어댑터 및 데이터 변수 초기화
     */
    private void initData() {
        preferences = getSharedPreferences("loginData", MODE_PRIVATE);
        client      = new OkHttpClient();
        groupList   = new ArrayList<>();
        adapter     = new JoinedGroupListAdapter(groupList, preferences);

        getGroupsDataFromServer();
    }

    private void getGroupsDataFromServer() {

        new AsyncTask<Void, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadingDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                try {

                    RequestBody body = new FormBody.Builder()
                            .add("userid", preferences.getString("user_id", ""))
                            .build();

                    Request     request = new Request.Builder()
                            .url(Environments.LARAVEL_HIKONNECT_IP + "/api/mygroup")
                            .post(body)
                            .build();

                    Response response = client.newCall(request).execute();

                    return response.body().string();

                } catch (IOException ie) {

                    Log.e(JOINED_GROUP_LOG_TAG, "IOException was occurred while getting group data from server!!!!\n" + ie);
                    return null;

                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                Log.d(JOINED_GROUP_LOG_TAG, s);

                try {

                    // JSON parsing
                    JSONArray jsonArray = new JSONArray(s);

                    for (int i = 0 ; i < jsonArray.length() ; i++) {

                        JSONObject groupObj = jsonArray.getJSONObject(i);

                        // 데이터 넣기
                        groupList.add(new GroupListItem(
                                groupObj.getString("uuid"),
                                groupObj.getString("title"),
                                groupObj.getString("nickname"),
                                groupObj.getString("content"),
                                getBaseContext()
                        ));

                    }

                    adapter.notifyDataSetChanged();
                } catch (JSONException je) {

                    Log.e(JOINED_GROUP_LOG_TAG, "JSONException was occurred while JSON parsing with group data!!!!\n" + je);
                }

                loadingDialog.cancel();
            }
        }.execute();
    }
}
