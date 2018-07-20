package kr.ac.yjc.wdj.hikonnect.activities.groupDetail;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;
import com.github.mikephil.charting.data.LineData;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import devlight.io.library.ntb.NavigationTabBar;
import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.LoadingDialog;
import kr.ac.yjc.wdj.hikonnect.activities.LoginActivity;
import kr.ac.yjc.wdj.hikonnect.activities.MainActivity;
import kr.ac.yjc.wdj.hikonnect.activities.group.GroupNoticeActiviry;
import kr.ac.yjc.wdj.hikonnect.activities.group_list.groups_list_main;
import kr.ac.yjc.wdj.hikonnect.activities.myPage.UserJoinedGroup;
import kr.ac.yjc.wdj.hikonnect.activities.myPage.UserRecordActivity;
import kr.ac.yjc.wdj.hikonnect.activities.myPage.UserProfileActivity;
import kr.ac.yjc.wdj.hikonnect.activities.groups.NoticeActivity;
import kr.ac.yjc.wdj.hikonnect.adapters.MemberListAdapter;
import kr.ac.yjc.wdj.hikonnect.adapters.RecycleAdapterForGDetail;
import kr.ac.yjc.wdj.hikonnect.beans.Bean;
import kr.ac.yjc.wdj.hikonnect.beans.GroupNotice;
import kr.ac.yjc.wdj.hikonnect.beans.GroupSchedule;
import kr.ac.yjc.wdj.hikonnect.beans.GroupUserInfoBean;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Sungeun Kang (kasueu0814@gmail.com), Areum Lee (leear5799@gmail.com)
 * @since 2018-04-10
 */
