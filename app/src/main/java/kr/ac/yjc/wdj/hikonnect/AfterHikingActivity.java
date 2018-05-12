package kr.ac.yjc.wdj.hikonnect;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import kr.ac.yjc.wdj.hikonnect.adapters.AfterHikingListAdapter;
import kr.ac.yjc.wdj.hikonnect.beans.AfterHikingMenu;

/**
 * 등산 직후 화면
 * @author  Sungeun Kang(kasueu0814@gmail.com)
 * @since   2018-05-12
 */
public class AfterHikingActivity extends AppCompatActivity {
    // UI 변수
    private TextView                    userName;           // 사용자 이름
    private RecyclerView                rvAfterHikingList;  // 등산 후 통계를 종류별로 나타낼 RecyclerView

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

        // 데이터 초기화
        hikingMenus = new ArrayList<>();
        menuTitles  = new ArrayList<>();
        menuValues  = new ArrayList<>();
        images      = new ArrayList<>();

        // hikingMenus 설정
        setHikingMenus();

        // RecyclerView에 Adapter 붙이기
        rvAfterHikingList.setAdapter(new AfterHikingListAdapter(hikingMenus, R.layout.after_hiking_list_item));
        rvAfterHikingList.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
    }

    /**
     * menus, images 초기화 --> 메뉴 추가 시 이곳에 적용
     */
    private void initData() {
        // TODO : intent로 받아오도록 변경하기
        // TODO : 데이터 DB 에서 받아올 수 있도록 하기
        // 내용 초기화
        menuTitles.add("총 등산 시간");
        menuValues.add("6.5 시간");
        menuTitles.add("순위");
        menuValues.add("8등");
        menuTitles.add("완료한 산");
        menuValues.add("소백산");
        menuTitles.add("현재 등급");
        menuValues.add("한라산");

        // image 초기화
        images.add(getResources().getDrawable(R.drawable.ic_baseline_alarm_24px));
        images.add(getResources().getDrawable(R.drawable.ic_ranking_svgrepo_com));
        images.add(getResources().getDrawable(R.drawable.ic_mountain_svgrepo_com));
        images.add(getResources().getDrawable(R.drawable.ic_rating_svgrepo_com));        
    }

    /**
     * hikingMenus 설정
     */
    private void setHikingMenus() {
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
