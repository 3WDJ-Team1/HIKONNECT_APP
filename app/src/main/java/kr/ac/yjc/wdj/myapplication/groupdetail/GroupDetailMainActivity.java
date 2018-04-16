package kr.ac.yjc.wdj.myapplication.groupdetail;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.ac.yjc.wdj.myapplication.APIs.HttpRequest.HttpRequestConnection;
import kr.ac.yjc.wdj.myapplication.R;
import kr.ac.yjc.wdj.myapplication.adapters.RecycleAdapterForGDetail;
import kr.ac.yjc.wdj.myapplication.beans.Bean;
import kr.ac.yjc.wdj.myapplication.beans.GroupNotice;
import kr.ac.yjc.wdj.myapplication.beans.GroupUserInfoBean;
import kr.ac.yjc.wdj.myapplication.groupdetail.fragment.FragmentPlan;
import kr.ac.yjc.wdj.myapplication.groupdetail.fragment.RecyclerViewFragment;
import kr.ac.yjc.wdj.myapplication.groupdetail.fragment.RecyclerViewMember;
import kr.ac.yjc.wdj.myapplication.groupdetail.fragment.RecyclerViewNotice;
import kr.ac.yjc.wdj.myapplication.models.Conf;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GroupDetailMainActivity extends DrawerActivity{

    @BindView(R.id.materialViewPager)
    MaterialViewPager mViewPager;
    @BindView(R.id.btnEnterGroup)
    FloatingActionButton btnEnterGroup;

    // UI 내부 데이터 연결 객체
    public static RecycleAdapterForGDetail      adapterNotice,      // rvNotice에 값을 연결할 어댑터
                                                adapterMember;      // rvMember에 값을 연결할 어댑터
    private static ArrayList<Bean>              dataListNotice,     // notice URL 요청으로 받아온 값을 담아둘 곳
                                                dataListMember;     // member URL 요청으로 받아온 값을 담아둘 곳

    // 데이터 담아둘 변수
    private String[]            colors;             // 안드로이드 내장 색상을 불러올 배열
    private int                 firstIndex;         // 요청 공지사항 데이터 시작 인덱스
    private int                 firstIndexForMem;   // 요청 그룹 멤버 데이터 시작 인덱스
    private final int           REQ_LENGTH = 10;    // 한 번의 요청에 불러올 데이터 수
    private static final int    PAGE_COUNT = 2;     // 총 페이지의 수
    private static boolean      isJoined;           // 사용자가 해당 그룹에 참여하고 있는 지 여부
    private static String       groupId;            // 해당 그룹의 id

    // 무한 스크롤
    private boolean     isScrolling;                                // 현재 스크롤 되고 있는지 확인
    private int         currentItems, totalItems, scrollOutItems;   // 현재 스크롤 위치 파악하기 위한 변수

    // MediaType
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_detail_main);
        setTitle("");
        ButterKnife.bind(this);

        final Toolbar toolbar = mViewPager.getToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        isScrolling         = false;
        dataListNotice      = new ArrayList<>();
        dataListMember      = new ArrayList<>();
        firstIndex          = 0;
        firstIndexForMem    = 0;

        adapterNotice       = new RecycleAdapterForGDetail(R.layout.notice_item, dataListNotice);
        adapterMember       = new RecycleAdapterForGDetail(R.layout.member_list, dataListMember);

        groupId             = "16f78874-b51c-3ad0-9b91-5d35f22a412b";

        client              = new OkHttpClient();
        // 이 사람이 그룹에 있으면 그룹 참가 버튼 -> 그룹 탈퇴 버튼
//        initVarisJoined("", "");
        // 플로팅 버튼 누르면 그룹 참가