public class TabsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    // UI 변수
    private ViewPager viewPager;          // 전체 페이지
    private NavigationTabBar navigationTabBar;   // 네비게이션 탭 바
    private Button btnEnterGroup,      // 그룹 참가 버튼
            btnExitGroup,       // 그룹 탈퇴 버튼
            btnDeleteGroup,     // 그룹 삭제 버튼
            btnAcceptUser,
            btnRejectUser;
    private FloatingActionMenu mainBtn;            // 플로팅 버튼들을 포괄하는 메인 플로팅 버튼
    private LoadingDialog loadingDialog;      // 로딩 화면
    private DrawerLayout drawer;             // drawer
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private TextView txtView,
            tvGroupName,        // 그룹 명
            scheduleTxtView;

    // UI 내부 데이터 연결 객체
    private ArrayList<NavigationTabBar.Model> models;             // 네비게이션 탭 안의 메뉴 구성을 위한 모델
    private static RecycleAdapterForGDetail adapterNotice,      // rvNotice에 값을 연결할 어댑터
            adapterMember,      // rvMember에 값을 연결할 어댑터
            adapterSchedule,    // rvPlan 에 값을 연결할 어댑터
            adapterWarning;     // rvWarning에 값을 연결할 어댑터

    private MemberListAdapter adapterMemberJoined;// rvMemberJoined에 값을 연결할 어댑터
    private ArrayList<Bean> dataListNotice,     // notice URL 요청으로 받아온 값을 담아둘 곳
            dataListMember,     // member URL 요청으로 받아온 값을 담아둘 곳
            dataListSchedule,   // schedule URL 요청으로 받아온 값을 담아둘 곳
            dataListWarning;    // notice, member, schedule URl 요청으로 받아온 값이
    // 없을 경우, 데이터가 없는 공간의 이름을 담아둘 곳
    private ArrayList<GroupUserInfoBean> dataListJoinedMember;

    // 데이터 담아둘 변수
    private static final int PAGE_COUNT = 3;     // 총 페이지의 수
    private AsyncTask imageUpload;
    private static String status;             // 사용자가 해당 그룹에 참여하고 있는 지 여부
    public static String groupId,            // 해당 그룹의 id
            groupName,          // 그룹명
            userId,             // 현재 사용자의 id
            memberId;           // 참가 신청한 사용자의 id
    private String[] colors;             // 안드로이드 내장 색상을 불러올 배열
    private int firstIndex,         // 요청 공지사항 데이터 시작 인덱스
            Ncount,             // 공지사항 데이터가 있는지 확인하기 위한 변수
            Scount;             // 일정 데이터가 있는지 확인하기 위한 변수

    // 무한 스크롤
    private boolean isScrolling;        // 현재 스크롤 되고 있는지 확인
    private int currentItems,       // 현재 스크롤 위치 파악하기 위한 변수
            totalItems,
            scrollOutItems;

    // Session
    private SharedPreferences pref;

    // OKHttp
    private OkHttpClient client;

    // Floating Button
    private boolean isFABOpen = false;
    private View fabBGLayout;
    private LinearLayout fabLayout1, fabLayout2, fabLayout3, fabLayout4;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_detail_drawer);

        pref = getSharedPreferences("loginData", MODE_PRIVATE);

        // [1] 변수 초기화
        // intent
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        groupName = intent.getStringExtra("groupName");
        status = intent.getStringExtra("status");

        // UI 변수
        btnEnterGroup = (Button) findViewById(R.id.btnEnterGroup);
        btnExitGroup = (Button) findViewById(R.id.btnExitGroup);
        btnDeleteGroup = (Button) findViewById(R.id.btnDeleteGroup);
        viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        loadingDialog = new LoadingDialog(this);
        tvGroupName = (TextView) findViewById(R.id.tvGroupName);
        tvGroupName.setText(groupName);

        // 데이터 변수
        models = new ArrayList<>();
        colors = getResources().getStringArray(R.array.default_preview);
        isScrolling = false;
        dataListNotice = new ArrayList<>();
        dataListMember = new ArrayList<>();
        dataListJoinedMember = new ArrayList<>();
        dataListSchedule = new ArrayList<>();
        firstIndex = 0;

        // 어댑터 변수
        adapterNotice = new RecycleAdapterForGDetail(R.layout.notice_item, dataListNotice, TabsActivity.this);
        adapterSchedule = new RecycleAdapterForGDetail(R.layout.schedule_item_cardview_, dataListSchedule, TabsActivity.this);
        adapterMember = new RecycleAdapterForGDetail(R.layout.member_list, dataListMember, status, TabsActivity.this);
        adapterMemberJoined = new MemberListAdapter(R.layout.member_list_schedule, dataListJoinedMember, status);

        // OKHttp
        client = new OkHttpClient();

        // 사용자의 권한 파악
        toggleBtnIfJoined();

        // 툴바 초기화
        initToolbar();


        // 페이지 불러오기
        initUI();

        // 공지사항 리스트 받아오기
        datafetchForNotice(groupId, firstIndex);

        // 참여자 값 받기
        datafetchForMember(groupId);

        // 버튼에 리스너 달기
        setListenerToFloatingButton();

    }

    /**
     * 플로팅 버튼에 리스너 달기
     */
    private void setListenerToFloatingButton() {
        // 그룹 참가
        btnEnterGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Integer, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        loadingDialog.show();
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        userId = pref.getString("user_id", "");

                        try {
                            OkHttpClient client = new OkHttpClient();

                            RequestBody body = new FormBody.Builder()
                                    .add("userid", userId)
                                    .add("uuid", groupId)
                                    .build();

                            Request request = new Request.Builder()
                                    .url(Environments.LARAVEL_HIKONNECT_IP + "/api/member")
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
                        Toast.makeText(
                                getBaseContext(),
                                "참가신청 되었습니다.",
                                Toast.LENGTH_SHORT
                        ).show();
                        loadingDialog.dismiss();
                        btnEnterGroup.setVisibility(View.GONE);
                    }
                }.execute();
            }
        });

        // 그룹 탈퇴
        btnExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder exitAd = new AlertDialog.Builder(TabsActivity.this);

                exitAd.setTitle("그룹 탈퇴 확인");
                exitAd.setMessage("그룹을 탈퇴하시겠습니까?");

                // 확인 버튼 설정
                exitAd.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Event
                        new AsyncTask<Void, Integer, String>() {

                            @Override
                            protected String doInBackground(Void... params) {
                                userId = pref.getString("user_id", "");

                                try {
                                    RequestBody body = new FormBody.Builder()
                                            .add("userid", userId)
                                            .add("uuid", groupId)
                                            .build();

                                    Request request = new Request.Builder()
                                            .url(Environments.LARAVEL_HIKONNECT_IP + "/api/out_group")
                                            .post(body)
                                            .build();

                                    Response response = client.newCall(request).execute();

                                    return response.body().string();

                                } catch (IOException ie) {

                                    Log.e("IOExcept", "IOException at btnExitGroup.setOnClickListener!!\n" + ie);
                                    return null;

                                }
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);

                                if (s == "false") {
                                    Toast.makeText(
                                            getBaseContext(),
                                            "오류로 인해 탈퇴하지 못했습니다.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                } else {
                                    Toast.makeText(
                                            getBaseContext(),
                                            "성공적으로 탈퇴되었습니다.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    status = "guest";
                                    toggleBtnIfJoined();
                                }
                            }
                        }.execute();
                    }
                });

        /*// 공지사항 작성
        btnMakeNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 공지사항 작성 페이지로 이동
                // 공지사항 페이지로 그룹id 전달
                Intent intent = new Intent(getBaseContext(), GroupNoticeActiviry.class);
                intent.putExtra("groupId", TabsActivity.groupId);
                startActivity(intent);
            }
        });*/
            }
        });

        // 그룹 삭제
        btnDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Integer, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        loadingDialog.show();
                    }

                    @Override
                    protected String doInBackground(Void... params) {

                        try {
                            Request request = new Request.Builder()
                                    .url(Environments.LARAVEL_HIKONNECT_IP + "/api/hikingGroup/" + groupId)
                                    .delete()
                                    .build();

                            Response response = client.newCall(request).execute();

                            return response.body().string();

                        } catch (IOException ie) {

                            Log.e("IOExcept", "IOException at btnExitGroup.setOnClickListener!!\n" + ie);
                            return null;

                        }
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        Toast.makeText(
                                getBaseContext(),
                                "성공적으로 삭제되었습니다.",
                                Toast.LENGTH_SHORT
                        ).show();
                        loadingDialog.dismiss();

                        Intent intent = new Intent(TabsActivity.this, groups_list_main.class);
                        startActivity(intent);
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
                     * @return view
                     */
                    @Override
                    public Object instantiateItem(final ViewGroup container, final int position) {
                        View view = null; // 페이지에 넣을 View

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
                                        currentItems = recyclerView.getLayoutManager().getChildCount();
                                        totalItems = recyclerView.getLayoutManager().getItemCount();
                                        scrollOutItems = ((LinearLayoutManager) recyclerView.getLayoutManager())
                                                .findFirstVisibleItemPosition();

                                        if (isScrolling && (currentItems + scrollOutItems > totalItems)) {
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
                                RecyclerView rvMember = (RecyclerView) view.findViewById(R.id.groupMemberList);
                                RecyclerView rvMemberJoined = (RecyclerView) view.findViewById(R.id.groupMemberListJoined);

                                if (status.equals("\"owner\"")) {
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
                addModels(R.drawable.ic_notifications_white_24px, "#22B573", "お知らせ", "Notice");
                addModels(R.drawable.ic_event_white_24px, "#22B573", "日程", "Plan");
                addModels(R.drawable.ic_group_white_24px, "#22B573", "メンバー", "Members");

                // 탭 모델 설정
                navigationTabBar.setModels(models);

                // 뷰페이저 설정
                navigationTabBar.setViewPager(viewPager, 0);

                navigationTabBar.setIsBadged(true);

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
                        navigationTabBar.getModels().get(position).hideBadge();
                    }

                    @Override
                    public void onPageScrollStateChanged(final int state) {

                    }
                });
            }

            /**
             * 툴바 초기화
             */
            private void initToolbar() {
                Log.e("null??", R.id.toolbar + "/" + toolbar);
                // 툴바 설정
                toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
                toolbar.setTitle("");
                setSupportActionBar(toolbar);

                // 토글 버튼 설정
                toggle = new ActionBarDrawerToggle(
                        TabsActivity.this,
                        drawer,
                        toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close
                );

                // 토글 버튼 추가
                drawer.addDrawerListener(toggle);
                toggle.syncState();

                // 각 리스트 별 리스너 장착
                navigationView.setNavigationItemSelectedListener(TabsActivity.this);

                // 프로필 이름 설정
                txtView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.main_textview);
                txtView.setText(pref.getString("user_name", ""));

                // 프로필 사진
                final CircularImageView imageView = (CircularImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_imageView);


                // 서버에서 사진 받아와 넣기
                imageUpload = new AsyncTask<Void, Integer, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... params) {


                        try {

                            HttpUrl httpUrl = HttpUrl
                                    .parse(Environments.NODE_HIKONNECT_IP + "/images/UserProfile/" + pref.getString("user_id", "") + ".jpg")
                                    .newBuilder()
                                    .build();


                            Request req = new Request.Builder().url(httpUrl).build();
                            Response res = client.newCall(req).execute();
                            InputStream is = res.body().byteStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            return bitmap;

                        } catch (IOException ie) {

                            ie.printStackTrace();
                            return null;

                        }
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);

                        if (bitmap != null) {

                            imageView.setImageBitmap(bitmap);

                        } else {

                            BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(getBaseContext(), R.drawable.circle_solid_profile_512px);
                            Bitmap defaultImg = drawable.getBitmap();

                            imageView.setImageBitmap(Bitmap.createScaledBitmap(defaultImg, 50, 50, true));

                        }

                    }
                }.execute();
            }

            /**
             * 1. 사용자가 오너라면, 공지사항 작성 버튼 & 일정 작성 버튼을 보여준다.
             * 2. 사용자가 멤버라면, 그룹 탈퇴 버튼 & 공지사항 작성 버튼 & 일정 작성 버튼을 보여준다.
             * 3. 사용자가 오너도 멤버도 아니라면, 그룹 참가 버튼을 보여준다.
             */
            private void toggleBtnIfJoined() {
                switch (status) {
                    // 그룹 owner인 경우 => 모든 버튼 표시 X
                    case "\"owner\"":
                        btnEnterGroup.setVisibility(View.GONE);
                        btnExitGroup.setVisibility(View.GONE);
                        btnDeleteGroup.setVisibility(View.VISIBLE);
                        break;
                    // 그룹 member 여부 판별
                    case "\"member\"":
                        userId = pref.getString("user_id", "");
                        checkUserStatus(userId);
                        break;
                    // guest인 경우
                    case "\"guest\"":
                        btnEnterGroup.setVisibility(View.VISIBLE);
                        btnExitGroup.setVisibility(View.GONE);
                        btnDeleteGroup.setVisibility(View.GONE);
                        break;
                }
            }

            private void checkUserStatus(final String userId) {
                final String userid = userId;

                // 비동기
                new AsyncTask<Void, Integer, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        loadingDialog.show();
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            // http 리퀘스트
                            OkHttpClient client = new OkHttpClient();

                            Request request = new Request.Builder()
                                    .url(Environments.LARAVEL_HIKONNECT_IP + "/api/list_member/" + TabsActivity.groupId)
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
                            Log.d("list_member", s);
                            // json 파싱
                            JSONArray jsonArray = new JSONArray(s);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            JSONArray NotEnter = jsonObject.getJSONArray("not_enter");
                            Log.d("not_enter", NotEnter.toString());

                            // 가입 신청 상태인지 확인
                            for (int count = 0; count < NotEnter.length(); count++) {
                                JSONObject object = NotEnter.getJSONObject(count);

                                if (object.getString("userid").equals(userid)) {
                                    // 그룹에 가입 신청 상태인 경우
                                    btnEnterGroup.setVisibility(View.GONE);
                                    btnExitGroup.setVisibility(View.GONE);
                                    btnDeleteGroup.setVisibility(View.GONE);
                                } else {
                                    // 그룹 member인 경우
                                    btnEnterGroup.setVisibility(View.GONE);
                                    btnExitGroup.setVisibility(View.VISIBLE);
                                    btnDeleteGroup.setVisibility(View.GONE);
                                }
                            }
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                    }
                }.execute();
            }


            // 탭에 모델 추가
            private void addModels(int drawable, String color, String title, String bgTitle) {
                models.add(
                        new NavigationTabBar.Model.Builder(
                                getResources().getDrawable(drawable),
                                Color.parseColor(color))
                                .title(title)
                                .badgeTitle(bgTitle)
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
                        loadingDialog.show();
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            // http 리퀘스트
                            OkHttpClient client = new OkHttpClient();

                            Request request = new Request.Builder()
                                    .url(Environments.LARAVEL_HIKONNECT_IP + "/api/list_announce/" + groupId + "/" + page)
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
                            JSONArray jsonArray = new JSONArray(s);
                            GroupNotice groupNotice = null;
                            if (jsonArray.length() != 0) {
                                // 공지사항 데이터가 있다면
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

                                // 데이터 변경 어댑터에 알리기
                                adapterNotice.notifyDataSetChanged();
                                Log.d("adapters", adapterNotice.getItemCount() + "");
                                firstIndex++;

                                loadingDialog.dismiss();
                            } else {
                                // 데이터가 없다면
                                groupNotice = new GroupNotice(/*"No Data In Notice List"*/null, /*"데이터가 없습니다."*/null, null, null, null);
                                // dataList에 삽입
                                dataListNotice.add(groupNotice);
                                // 데이터 변경 어댑터에 알리기
                                adapterNotice.notifyDataSetChanged();

                                loadingDialog.dismiss();
                            }
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
                                    .url(Environments.LARAVEL_HIKONNECT_IP + "/api/schedule/" + groupId)
                                    .build();

                            Log.d("schedule", Environments.LARAVEL_HIKONNECT_IP + "/api/schedule/" + groupId);

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
                            JSONArray jsonArray = new JSONArray(s);

                            if (jsonArray.length() != 0) {
                                // 스케줄 데이터가 있다면
                                // 결과 배열 값을
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    Log.d("mnt_id", Integer.toString(jsonObject.getInt("no")));

                                    // 데이터 넣기
                                    dataListSchedule.add(new GroupSchedule(
                                            jsonObject.getInt("no"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("content"),
                                            jsonObject.getString("leader"),
                                            jsonObject.getDouble("mnt_id"),
                                            jsonObject.getString("start_date"),
                                            jsonObject.getString("route"),
                                            getBaseContext()
                                    ));
                                }
                                // 데이터 변경 어댑터에 알리기
                                adapterSchedule.notifyDataSetChanged();

                                loadingDialog.dismiss();
                            } else {
                                // 데이터가 없다면
                                dataListSchedule.add(new GroupSchedule(-1, /*"데이터가 없습니다."*/null, null, /*"No Data In Schedule List"*/null, 000000000, null, null, getBaseContext()));
                                // 데이터 변경 어댑터에 알리기
                                adapterSchedule.notifyDataSetChanged();

                                loadingDialog.dismiss();
                            }
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
                        loadingDialog.show();
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            // http 리퀘스트
                            OkHttpClient client = new OkHttpClient();

                            Request request = new Request.Builder()
                                    .url(Environments.LARAVEL_HIKONNECT_IP + "/api/list_member/" + groupId)
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
                            JSONArray jsonArray = new JSONArray(s);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            GroupUserInfoBean userInfoBean = null;

                            JSONArray arrayNotEnter = jsonObject.getJSONArray("not_enter");
                            JSONArray arrayEnter = jsonObject.getJSONArray("enter");

                            // 결과 배열 값을
                            for (int i = 0; i < arrayNotEnter.length(); i++) {
                                JSONObject objNotEnter = arrayNotEnter.getJSONObject(i);
                                // 객체로 분해 해 Bean 객체에 삽입
                                userInfoBean = new GroupUserInfoBean(
                                        objNotEnter.getString("userid"),
                                        objNotEnter.getString("nickname"),
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

                            for (int j = 0; j < arrayEnter.length(); j++) {
                                JSONObject objEnter = arrayEnter.getJSONObject(j);
                                userInfoBean = new GroupUserInfoBean(
                                        objEnter.getString("userid"),
                                        objEnter.getString("nickname"),
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

                            loadingDialog.dismiss();
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                    }
                }.execute();

            }

            // get data from server for notice list
            // data가 있는지 없는지 체크
            private int ckNresult(final String groupId, final int page) {
                // 비동기
                new AsyncTask<Void, Integer, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            // http 리퀘스트
                            OkHttpClient client = new OkHttpClient();

                            Request request = new Request.Builder()
                                    .url(Environments.LARAVEL_HIKONNECT_IP + "/api/list_announce/" + groupId + "/" + page)
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
                        Log.d("Notice", s);

                        try {
                            // JSON  파싱
                            JSONArray jsonArray = new JSONArray(s);

                            Ncount = jsonArray.length();
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                    }
                }.execute();

                return Ncount;
            }

            private int ckSresult(final String groupId) {
                new AsyncTask<Void, Integer, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            // 클라이언트 객체
                            OkHttpClient client = new OkHttpClient();

                            // 리퀘스트 생성
                            Request request = new Request.Builder()
                                    .url(Environments.LARAVEL_HIKONNECT_IP + "/api/schedule/" + groupId)
                                    .build();

                            Log.d("schedule", Environments.LARAVEL_HIKONNECT_IP + "/api/schedule/" + groupId);

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
                            // JSON  파싱
                            JSONArray jsonArray = new JSONArray(s);

                            Scount = jsonArray.length();
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                    }
                }.execute();

                return Scount;
            }


            // -----------------------------------------------------------------------------------

            // 오버라이딩 Drawer
            @Override
            public void onBackPressed() {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                } else {
                    super.onBackPressed();
                }
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_settings) {
                    return true;
                }

                return super.onOptionsItemSelected(item);
            }

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.groups) {
                    startActivity(new Intent(TabsActivity.this, groups_list_main.class));
                } else if (id == R.id.my_groups) {
                    startActivity(new Intent(TabsActivity.this, UserJoinedGroup.class));
                } else if (id == R.id.my_records) {
                    startActivity(new Intent(TabsActivity.this, UserRecordActivity.class));
                } else if (id == R.id.my_profile) {
                    startActivity(new Intent(TabsActivity.this, UserProfileActivity.class));
                } else if (id == R.id.log_out) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(TabsActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

//            session.logOutUser();
                    //startActivity(new Intent(this, PreActivity.class));
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        }
