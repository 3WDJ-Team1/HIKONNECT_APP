package kr.ac.yjc.wdj.hikonnect.activities.schedule_detail;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.Environment;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.groupDetail.TabsActivity;
import kr.ac.yjc.wdj.hikonnect.adapters.MemberListAdapter;
import kr.ac.yjc.wdj.hikonnect.beans.GroupUserInfoBean;
import kr.ac.yjc.wdj.hikonnect.models.GMapFragment;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 그룹 스케줄 상세보기 액티비티
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-17
 */
public class ScheduleDetailActivity extends FragmentActivity implements OnMapReadyCallback {
    // UI 변수
    private TextView        tvScheduleTitle;    // 툴바 제목
    private ViewPager       viewPager;          // 뷰페이저
    private TabLayout       tabLayout;

    // 데이터 변수
    private String          status;             // 그룹의 손님/참가자/오너
    private String          mntName;            // 산 이름
    private double          mntId;              // 산 코드
    private String          content;            // 내용
    private String          route;              // 경로
    private String          startDate;          // 시작일
    private String          groupId;            // 그룹 아이디
    private int             scheduleNo;         // 스케줄 번호

    // 상수
    private final int       PAGE_COUNT = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        initData();
        initUI();
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

    /**
     * 데이터 초기화
     */
    private void initData() {
        Intent intent = getIntent();
        status      = intent.getStringExtra("status");
        mntId       = intent.getDoubleExtra("mntId", 0);
        content     = intent.getStringExtra("content");
        route       = intent.getStringExtra("route");
        startDate   = intent.getStringExtra("startDate");
        groupId     = TabsActivity.groupId;
        scheduleNo  = intent.getIntExtra("scheduleNo", 0);
    }

    /**
     * UI 초기화
     */
    private void initUI() {
        tvScheduleTitle = (TextView)    findViewById(R.id.scheduleTitle);
        viewPager       = (ViewPager)   findViewById(R.id.viewPager);
        tabLayout       = (TabLayout)   findViewById(R.id.tabs);

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return PAGE_COUNT;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            /**
             * 페이지 내부 설정
             * @param container 컨테이너 뷰 그룹
             * @param position  몇 번 째 페이지?
             * @return          view
             */
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = null;

                switch (position) {
                    // 일정 페이지
                    case 0:
                        // 일정 페이지 내부 레이아웃 가져오기
                        view = new GMapFragment().onCreateView(getLayoutInflater(), container, null);

                        // 값 수정
                        FragmentManager fragmentManager = getFragmentManager();
                        MapFragment     mapFragment     = (MapFragment) fragmentManager.findFragmentById(R.id.scheduleMap);

                        mapFragment.getMapAsync(ScheduleDetailActivity.this);

                        TextView mountainName   = (TextView) view.findViewById(R.id.mountainName);
                        TextView routes         = (TextView) view.findViewById(R.id.routes);
                        TextView tvPlan         = (TextView) view.findViewById(R.id.tvPlan);

                        // TODO 산 이름으로
                        mountainName.setText(mntId + "");
                        routes.setText(route);
                        tvPlan.setText(startDate + "\n" + content);

                        container.addView(view);
                        break;
                    // 스케줄에 참여한 인원 페이지
                    case 1:
                        // 멤버 리스트 페이지 내부 레이아웃 가져오기
                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.schedule_page_member, container, false);

                        // RecyclerView 찾아
                        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.scheduleMember);

                        ArrayList<GroupUserInfoBean> dataList = new ArrayList<>();

                        // TODO 어댑터 붙이기
                        recyclerView.setAdapter(new MemberListAdapter(
                                R.layout.member_list_schedule,
                                dataList,
                                status
                        ));

                        container.addView(view);
                        break;
                }

                return view;
            }
        });

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(R.color.black, R.color.cyan_500);
        tabLayout.getTabAt(0).setText("목적지 및 일정");
        tabLayout.getTabAt(1).setText("멤버");
    }

    /**
     * 산 코드로 산 이름 찾아서 반환
     * @param inputMntId    산 코드
     * @return              산 이름
     */
    private String getMntNameFromMntId(double inputMntId) {
        // http 리퀘스트로 산 이름 알아내기
        return null;
    }

    /**
     * 스케줄 멤버 리스트 반환
     * @param groupId       그룹 uuid
     * @param scheduleNo    스케줄 번호
     * @return              스케줄 멤버 리스트
     */
    private ArrayList<GroupUserInfoBean> getScheduleMembers(final String groupId, final int scheduleNo) {

        ArrayList<GroupUserInfoBean> list = new ArrayList<>();

        // http 리퀘스트로 스케줄 멤버 리스트 만들기
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {

                try {
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url(Environment.LARAVEL_SOL_SERVER + "/schedule_member/" + groupId + "/" + scheduleNo)
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

                Log.d("SCHEDULE", s);


            }
        }.execute();
        return null;
    }
}
