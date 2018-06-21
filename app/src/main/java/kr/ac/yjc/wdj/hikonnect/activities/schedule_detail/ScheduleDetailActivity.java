package kr.ac.yjc.wdj.hikonnect.activities.schedule_detail;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.LoadingDialog;
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
    private TextView            tvScheduleTitle;    // 툴바 제목
    private ViewPager           viewPager;          // 뷰페이저
    private TabLayout           tabLayout;
    private ImageButton         btnGoBack;
    private LoadingDialog       loadingDialog;

    // 데이터 변수
    private String              status;             // 그룹의 손님/참가자/오너
    private double              mntId;              // 산 코드
    private String              content;            // 내용
    private String              startDate;          // 시작일
    private String              groupId;            // 그룹 아이디
    private int                 scheduleNo;         // 스케줄 번호
    private String              scheduleTitle;      // 스케줄 제목
    private ArrayList<Integer>  fidList;            // 경로(FID) 배열
    private ArrayList<LatLng>   routeList;          // 경로 좌표 배열

    // 스케줄 유저 리사이클러 뷰 관련
    private ArrayList<GroupUserInfoBean>    dataList;
    private MemberListAdapter               adapter;

    // 구글 맵 관련
    private GoogleMap           googleMap;          // 지도 객체
    private PolylineOptions     polylineOptions;    // 지도에 찍어 낼 PolyLineOption

    // OkHttp
    private OkHttpClient    client;

    // 상수
    private final int       PAGE_COUNT  = 2;
    private final String    LOG_TAG     = "SCH_DETAIL";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail_app_bar);

        loadingDialog = new LoadingDialog(this);

        initData();
        initUI();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        Intent intent   = getIntent();
        mntId           = intent.getDoubleExtra("mntId", 0);

        getMntRouteWithMntId(mntId);
    }

    /**
     * 데이터 초기화
     */
    private void initData() {

        // OkHttp
        client          = new OkHttpClient();
        // GoogleMaps
        polylineOptions = new PolylineOptions();

        Intent intent   = getIntent();
        status          = intent.getStringExtra("status");
        mntId           = intent.getDoubleExtra("mntId", 0);
        content         = intent.getStringExtra("content");
        startDate       = intent.getStringExtra("startDate");
        groupId         = TabsActivity.groupId;
        scheduleNo      = intent.getIntExtra("scheduleNo", 0);
        scheduleTitle   = intent.getStringExtra("scheduleTitle");
        routeList       = new ArrayList<>();

        // FID 리스트 초기화
        try {

            // 받아온 String을 JSON 객체로 변환
            JSONArray jArray    = new JSONArray(intent.getStringExtra("scheduleRoute"));
            // fidList 초기화
            fidList = new ArrayList<>();

            // 값 초기화
            for (int i = 0 ; i < jArray.length() ; i++) {
                fidList.add(jArray.getInt(i));
            }

        } catch (JSONException je) {

            Log.e(LOG_TAG, "JSONException was occurred while initing routeArrString!!!!! \n" + je);
        }

        dataList        = new ArrayList<>();
        adapter         = new MemberListAdapter(
                R.layout.member_list_schedule,
                dataList,
                status
        );

        getScheduleMembers(groupId, scheduleNo);
    }

    /**
     * UI 초기화
     */
    private void initUI() {

        tvScheduleTitle = (TextView)    findViewById(R.id.scheduleTitle);
        viewPager       = (ViewPager)   findViewById(R.id.viewPager);
        tabLayout       = (TabLayout)   findViewById(R.id.tabs);
        btnGoBack       = (ImageButton) findViewById(R.id.btnGoBack);

        // 버튼 초기화

        // 뒤로가기 버튼에 리스너 장착
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 타이틀 지정
        tvScheduleTitle.setText(scheduleTitle);

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
                        TextView tvPlan         = (TextView) view.findViewById(R.id.tvPlan);

                        // TODO 산 이름으로
                        getMntNameFromMntId(mntId, mountainName);
                        tvPlan.setText(startDate + "\n\n" + content);

                        container.addView(view);
                        break;
                    // 스케줄에 참여한 인원 페이지
                    case 1:
                        // 멤버 리스트 페이지 내부 레이아웃 가져오기
                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.schedule_page_member, null, false);

                        // RecyclerView 찾아
                        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.scheduleMember);

                        // TODO 어댑터 붙이기
                        recyclerView.setAdapter(adapter);
                        recyclerView.setHasFixedSize(true);

                        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

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
     * @param tv            산 이름 출력할 텍스트 뷰
     */
    private void getMntNameFromMntId(final double inputMntId, final TextView tv) {
        // http 리퀘스트로 산 이름 알아내기
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadingDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url(Environments.LARAVEL_HIKONNECT_IP + "/api/mnt_name/" + inputMntId)
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

                // 유니코드 디코딩
                StringBuffer buffer = new StringBuffer();

                for (int i = s.indexOf("\\u") ; i > -1 ; i = s.indexOf("\\u")) {
                    buffer.append(s.substring(0, i));
                    buffer.append(String.valueOf( (char) Integer.parseInt( s.substring(i + 2, i + 6), 16 ) ));
                    s = s.substring(i + 6);
                }

                buffer.append( s );

                // 변수에 넣기
                tv.setText( buffer.toString().replace("\"", "") );

                loadingDialog.cancel();
            }
        }.execute();
    }

    /**
     * 스케줄 멤버 리스트 반환
     * @param groupId       그룹 uuid
     * @param scheduleNo    스케줄 번호
     * @return              스케줄 멤버 리스트
     */
    private void getScheduleMembers(final String groupId, final int scheduleNo) {

        // http 리퀘스트로 스케줄 멤버 리스트 만들기
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadingDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {

                try {

                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url(Environments.LARAVEL_HIKONNECT_IP + "/api/schedule_member/" + groupId + "/" + scheduleNo)
                            .build();

                    Response response = client.newCall(request).execute();

                    return response.body().string();

                } catch (IOException ie) {

                    Log.e(LOG_TAG, "IOException was occured in getScheduleMembers()!!!!! \n" + ie);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                Log.d(LOG_TAG, s);
                try {

                    JSONArray jsonArray = new JSONArray(s);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);

                        dataList.add(new GroupUserInfoBean(
                                object.getString("userid"),
                                object.getString("nickname"),
                                object.getInt("gender"),
                                object.getInt("age_group"),
                                object.getInt("scope"),
                                object.getString("phone"),
                                object.getString("grade"),
                                getBaseContext()
                        ));
                    }

                    adapter.notifyDataSetChanged();
                } catch (JSONException je) {
                    je.printStackTrace();
                }
                loadingDialog.cancel();
            }
        }.execute();

    }

    /**
     * 산 코드로 산 경로 배열 (lat, lng) 받아와 지도에 찍기
     * @param inputMntId    산 코드
     */
    private void getMntRouteWithMntId(final double inputMntId) {

        new AsyncTask<Double, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadingDialog.show();
            }

            @Override
            protected String doInBackground(Double... params) {

                try {

                    Request request = new Request.Builder()
                            .url(Environments.NODE_HIKONNECT_IP + "/paths/" + params[0].intValue())
                            .build();

                    Response response = client.newCall(request).execute();

                    return response.body().string();

                } catch (IOException ie) {

                    Log.e(LOG_TAG, "IOException was occured in getMntRouteWithMntId()!!!!\n" + ie);
                    return null;

                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                // 값을 파싱하여 필요한 경로만 polyLineOptions 에 넣기
                try {

                    Log.d("TEST", s);

                    // JSON parsing
                    JSONArray routes = new JSONArray(s);

                    for (int i = 0 ; i < routes.length() ; i++) {

                        JSONObject jsonObject   = routes.getJSONObject(i);
                        JSONObject infoObj      = jsonObject.getJSONObject("attributes");

                        if (!fidList.contains(infoObj.getInt("FID"))) {
                            continue;
                        }

                        JSONObject  geometryObj = jsonObject.getJSONObject("geometry");
                        JSONArray   tempsArr    = geometryObj.getJSONArray("paths");
                        JSONArray   pathsArr    = tempsArr.getJSONArray(0);

                        routeList       = new ArrayList<>();
                        polylineOptions = new PolylineOptions();

                        for (int j = 0; j < pathsArr.length(); j++) {

                            JSONObject pathObj = pathsArr.getJSONObject(j);

                            double lat = pathObj.getDouble("lat");
                            double lng = pathObj.getDouble("lng");

                            // 폴리라인 옵션에 지점 추가
                            LatLng latLng = new LatLng(lat, lng);

                            if (infoObj.getInt("FID") == fidList.get(0) && j == 0) {
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                            }

                            routeList.add(latLng);
                        }

                        polylineOptions.addAll(routeList);
                        polylineOptions.color(Color.RED);
                        polylineOptions.width(13);
                        googleMap.addPolyline(polylineOptions);
                    }

                    loadingDialog.cancel();
                } catch (JSONException je) {

                    Log.e(LOG_TAG, "JSONException was occurred in getMntRouteWithMntId()!!!!\n" + je);

                }


            }

        }.execute(inputMntId);
    }


}
