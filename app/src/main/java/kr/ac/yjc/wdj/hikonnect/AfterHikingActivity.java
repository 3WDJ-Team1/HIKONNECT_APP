package kr.ac.yjc.wdj.hikonnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import kr.ac.yjc.wdj.hikonnect.adapters.AfterHikingListAdapter;
import kr.ac.yjc.wdj.hikonnect.beans.AfterHikingMenu;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 등산 직후 화면
 * @author  Sungeun Kang(kasueu0814@gmail.com)  UI 작업
 * @author  bs Kwon <rnjs9957@gmail.com>        데이터 바인딩
 * @since   2018-05-12
 */
public class AfterHikingActivity extends AppCompatActivity {
    // UI 변수
    private TextView                    tvRemainMembers;    // 남은 등산 맴버 수.
    private TextView                    userName;           // 사용자 이름
    private RecyclerView                rvAfterHikingList;  // 등산 후 통계를 종류별로 나타낼 RecyclerView
    private Button                      btnToOthersInfo;    // 현재 등산 중인 멤버 리스트로 이동 버튼

    // 데이터 변수
    private ArrayList<AfterHikingMenu>  hikingMenus;        // 메뉴 리스트
    private ArrayList<String>           menuTitles;         // 메뉴 리스트 제목
    private ArrayList<String>           menuValues;         // 메뉴 리스트 값
    private ArrayList<Drawable>         images;             // image drawables

    // Intent 전달 데이터.
    private int                         memberNum;          // 맴버 번호.

    // 출력 데이터
    private int                         remainMembers;      // 남은 맴버의 수.
    private String                      hikingTime;         // 등산 한 시간.
    private int                         hikingRank;         // 등산 순위.
    private String                      completedMountain;  // 완료한 산 이름.
    private String                      hikingTear;         // 산행 등급.

    //
    public SharedPreferences            pref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_hiking);

        pref = getSharedPreferences("loginData", MODE_PRIVATE);

        // UI 초기화
        tvRemainMembers     = (TextView)        findViewById(R.id.howManyNowHiking);
        userName            = (TextView)        findViewById(R.id.userName);
        rvAfterHikingList   = (RecyclerView)    findViewById(R.id.afterHikingList);
        btnToOthersInfo     = (Button)          findViewById(R.id.showMemberList);

        // 데이터 초기화
        hikingMenus = new ArrayList<>();
        menuTitles  = new ArrayList<>();
        menuValues  = new ArrayList<>();
        images      = new ArrayList<>();

        // hikingMenus 설정
        try {
            setHikingMenus();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        userName.setText(pref.getString("user_name", ""));

        // RecyclerView에 Adapter 붙이기
        rvAfterHikingList.setAdapter(new AfterHikingListAdapter(hikingMenus, R.layout.after_hiking_list_item));
        rvAfterHikingList.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

        // Button에 리스너 달기
        btnToOthersInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Othersinfo.class);
                intent.putExtra("member_no", memberNum);
                startActivity(intent);
                AfterHikingActivity.this.finish();
            }
        });
    }

    /**
     * menus, images 초기화 --> 메뉴 추가 시 이곳에 적용
     */
    private void initData() throws InterruptedException {
        // TODO : intent로 받아오도록 변경하기
        // TODO : 데이터 DB 에서 받아올 수 있도록 하기
        Intent intent = getIntent();
        memberNum = intent.getIntExtra("member_no", 1);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUrl httpUrl = HttpUrl.parse(Environments.LARAVEL_HIKONNECT_IP + "/api/getHikingResult").newBuilder().build();

                RequestBody reqBody = new FormBody.Builder()
                        .add("member_no", String.valueOf(memberNum))
                        .build();
                Request req = new Request.Builder().url(httpUrl).post(reqBody).build();

                try {
                    Response response = new OkHttpClient().newCall(req).execute();

                    String result = response.body().string();

                    JSONParser parser = new JSONParser();

                    JSONObject resObj = (JSONObject) ((JSONArray) parser.parse(result)).get(0);

                    Date date = new SimpleDateFormat("hh:mm:ss").parse(resObj.get("hiking_time").toString());
                    String parsedHikingTime = new SimpleDateFormat("hh시간 mm분 ss초").format(date);

                    remainMembers       = Integer.valueOf(resObj.get("remain_member") != null
                            ? resObj.get("remain_member").toString()
                            : "0");
                    hikingTime          = resObj.get("hiking_time").toString();
                    hikingRank          = Integer.valueOf(resObj.get("rank") != null
                            ? resObj.get("rank").toString()
                            : "0");
                    completedMountain   = resObj.get("mountain") != null
                            ? resObj.get("mountain").toString()
                            : "No Data";
                    hikingTear          = resObj.get("hiking_tear") != null
                            ? resObj.get("hiking_tear").toString()
                            : "No Data";
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        thread.join();

        //
        tvRemainMembers.setText(String.valueOf(remainMembers));

        // 내용 초기화
        images.add(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_baseline_alarm_24px));
        menuTitles.add("총 등산 시간");
        menuValues.add(String.valueOf(hikingTime));

        images.add(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_ranking_svgrepo_com));
        menuTitles.add("순위");
        menuValues.add(String.valueOf(hikingRank) + "등");

        images.add(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_mountain_svgrepo_com));
        menuTitles.add("완료한 산");
        menuValues.add(completedMountain);

        images.add(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_rating_svgrepo_com));
        menuTitles.add("현재 등급");
        menuValues.add(hikingTear);
    }

    /**
     * hikingMenus 설정
     */
    private void setHikingMenus() throws InterruptedException {
        initData();

        Iterator<String>    iForTitles  = menuTitles.iterator();
        Iterator<String>    iForValues  = menuValues.iterator();
        Iterator<Drawable>  iForIamges  = images.iterator();

        while(iForTitles.hasNext() && iForValues.hasNext() && iForIamges.hasNext()) {
            String      title   = iForTitles.next();
            String      value   = iForValues.next();
            Drawable    image   = iForIamges.next();
            
            hikingMenus.add( new AfterHikingMenu(image, title, value) );
        }
    }
}
