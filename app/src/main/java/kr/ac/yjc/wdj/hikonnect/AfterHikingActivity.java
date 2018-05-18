package kr.ac.yjc.wdj.hikonnect;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
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
 * @author  Sungeun Kang(kasueu0814@gmail.com)
 * @author  bs Kwon <rnjs9957@gmail.com>
 * @since   2018-05-12
 */
public class AfterHikingActivity extends AppCompatActivity {
    // UI 변수
    private TextView                    userName;           // 사용자 이름
    private RecyclerView                rvAfterHikingList;  // 등산 후 통계를 종류별로 나타낼 RecyclerView
    private Button                      btnToOthersInfo;    // 현재 등산 중인 멤버 리스트로 이동 버튼

    // 데이터 변수
    private ArrayList<AfterHikingMenu>  hikingMenus;        // 메뉴 리스트
    private ArrayList<String>           menuTitles;         // 메뉴 리스트 제목
    private ArrayList<String>           menuValues;         // 메뉴 리스트 값
    private ArrayList<Drawable>         images;             // image drawables

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_hiking);

        // UI 초기화
        userName            = (TextView) findViewById(R.id.userName);
        rvAfterHikingList   = (RecyclerView) findViewById(R.id.afterHikingList);
        btnToOthersInfo     = (Button) findViewById(R.id.showMemberList);

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

        // RecyclerView에 Adapter 붙이기
        rvAfterHikingList.setAdapter(new AfterHikingListAdapter(hikingMenus, R.layout.after_hiking_list_item));
        rvAfterHikingList.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

        // Button에 리스너 달기
        btnToOthersInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Othersinfo.class);
                startActivity(intent);
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
        final int memberNo = intent.getIntExtra("member_no", 1);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUrl httpUrl = HttpUrl.parse("http://172.26.1.88:8000/api/getAfterHikingInfo").newBuilder().build();

                RequestBody reqBody = new FormBody.Builder()
                        .add("member_no", String.valueOf(memberNo))
                        .build();
                Request req = new Request.Builder().url(httpUrl).post(reqBody).build();

                Response response = null;
                try {
                    response = new OkHttpClient().newCall(req).execute();

                    String result = null;

                    result = response.body().string();

                    Log.d("HIKONNECT", "initData: res: " + result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        thread.join();

        // 내용 초기화
        images.add(getResources().getDrawable(R.drawable.ic_baseline_alarm_24px));
        menuTitles.add("총 등산 시간");
        menuValues.add("6.5 시간");

        images.add(getResources().getDrawable(R.drawable.ic_ranking_svgrepo_com));
        menuTitles.add("순위");
        menuValues.add("8등");

        images.add(getResources().getDrawable(R.drawable.ic_mountain_svgrepo_com));
        menuTitles.add("완료한 산");
        menuValues.add("소백산");

        images.add(getResources().getDrawable(R.drawable.ic_rating_svgrepo_com));
        menuTitles.add("현재 등급");
        menuValues.add("한라산");
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
