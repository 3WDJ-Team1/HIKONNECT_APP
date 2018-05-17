package kr.ac.yjc.wdj.hikonnect.activities.groupDetail;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import devlight.io.library.ntb.NavigationTabBar;
import kr.ac.yjc.wdj.hikonnect.Environment;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.UsersData;
import kr.ac.yjc.wdj.hikonnect.adapters.MemberListAdapter;
import kr.ac.yjc.wdj.hikonnect.adapters.RecycleAdapterForGDetail;
import kr.ac.yjc.wdj.hikonnect.beans.Bean;
import kr.ac.yjc.wdj.hikonnect.beans.GroupNotice;
import kr.ac.yjc.wdj.hikonnect.beans.GroupSchedule;
import kr.ac.yjc.wdj.hikonnect.beans.GroupUserInfoBean;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-04-10
 */
public class TabsActivity extends AppCompatActivity {

    // UI 변수
    private ViewPager               viewPager;          // 전체 페이지
    private NavigationTabBar        navigationTabBar;   // 네비게이션 탭 바
    private FloatingActionButton    btnEnterGroup;      // 그룹 참가 버튼
    private CollapsingToolbarLayout toolbarLayout;      // 툴바레이아웃
    private ProgressBar             progressBar;        // 로딩 시간...

    // UI 내부 데이터 연결 객체
    private ArrayList<NavigationTabBar.Model>   models;             // 네비게이션 탭 안의 메뉴 구성을 위한 모델
    private static RecycleAdapterForGDetail     adapterNotice,      // rvNotice에 값을 연결할 어댑터
                                                adapterMember,      // rvMember에 값을 연결할 어댑터
                                                adapterSchedule;    // rvPlan 에 값을 연결할 어댑터
    private MemberListAdapter                   adapterMemberJoined;// rvMemberJoined에 값을 연결할 어댑터
    private ArrayList<Bean>                     dataListNotice,     // notice URL 요청으로 받아온 값을 담아둘 곳
                                                dataListMember,     // member URL 요청으로 받아온 값을 담아둘 곳
                                                dataListSchedule;   // schedule URL 요청으로 받아온 값을 담아둘 곳
    private ArrayList<GroupUserInfoBean>        dataListJoinedMember;
    // 데이터 담아둘 변수
    private String[]            colors;             // 안드로이드 내장 색상을 불러올 배열
    private int                 firstIndex;         // 요청 공지사항 데이터 시작 인덱스
    private static final int    PAGE_COUNT = 3;     // 총 페이지의 수
    private static String       status;             // 사용자가 해당 그룹에 참여하고 있는 지 여부
    public  static String       groupId;            // 해당 그룹의 id

    // 무한 스크롤
    private boolean     isScrolling;                                // 현재 스크롤 되고 있는지 확인
    private int         currentItems, totalItems, scrollOutItems;   // 현재 스크롤 위치 파악하기 위한 변수


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_detail_home);

        Intent intent       = getIntent();
        groupId             = intent.getStringExtra("groupId");
        status              = intent.getStringExtra("status");

        viewPager           = (ViewPager)           findViewById(R.id.vp_horizontal_ntb);
        navigationTabBar    = (NavigationTabBar)    findViewById(R.id.ntb_horizontal);
        progressBar         = (ProgressBar)         findViewById(R.id.pageProgress);
        models              = new ArrayList<>();
        colors              = getResources().getStringArray(R.array.default_preview);
        btnEnterGroup       = (FloatingActionButton) findViewById(R.id.btnEnterGroup);
        isScrolling         = false;
        dataListNotice      = new ArrayList<>();
        dataListMember      = new ArrayList<>();
        dataListJoinedMember= new ArrayList<>();
        dataListSchedule    = new ArrayList<>();
        firstIndex          = 0;
        toolbarLayout       = (CollapsingToolbarLayout) findViewById(R.id.toolbar);
        adapterNotice       = new RecycleAdapterForGDetail(R.layout.notice_item, dataListNotice);
        adapterSchedule     = new RecycleAdapterForGDetail(R.layout.schedule_item_cardview_, dataListSchedule);
        adapterMember       = new RecycleAdapterForGDetail(R.layout.member_list, dataListMember);
        adapterMemberJoined = new MemberListAdapter(R.layout.member_list_schedule, dataListJoinedMember, status);

        // 툴바에 그룹 이름 넣기
        toolbarLayout.setTitle(intent.getStringExtra("groupName"));

        //TODO
        // 이 사람이 그룹에 있으면 그룹 참가 버튼 -> 그룹 탈퇴 버튼
//        initVarisJoined("", "");
        // 플로팅 버튼 누르면 그룹 참가