//        setListenerToFloatingButton();
        // 페이지 불러오기
        initUI();

        datafetchForNotice(groupId, firstIndex, REQ_LENGTH);
        datafetchForMember(groupId, firstIndexForMem, REQ_LENGTH);

    }

    /**
     * 사용자가 현재 그룹에 참가하고 있는 지 여부를 isJoined 에 넣음
     * @param groupId   그룹 아이디
     * @param userId    해당 사용자의 아이디
     */
    private void initVarisJoined(String groupId, String userId) {
        isJoined = false;
        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                String response = HttpRequestConnection.request("url", null);
                return response;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                GroupDetailMainActivity.isJoined = true;
            }
        }.execute(groupId, userId);
    }

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * 플로팅 버튼에 리스너 달기
     */
    private void setListenerToFloatingButton () {
        btnEnterGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                post(Conf.HTTP_ADDR + "/entryGroup", "{'groupId':" + "'" + groupId + "', 'userId': '}");
            }
        });
    }

    /**
     * 전체적인 UI 초기화 작업 --> 페이지 초기화 등
     */
    private void initUI() {

        mViewPager.getViewPager().setAdapter(/*
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
                                    datafetchForNotice(GroupDetailMainActivity.groupId, firstIndex, REQ_LENGTH);
                                }
                            }
                        });

                    // 그룹 일정 페이지
                    case 1:
                        view = new GMapFragment().onCreateView(getLayoutInflater(), container, null);
                        android.app.FragmentManager fragmentManager = getFragmentManager();
                        MapFragment mapFragment = (MapFragment)f                                                                                                                                                   ragmentManager.findFragmentById(R.id.groupPlanMap);

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
                                    datafetchForMember(GroupDetailMainActivity.groupId, firstIndexForMem, REQ_LENGTH);
                                }
                            }
                        });

                        container.addView(view);
                        break;
                }
                return view;
            }

        });*/

        new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                switch (position % PAGE_COUNT) {
                    case 0:
                        return RecyclerViewNotice.newInstance();
                   /* case 1:
                        return FragmentPlan.newInstance();*/
                    case 1:
                        return RecyclerViewMember.newInstance();
                    default:
                        return RecyclerViewFragment.newInstance();
                }
            }


            @Override
            public int getCount() {
                return PAGE_COUNT;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position % PAGE_COUNT) {
                    case 0:
                        return "Notice";
                    case 1:
                        return "Members";
                    case 2:
                        return "Members";
                }
                return "";
            }
        });

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.green,
                                "http://phandroid.s3.amazonaws.com/wp-content/uploads/2014/06/android_google_moutain_google_now_1920x1080_wallpaper_Wallpaper-HD_2560x1600_www.paperhi.com_-640x400.jpg");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.blue,
                                "http://www.hdiphonewallpapers.us/phone-wallpapers/540x960-1/540x960-mobile-wallpapers-hd-2218x5ox3.jpg");
                    case 2:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.cyan,
                                "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg");
                    case 3:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.red,
                                "http://www.tothemobile.com/wp-content/uploads/2014/07/original.jpg");
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        final View logo = findViewById(R.id.logo_white);
        if (logo != null) {
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.notifyHeaderChanged();
                    Toast.makeText(getApplicationContext(), "Yes, the title is clickable", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // get data from server for notice list
    private void datafetchForNotice(final String groupId, final int startIndex, final int length) {
        // 비동기
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {
                // http 리퀘스트
                String result = HttpRequestConnection.request(Conf.HTTP_ADDR + "/notice/" + groupId + "/" + startIndex + "/" + length, null);
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

                    adapterNotice.notifyDataSetChanged();
                    Log.d("adapters", adapterNotice.getItemCount() + "");
                    firstIndex += REQ_LENGTH;
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }.execute();
    }

    // 멤버 리스트 받아오기
    private void datafetchForMember(final String groupId, final int startIndex, final int length) {
        // 비동기
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {
                // http 리퀘스트
                String result = HttpRequestConnection.request(Conf.HTTP_ADDR + "/groupMembers/" + groupId + "/" + startIndex + "/" + length, null);
                return result;
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