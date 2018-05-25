package kr.ac.yjc.wdj.hikonnect;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import kr.ac.yjc.wdj.hikonnect.activities.MapsActivity;
import kr.ac.yjc.wdj.hikonnect.adapters.HikingMemberListAdapter;
import kr.ac.yjc.wdj.hikonnect.adapters.MemberListAdapter;
import kr.ac.yjc.wdj.hikonnect.apis.HttpRequest.HttpRequestConnection;
import kr.ac.yjc.wdj.hikonnect.beans.GroupUserInfoBean;
import kr.ac.yjc.wdj.hikonnect.beans.HikingMemberListBean;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jungyu on 2018-05-02.
 */

public class Othersinfo extends Activity {

    // UI 변수
    private RecyclerView                    listView;   // 검색을 보여줄 리스트변수
    private ProgressBar                     progressBar;// 진행 상황을 표시할 ProgressBar
    private EditText                        editSearch; // 검색어를 입력할 Input 창
    private ImageButton                     btnGoBack;  // 뒤로가기 버튼

    // 데이터 변수
    private HikingMemberListAdapter         adapter;    // 리스트뷰에 연결할 아답터
    private ArrayList<HikingMemberListBean> dataList,
                                            tempList;
    private String                          groupId;
    private int                             scheduleNo;

    // 18.05.23(Wed) bs Kwon
    private int                             memberNo;
    // 18.05.23(Wed) bs Kwon


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_app_bar);

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

                    /*  18.05.23(Wed) bs Kwon

                        [1] Request URL changed.

                            /api/getHikingMembers
                            ->
                            /api/getScheduleMembers

                        [2] Request Method and Params changed.

                            [Method] GET
                            [Params]
                            1, Group ID         user's group ID.
                            2, Schedule NO      user's schedule ID.
                            ->
                            [Method] POST
                            [Params]
                            1. Member NO        user's member NO.
                    */
                    HttpUrl httpUrl = HttpUrl
                            .parse(Environments.LARAVEL_HIKONNECT_IP + "/api/getScheduleMembers")
                            .newBuilder()
                            .build();

                    RequestBody reqBody = new FormBody.Builder()
                            .add("member_no", String.valueOf(memberNo))
                            .build();

                    Request request = new Request.Builder()
                            .url(httpUrl)
                            .post(reqBody)
                            .build();

                    // 18.05.23(Wed) bs Kwon

                    /*Request request = new Request.Builder()
                            .url(Environments.LARAVEL_HIKONNECT_IP + "/getHikingMembers/" + params[0] + "/" + params[1])
                            .build();*/

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

                        new AsyncTask<JSONObject, Integer, JSONObject>() {

                            @Override
                            protected JSONObject doInBackground(JSONObject... jsonObjects) {
                                try {
                                    JSONObject jsonObj = jsonObjects[0];

                                    Request req = new Request.Builder()
                                            .url(Environments.NODE_HIKONNECT_IP + "/images/UserProfile/" + jsonObj.getString("userid") + ".jpg")
                                            .build();

                                    OkHttpClient client = new OkHttpClient();

                                    Response res = client.newCall(req).execute();

                                    InputStream is = res.body().byteStream();

                                    Bitmap userProfileImg = BitmapFactory.decodeStream(is);

                                    HikingMemberListBean userInfo = new HikingMemberListBean(
                                            jsonObj.getInt("member_no"),
                                            jsonObj.getString("nickname"),
                                            jsonObj.getDouble("distance"),
                                            jsonObj.getInt("rank"),
                                            userProfileImg,
                                            jsonObj.getDouble("latitude"),
                                            jsonObj.getDouble("longitude")
                                            );

                                    dataList.add(userInfo);
                                    tempList.add(userInfo);
                                } catch (JSONException jse) {
                                    jse.printStackTrace();
                                } catch (IOException ioe) {
                                    ioe.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(JSONObject jsonObject) {
                                super.onPostExecute(jsonObject);
                                adapter.notifyDataSetChanged();
                            }
                        }.execute(object);
                    }
                } catch (JSONException je) {
                    je.printStackTrace();
                }
                publishProgress(100);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);

                if (values[0] == 100) {
                    progressBar.setVisibility(View.GONE);
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
        progressBar = (ProgressBar)     findViewById(R.id.otherInfoProgressBar);
        btnGoBack   = (ImageButton)     findViewById(R.id.btnGoBack);

        // 뒤로가기 리스너 정의
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
        // 18.05.23(Wed) bs Kwon
        memberNo    = intent.getIntExtra("member_no", 0);
        // 18.05.23(Wed) bs Kwon
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