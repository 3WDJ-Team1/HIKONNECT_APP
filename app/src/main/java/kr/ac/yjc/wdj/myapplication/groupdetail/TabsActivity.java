package kr.ac.yjc.wdj.myapplication.groupdetail;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import devlight.io.library.ntb.NavigationTabBar;
import kr.ac.yjc.wdj.myapplication.APIs.HttpRequest.HttpRequestConnection;
import kr.ac.yjc.wdj.myapplication.R;
import kr.ac.yjc.wdj.myapplication.adapters.RecycleAdapterForGDetail;
import kr.ac.yjc.wdj.myapplication.beans.Bean;
import kr.ac.yjc.wdj.myapplication.beans.GroupNotice;
import kr.ac.yjc.wdj.myapplication.beans.GroupUserInfoBean;
import kr.ac.yjc.wdj.myapplication.models.Conf;

import java.util.ArrayList;

/**
 * Created by GIGAMOLE on 28.03.2016.
 */
public class TabsActivity extends FragmentActivity implements OnMapReadyCallback {

    // 멤버 변수
    private ViewPager                           viewPager;
    private NavigationTabBar                    navigationTabBar;
    private ArrayList<NavigationTabBar.Model>   models;
    private String[]                            colors;
    private CoordinatorLayout                   coordinatorLayout;
    private FloatingActionButton                btnEnterGroup;
    private ArrayList<Bean>                     dataListNotice, dataListPlan, dataListMember;
    private Boolean                             isScrolling;
    private int                                 currentItems, totalItems, scrollOutItems;
    private static RecyclerView                 rvNotice, rvPlan, rvMember;
    private static RecycleAdapterForGDetail     adapterNotice, adapterPlan, adapterMember;
    private static LinearLayoutManager          linearLayoutManager;
    private int                                 firstIndex;
    private final int                           REQ_LENGTH = 10;
    private static final int                    PAGE_COUNT = 3;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_detail_home);

        viewPager           = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        navigationTabBar    = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        models              = new ArrayList<>();
        colors              = getResources().getStringArray(R.array.default_preview);
        coordinatorLayout   = (CoordinatorLayout) findViewById(R.id.parent);
        btnEnterGroup       = (FloatingActionButton) findViewById(R.id.btnEnterGroup);
        isScrolling         = false;
        dataListNotice      = new ArrayList<>();
        dataListPlan        = new ArrayList<>();
        dataListMember      = new ArrayList<>();
        firstIndex          = 0;
        linearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);

        initUI();
    }

    private void initUI() {
        viewPager.setAdapter(new PagerAdapter() {
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

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                View view  = null;

                switch (position) {
                    // 그룹 공지사항 페이지
                    case 0:
                        // group_detail_notice의 view 객체를 받아옴
                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.group_detail_notice, null, false);

                        // 해당 view의 RecyclerView 찾아옴
                        rvNotice = (RecyclerView)view.findViewById(R.id.groupNoticeList);
                        // 사이즈 고정
                        rvNotice.setHasFixedSize(true);
                        // 레이아웃 매니저 설정
                        rvNotice.setLayoutManager(TabsActivity.linearLayoutManager);

                        // 데이터 값 받아오기
                        datafetchForNotice(firstIndex, REQ_LENGTH);

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
                                currentItems    = linearLayoutManager.getChildCount();
                                totalItems      = linearLayoutManager.getItemCount();
                                scrollOutItems  = linearLayoutManager.findFirstVisibleItemPosition();

                                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                                    isScrolling = false;
                                    firstIndex += REQ_LENGTH;
                                    // data fetch
                                    datafetchForNotice(firstIndex, REQ_LENGTH);
                                }
                            }
                        });

                        // 총 컨테이너 페이저에 삽입
                        container.addView(view);
                        break;
                    // 그룹 일정 페이지
                    case 1:
//                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.group_detail_plan, null, false);
                        view = new GMapFragment().onCreateView(getLayoutInflater(), container, null);
                        android.app.FragmentManager fragmentManager = getFragmentManager();
                        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.groupPlanMap);

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
                            }
                        });

                        container.addView(view);
                        // 리사이클러 뷰를 갈아 끼우는 함수 (setPage) 를 이용할 방법 없는지 확인 할 것
                        break;
                    // 그룹 멤버 리스트
                    case 2:
//                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.group_detail_members, null, false);
//
//                        recyclerView = (RecyclerView) view.findViewById(R.id.groupMemberList);
//                        recyclerView.setHasFixedSize(true);
//                        recyclerView.setLayoutManager(linearLayoutManager);
//
//                        datafetchForMember("388c7730-afc5-3bdf-b839-332a589763a1");
//
//                        container.addView(view);
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
    private void datafetchForNotice(final int startIndex, final int length) {
        // 비동기
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {
                // http 리퀘스트
                String result = HttpRequestConnection.request(Conf.HTTP_ADDR + "/notice/" + startIndex + "/" + length, null);
                return result;
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
                    // 처음 실행 시에는 어댑터 생성
                    if (firstIndex == 0) {
                        TabsActivity.adapterNotice = new RecycleAdapterForGDetail(R.layout.notice_item, dataListNotice);
                        TabsActivity.rvNotice.setAdapter(TabsActivity.adapterNotice);
                    } else { // 아니라면 기존 어댑터에 변경사항 수용
                        TabsActivity.adapterNotice.notifyDataSetChanged();
                    }
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }.execute();
    }

    // 멤버 리스트 받아오기
//    private void datafetchForMember(final String groupId) {
//        // 비동기 통신
//        new AsyncTask<Void, Integer, String>() {
//            // 백그라운드 작업
//            @Override
//            protected String doInBackground(Void... params) {
//                String result = HttpRequestConnection.request(Conf.HTTP_ADDR + "/groupMembers/" + groupId, null);
//                return result;
//            }
//
//            // 작업 완료 시
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                try {
//                    JSONArray jsonArray = new JSONArray(s);
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        TabsActivity.tmpJsonObj = jsonArray.getJSONObject(i);
//
//                        // 비동기 통신
//                        new AsyncTask<Integer, Integer, String>(){
//                            @Override
//                            protected String doInBackground(Integer... params) {
//                                String resultUserInfo = "";
//                                try {
//                                    resultUserInfo = HttpRequestConnection.request(Conf.HTTP_ADDR + "/" + TabsActivity.tmpJsonObj.getString("uuid"), null);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                return resultUserInfo;
//                            }
//
//                            @Override
//                            protected void onPostExecute(String s) {
//                                super.onPostExecute(s);
//                                try {
//                                    Log.d("sibal", s);
//                                    JSONObject groupUser = new JSONArray(s).getJSONObject(0);
//                                    GroupUserInfoBean user = new GroupUserInfoBean(
//                                            groupUser.getString("uuid"),
//                                            groupUser.getString("name"),
//                                            groupUser.getString("profilePic"),
//                                            groupUser.getString("phone"),
//                                            groupUser.getInt("gender")
//                                    );
//                                    dataList.add(user);
//                                } catch (JSONException je) {
//                                    je.printStackTrace();
//                                }
//                            }
//                        }.execute(i);
//                    }
//                    TabsActivity.adapter = new RecycleAdapterForGDetail(R.layout.member_list, dataList);
//                    TabsActivity.recyclerView.setAdapter(adapter);
//
//                } catch (JSONException je) {
//                    je.printStackTrace();
//                }
//            }
//        }.execute();
//    }
}