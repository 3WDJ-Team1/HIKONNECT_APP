package kr.ac.yjc.wdj.hikonnect.activities.groupDetail;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import devlight.io.library.ntb.NavigationTabBar;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.groups.GroupAdapter;
import kr.ac.yjc.wdj.hikonnect.adapters.RecycleAdapterForGDetail;
import kr.ac.yjc.wdj.hikonnect.apis.http_request.HttpRequestConnection;
import kr.ac.yjc.wdj.hikonnect.beans.Bean;
import kr.ac.yjc.wdj.hikonnect.beans.GroupNotice;
import kr.ac.yjc.wdj.hikonnect.beans.GroupUserInfoBean;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class TabsActivity extends FragmentActivity implements OnMapReadyCallback {

    // UI 변수
    private ViewPager               viewPager;          // 전체 페이지
    private NavigationTabBar        navigationTabBar;   // 네비게이션 탭 바
    private FloatingActionButton    btnEnterGroup;      // 그룹 참가 버튼

    // UI 내부 데이터 연결 객체
    private ArrayList<NavigationTabBar.Model>   models;             // 네비게이션 탭 안의 메뉴 구성을 위한 모델
    private static RecycleAdapterForGDetail     adapterNotice,      // rvNotice에 값을 연결할 어댑터
                                                adapterMember;      // rvMember에 값을 연결할 어댑터
    private ArrayList<Bean>                     dataListNotice,     // notice URL 요청으로 받아온 값을 담아둘 곳
                                                dataListMember;     // member URL 요청으로 받아온 값을 담아둘 곳

    // 데이터 담아둘 변수
    private String[]            colors;             // 안드로이드 내장 색상을 불러올 배열
    private int                 firstIndex;         // 요청 공지사항 데이터 시작 인덱스
    private int                 firstIndexForMem;   // 요청 그룹 멤버 데이터 시작 인덱스
    private final int           REQ_LENGTH = 10;    // 한 번의 요청에 불러올 데이터 수
    private static final int    PAGE_COUNT = 3;     // 총 페이지의 수
    private static boolean      isJoined;           // 사용자가 해당 그룹에 참여하고 있는 지 여부
    private static String       groupId;            // 해당 그룹의 id
    private static String       groupName;          // 해당 그룹의 이름
    private Toolbar             groupToolbar;        // 그룹 페이지의 툴바 제목

    // 무한 스크롤
    private boolean     isScrolling;                                // 현재 스크롤 되고 있는지 확인
    private int         currentItems, totalItems, scrollOutItems;   // 현재 스크롤 위치 파악하기 위한 변수

    HttpRequestConnection   hrc = new HttpRequestConnection();
    String                  result;
    Handler                 handler;
    GroupAdapter            list_adapter;
    RecyclerView            recyclerView;
    LinearLayoutManager     layoutManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_detail_home);

        Intent intent = getIntent();
        groupName = intent.getStringExtra("title");

        /*groupToolbar = (Toolbar) findViewById(R.id.group_toolbar);
        setSupportActionBar(groupToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        groupToolbar.setTitle(groupName);*/

        viewPager           = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        navigationTabBar    = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        models              = new ArrayList<>();
        colors              = getResources().getStringArray(R.array.default_preview);
        btnEnterGroup       = (FloatingActionButton) findViewById(R.id.btnEnterGroup);
        isScrolling         = false;
        dataListNotice      = new ArrayList<>();
        dataListMember      = new ArrayList<>();
        firstIndex          = 0;
        firstIndexForMem    = 0;

        adapterNotice       = new RecycleAdapterForGDetail(R.layout.notice_item, dataListNotice);
        adapterMember       = new RecycleAdapterForGDetail(R.layout.member_list, dataListMember);

        this.groupId        = "16f78874-b51c-3ad0-9b91-5d35f22a412b";
        // 이 사람이 그룹에 있으면 그룹 참가 버튼 -> 그룹 탈퇴 버튼
//        initVarisJoined("", "");
        // 플로팅 버튼 누르면 그룹 참가