//        setListenerToFloatingButton();
        // 페이지 불러오기
        initUI();

        // 공지사항 리스트 받아오기
        datafetchForNotice(groupId, firstIndex);

        // 참여자 값 받기
        datafetchForMember(groupId);

        setListenerToFloatingButton();
    }

    /**
     * 플로팅 버튼에 리스너 달기
     */
    private void setListenerToFloatingButton () {
        btnEnterGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new AsyncTask<Void, Integer, String>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        protected String doInBackground(Void... params) {
                            try {
                                OkHttpClient client = new OkHttpClient();

                                String sendData = "{" +
                                            "\"userid\":\"" + UsersData.USER_ID + "\"," +
                                            "\"uuid\":\""   + groupId           + "\""  +
                                        "}";

                                RequestBody body = RequestBody.create(Environment.JSON, sendData);

                                Request request = new Request.Builder()
                                        .url(Environment.LARAVEL_SOL_SERVER + "/member")
                                        .post(body)
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
                            Toast.makeText(getBaseContext(), "참가신청 되었습니다.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            btnEnterGroup.setVisibility(View.GONE);
                        }
                    }.execute();
            }
        });
    }

    /**
     * 전체적인 UI 초기화 작업 --> 페이지 초기화 등
     */
    private void initUI() {
        // 페이지에 어댑터 장착
        viewPager.setAdapter(new PagerAdapter() {
            /**
             * 페이지 수 설정
             * @return
             */
            @Override
            public int getCount() {
                return TabsActivity.PAGE_COUNT;
            }

            @Override
            public boolean isViewFromObject(final View view, final Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            /**
             * 페이지 내부 설정
             * @param container 컨테이너 뷰그룹
             * @param position  몇 번째 페이지인지
             * @return          view
             */
            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                View view  = null; // 페이지에 넣을 View

                switch (position) {
                    // 그룹 공지사항 페이지
                    case 0:
                        // group_detail_notice의 view 객체를 받아옴
                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.group_detail_notice, null, false);
                        // 해당 view의 RecyclerView 찾아옴
                        RecyclerView rvNotice = (RecyclerView) view.findViewById(R.id.groupNoticeList);
                        rvNotice.setAdapter(adapterNotice);

                        // 사이즈 고정
                        rvNotice.setHasFixedSize(true);
                        // 레이아웃 매니저 설정
                        rvNotice.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

                        // 스크롤 리스너 : 무한 스크롤
                        rvNotice.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                    isScrolling = true;
                                }
                            }

                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                currentItems    = recyclerView.getLayoutManager().getChildCount();
                                totalItems      = recyclerView.getLayoutManager().getItemCount();
                                scrollOutItems  = ((LinearLayoutManager)recyclerView.getLayoutManager())
                                                    .findFirstVisibleItemPosition();

                                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                                    isScrolling = false;
                                    // data fetch
                                    datafetchForNotice(TabsActivity.groupId, firstIndex);
                                }
                            }
                        });
                        // 총 컨테이너 페이저에 삽입
                        container.addView(view);
                        break;
                    // 그룹 일정 페이지
                    case 1:
                        // group_detail_plan의 view 객체를 받아옴
                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.group_detail_plan, null, false);
                        // 해당 view의 RecyclerView 찾아옴
                        RecyclerView rvPlan = (RecyclerView) view.findViewById(R.id.groupPlanList);
                        rvPlan.setAdapter(adapterSchedule);

                        // 사이즈 고정
                        rvPlan.setHasFixedSize(true);
                        // 레이아웃 매니저 설정
                        rvPlan.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

                        // 일정 리스트 받아오기
                        datafetchForSchedule(groupId);

                        // 총 컨테이너 페이저에 삽입
                        container.addView(view);
                        break;
                    // 그룹 멤버 리스트
                    case 2:
                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.group_detail_members, null, false);
                        RecyclerView rvMember       = (RecyclerView) view.findViewById(R.id.groupMemberList);
                        RecyclerView rvMemberJoined = (RecyclerView) view.findViewById(R.id.groupMemberListJoined);

                        if (status.equals("owner")) {
                            rvMember.setAdapter(adapterMember);
                            rvMember.setHasFixedSize(true);
                            rvMember.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
                        } else {
                            rvMember.setVisibility(View.GONE);
                        }

                        rvMemberJoined.setAdapter(adapterMemberJoined);
                        rvMemberJoined.setHasFixedSize(true);
                        rvMemberJoined.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

                        container.addView(view);
                        break;
                }
                return view;
            }

        });

        // 탭에 메뉴 추가 (사진, 색상, 타이틀)
        addModels(R.drawable.ic_notifications_white_24px, colors[0], "Notice");
        addModels(R.drawable.ic_event_white_24px, colors[1], "Plan");
        addModels(R.drawable.ic_group_white_24px, colors[2], "Members");

        // 탭 모델 설정
        navigationTabBar.setModels(models);

        // 뷰페이저 설정
        navigationTabBar.setViewPager(viewPager, 0);

        //IMPORTANT: ENABLE SCROLL BEHAVIOUR IN COORDINATOR LAYOUT
        navigationTabBar.setBehaviorEnabled(true);

        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {
            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {

            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });
    }

    // 탭에 모델 추가
    private void addModels(int drawable, String color, String title) {
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(drawable),
                        Color.parseColor(color))
                        .title(title)
                        .build()
        );
    }

    // get data from server for notice list
    private void datafetchForNotice(final String groupId, final int page) {
        // 비동기
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    // http 리퀘스트
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url(Environment.LARAVEL_SOL_SERVER + "/list_announce/" + groupId + "/" + page)
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
                    Log.d("Notice", s);
                    // json 파싱
                    JSONArray   jsonArray   = new JSONArray(s);
                    GroupNotice groupNotice = null;
                    // 결과 배열 값을
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        // 객체로 분해 해 Bean 객체에 삽입
                        groupNotice = new GroupNotice(
                                jsonObject.getString("writer"),
                                jsonObject.getString("title"),
                                jsonObject.getString("content"),
                                jsonObject.getString("picture"),
                                jsonObject.getString("created_at")
                        );
                        // dataList에 삽입
                        dataListNotice.add(groupNotice);
                    }

                    adapterNotice.notifyDataSetChanged();
                    Log.d("adapters", adapterNotice.getItemCount() + "");
                    firstIndex++;
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }.execute();
    }

    // 일정 리스트 받아오기
    private void datafetchForSchedule(final String groupId) {
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    // 클라이언트 객체
                    OkHttpClient client = new OkHttpClient();

                    // 리퀘스트 생성
                    Request request = new Request.Builder()
                            .url(Environment.LARAVEL_SOL_SERVER + "/schedule/" + groupId)
                            .build();

                    Log.d("schedule", Environment.LARAVEL_SOL_SERVER + "/schedule/" + groupId);

                    // 결과 받아오기
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
                Log.d("#schedule#", s);
                try {

                    // JSON  파싱 후
                    JSONArray   jsonArray   = new JSONArray(s);
                    for (int i = 0 ; i < jsonArray.length() ; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        // 데이터 넣기
                        dataListSchedule.add(new GroupSchedule(
                                jsonObject.getInt("no"),
                                jsonObject.getString("title"),
                                jsonObject.getString("content"),
                                jsonObject.getString("leader"),
                                jsonObject.getDouble("mnt_id"),
                                jsonObject.getString("start_date"),
                                new JSONArray(jsonObject.get("route").toString()),
                                getBaseContext()
                        ));
                    }
                    // 데이터 변경 어댑터에 알리기
                    adapterSchedule.notifyDataSetChanged();
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }.execute();
    }

    // 멤버 리스트 받아오기
    private void datafetchForMember(final String groupId) {
        // 참여 안된 멤버
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    // http 리퀘스트
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url(Environment.LARAVEL_SOL_SERVER + "/list_member/" + groupId)
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
                    // json 파싱
                    JSONArray           jsonArray       = new JSONArray(s);
                    JSONObject          jsonObject      = jsonArray.getJSONObject(0);
                    GroupUserInfoBean   userInfoBean    = null;

                    JSONArray arrayNotEnter = jsonObject.getJSONArray("not_enter");
                    JSONArray arrayEnter    = jsonObject.getJSONArray("enter");

                    // 결과 배열 값을
                    for (int i = 0 ; i < arrayNotEnter.length() ; i++) {
                        JSONObject objNotEnter = arrayNotEnter.getJSONObject(i);
                        // 객체로 분해 해 Bean 객체에 삽입
                        userInfoBean = new GroupUserInfoBean(
                                objNotEnter.getString("userid"),
                                objNotEnter.getString("nickname"),
                                objNotEnter.getString("profile"),
                                objNotEnter.getString("grade"),
                                objNotEnter.getString("phone"),
                                objNotEnter.getString("enter_date"),
                                objNotEnter.getInt("gender"),
                                objNotEnter.getInt("age_group"),
                                objNotEnter.getInt("scope"),
                                getBaseContext()
                        );

                        dataListMember.add(userInfoBean);
                    }

                    for (int j = 0 ; j < arrayEnter.length() ; j++) {
                        JSONObject objEnter = arrayEnter.getJSONObject(j);
                        userInfoBean = new GroupUserInfoBean(
                                objEnter.getString("userid"),
                                objEnter.getString("nickname"),
                                objEnter.getString("profile"),
                                objEnter.getString("grade"),
                                objEnter.getString("phone"),
                                objEnter.getString("enter_date"),
                                objEnter.getInt("gender"),
                                objEnter.getInt("age_group"),
                                objEnter.getInt("scope"),
                                getBaseContext()
                        );
                        dataListJoinedMember.add(userInfoBean);
                    }

                    adapterMember.notifyDataSetChanged();
                    adapterMemberJoined.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }.execute();
    }

}