//        setListenerToFloatingButton();
        // 페이지 불러오기
        initUI();

        datafetchForNotice(groupId, firstIndex, REQ_LENGTH);
        datafetchForSchedule();
        datafetchForMember(groupId, firstIndexForMem, REQ_LENGTH);
    }


    /**
     * 사용자가 현재 그룹에 참가하고 있는 지 여부를 isJoined 에 넣음
     * @param groupId   그룹 아이디
     * @param userId    해당 사용자의 아이디
     */
    private void initVarisJoined(String groupId, String userId) throws IOException, JSONException {
        isJoined = false;

        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... params) {
                /*todo 1234
//                String response = HttpRequestConnection.request("url", null);
                return response;
                */
                return "1234";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                TabsActivity.isJoined = true;
            }
        }.execute(groupId, userId);
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
                    protected String doInBackground(Void... params) {
                        /*todo 1234
                        return HttpRequestConnection.request("url", null);*/
                        return "1234";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
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
             * @param container
             * @param position
             * @return
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
                                scrollOutItems  = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();


                                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                                    isScrolling = false;
                                    // data fetch
                                    datafetchForNotice(TabsActivity.groupId, firstIndex, REQ_LENGTH);
                                }
                            }
                        });
                        // 총 컨테이너 페이저에 삽입
                        container.addView(view);
                        break;
                    // 그룹 일정 페이지
                    case 1:
                        /*view = new GMapFragment().onCreateView(getLayoutInflater(), container, null);
                        android.app.FragmentManager fragmentManager = getFragmentManager();
                        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.groupPlanMap);

                        ConstraintLayout layout = (ConstraintLayout) view.findViewById(R.id.groupPlanLayout);

                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                LatLng SEOUL = new LatLng(37.56, 126.97);

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(SEOUL);
                                markerOptions.title("서울");
                                markerOptions.snippet("한국의 수도");
                                googleMap.addMarker(markerOptions);

                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
                                googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                                googleMap.getUiSettings().setScrollGesturesEnabled(false);
                            }
                        });

                        container.addView(view);*/
                        // group_detail_notice의 view 객체를 받아옴
                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.group_detail_plan, null, false);
                        // 해당 view의 RecyclerView 찾아옴
                        RecyclerView rvPlan = (RecyclerView) view.findViewById(R.id.groupPlanList);
                        rvPlan.setAdapter(adapterNotice);

                        // 사이즈 고정
                        rvPlan.setHasFixedSize(true);
                        // 레이아웃 매니저 설정
                        rvPlan.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

                        // 스크롤 리스너 : 무한 스크롤
                        rvPlan.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                scrollOutItems  = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();


                                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                                    isScrolling = false;
                                    // data fetch
                                    datafetchForSchedule();
                                }
                            }
                        });
                        // 총 컨테이너 페이저에 삽입
                        container.addView(view);

                        // 리사이클러 뷰를 갈아 끼우는 함수 (setPage) 를 이용할 방법 없는지 확인 할 것
                        break;
                    // 그룹 멤버 리스트
                    case 2:
                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.group_detail_members, null, false);
                        RecyclerView rvMember = (RecyclerView) view.findViewById(R.id.groupMemberList);

                        rvMember.setAdapter(adapterMember);

                        rvMember.setHasFixedSize(true);
                        rvMember.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

                        // 스크롤 리스너 : 무한 스크롤
                        rvMember.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                scrollOutItems  = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                                    isScrolling = false;

                                    // data fetch
                                    datafetchForMember(TabsActivity.groupId, firstIndexForMem, REQ_LENGTH);
                                }
                            }
                        });

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
        navigationTabBar.setViewPager(viewPager, 2);

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
                        getDrawable(drawable),
                        Color.parseColor(color))
                        .title(title)
                        .build()
        );
    }

    // 맵 로딩 시 초기화
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        googleMap.addMarker(markerOptions);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    // get data from server for notice list
    private void datafetchForNotice(final String groupId, final int startIndex, final int length) {
        // 비동기
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {
                // http 리퀘스트
                /*todo 1234*/
//                String result = HttpRequestConnection.request(Conf.HTTP_ADDR + "/notice/" + groupId + "/" + startIndex + "/" + length, null);
//                return result;
                return "1234";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    // json 파싱
                    JSONArray jsonArray = new JSONArray(s);
                    GroupNotice groupNotice = null;
                    // 결과 배열 값을
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        // 객체로 분해 해 Bean 객체에 삽입
                        groupNotice = new GroupNotice(
                                jsonObject.getString("uuid"),
                                jsonObject.getString("nickname"),
                                jsonObject.getString("title"),
                                jsonObject.getString("content"),
                                jsonObject.getInt("hits"),
                                jsonObject.getString("created_at"),
                                jsonObject.getString("updated_at")
                        );
                        // dataList에 삽입
                        dataListNotice.add(groupNotice);
                    }

                    adapterNotice.notifyDataSetChanged();
                    Log.d("adapters", adapterNotice.getItemCount() + "");
                    firstIndex += REQ_LENGTH;
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }.execute();
    }

    // 일정 리스트 받아오기
    private void datafetchForSchedule() {
        try {

            OkHttpClient client = new OkHttpClient();

            URL url = new URL("http://192.168.1.146/api/schedule");

            RequestBody body = new FormBody.Builder()
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("Success", "제발");
                    String body = response.body().toString();
                    Log.d("responser", body);
                }
            });

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // 멤버 리스트 받아오기
    private void datafetchForMember(final String groupId, final int startIndex, final int length) {
        // 비동기
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {
                // http 리퀘스트
                /*todo 1234
                String result = HttpRequestConnection.request(Conf.HTTP_ADDR + "/groupMembers/" + groupId + "/" + startIndex + "/" + length, null);
                return result;*/
                return "!234";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    // json 파싱
                    JSONArray jsonArray = new JSONArray(s);
                    GroupUserInfoBean userInfoBean = null;
                    // 결과 배열 값을
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        // 객체로 분해 해 Bean 객체에 삽입
                        userInfoBean = new GroupUserInfoBean(
                                jsonObject.getString("nickname"),
                                jsonObject.getString("image_path"),
                                jsonObject.getString("phone"),
                                jsonObject.getInt("gender"),
                                jsonObject.getInt("age_group"),
                                jsonObject.getInt("scope")
                        );
                        // dataList에 삽입
                        dataListMember.add(userInfoBean);
                    }
                    adapterMember.notifyDataSetChanged();
                    Log.d("adapters", adapterMember.getItemCount() + "");
                    firstIndexForMem += REQ_LENGTH;
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }.execute();
    }

}