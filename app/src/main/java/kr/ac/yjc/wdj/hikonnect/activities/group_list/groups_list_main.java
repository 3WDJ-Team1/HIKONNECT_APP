package kr.ac.yjc.wdj.hikonnect.activities.group_list;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.LoadingDialog;
import kr.ac.yjc.wdj.hikonnect.activities.LoginActivity;
import kr.ac.yjc.wdj.hikonnect.activities.user.UserProfileActivity;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author  Jiyoon Lee, Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-04-10
 */
public class groups_list_main extends AppCompatActivity
        implements  AdapterView.OnItemSelectedListener,
                    NavigationView.OnNavigationItemSelectedListener {
    // UI 변수
    private RecyclerView            recyclerView;       // 그룹 리스트 출력
    private MyAdapter               list_adapter;       // 어댑터
    private List<ListViewItem>      listItems;          // 그룹 리스트 내용
    private LinearLayout            list, container;
    private EditText                searchInput;        // 검색 내용
    private Spinner                 spinner;            // 검색 옵션
    private DatePicker              datePicker;         // 날짜 선택기
    private Button                  button;
    private ImageButton             btnGroupSearch;     // 그룹 검색 버튼
    private ProgressBar             groupListPbar;      // 로딩 프로그레스
    private LoadingDialog           loadingDialog;
    private DrawerLayout            drawer;
    private Toolbar                 toolbar;
    private ActionBarDrawerToggle   toggle;
    private NavigationView          navigationView;
    private TextView                txtView;

    // 어댑터/핸들러/레이아웃 매니저
    private LinearLayoutManager     manager;

    public SharedPreferences        pref;

    // 기타 변수
    private Boolean isScrolling = false;
    private int     currentItems, totalItems, scrollOutItems;
//    private String  result;
    private         int   page      = 0;
    private         int   cusor     = 1;
    private final   int   REQ_COUNT = 10;

    // Http Request 관련 변수 및 상수
    private         String          select  = null;
    private         String          input   = null;
    private         OkHttpClient    client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list_drawer);

        pref            = getSharedPreferences("loginData", MODE_PRIVATE);
        datePicker      = (DatePicker)  findViewById(R.id.Datepicker);
        spinner         = (Spinner)     findViewById(R.id.group_search_spinner);
//        list            = (LinearLayout)findViewById(R.id.list);
//        container       = (LinearLayout)findViewById(R.id.container);
        button          = (Button)      findViewById(R.id.set);
        searchInput     = (EditText)    findViewById(R.id.tvSearchText);
        btnGroupSearch  = (ImageButton) findViewById(R.id.btnSearchGroup);
        manager         = new LinearLayoutManager(this);

        loadingDialog   = new LoadingDialog(this);

        ////////////////////////////////////////////검색창 프래그먼트 생성/////////////////////////////////////////////


        FragmentManager     fragmentManager     = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        edit_view fragment = new edit_view();

        ////////////////////////////////////////////////////스피너////////////////////////////////////////////////////
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.group_search_selector, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // 스피너 array 클릭시 이벤트 발생
        spinner.setOnItemSelectedListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        txtView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.main_textview);

        txtView.setText(pref.getString("user_name", "No data"));

        final CircularImageView imageView = (CircularImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_imageView);

        // OKHttpClient 초기화
        client = new OkHttpClient();

        new AsyncTask<Void, Integer, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    HttpUrl httpUrl = HttpUrl
                            .parse(Environments.NODE_HIKONNECT_IP + "/images/UserProfile/" + pref.getString("user_id", "") + ".jpg")
                            .newBuilder()
                            .build();

                    Request     req = new Request.Builder().url(httpUrl).build();

                    Response    res = client.newCall(req).execute();

                    InputStream is  = res.body().byteStream();

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

                imageView.setImageBitmap(bitmap);
            }
        }.execute();


        /////////////////////////////////////////////////////////////////////////////////////


        // TODO 검색 버튼 클릭 시 검색 수행
        btnGroupSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input   = searchInput.getText().toString();
                page    = 0;
                listItems.clear();
                loadRecyclerViewData();
            }
        });


        ////////////////////////////////////////////////////그룹 리스트//////////////////////////////////////////////////

        LayoutInflater  inflater    = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View            groupList   = inflater.inflate(R.layout.groups_list, list, true);

        // 프로그레스 바 초기화
        groupListPbar = (ProgressBar) groupList.findViewById(R.id.groupListPbar);

        ////////////////////////////////////////////////////RecylerView 채우기//////////////////////////////////////////
        recyclerView = (RecyclerView) findViewById(R.id.rvGroupList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        listItems = new ArrayList<>();

        list_adapter = new MyAdapter(listItems, pref);
        recyclerView.setAdapter(list_adapter);
        loadRecyclerViewData();

        // TODO url 갈아 끼우고 주석 풀기
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                currentItems    = manager.getChildCount();
                totalItems      = manager.getItemCount();
                scrollOutItems  = manager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    page++;
                    loadRecyclerViewData();
                }
            }
        });


    }

    /**
     * 리사이클러 뷰 내부 데이터 받아오는 함수
     */
    public void loadRecyclerViewData() {

        new AsyncTask<Void, Integer, String>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String result = "";

                try {
                    String selection = select;

                    if (input == null || input.length() == 0) {
                        selection = null;
                    }

                    Log.d("page", page + "");
                    // JSON 형식 객체 생성
                    String jsonString = "{" +
                            "\"select\":\"" + selection + "\"," +
                            "\"input\":\""  + input     + "\"," +
                            "\"page\":"     + page      +
                            "}";

                    Log.d("request", jsonString + "\n to: " + Environments.LARAVEL_HIKONNECT_IP + "/api/groupList");
                    // 서버에 요청
                    result = requestPost(
                            Environments.LARAVEL_HIKONNECT_IP + "/api/groupList",
                            jsonString
                    );
                } catch (IOException ie) {
                    ie.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                Log.d("result", s);

                try {
                    JSONArray jsonArray = new JSONArray(s);
                    for (int i = 0 ; i < jsonArray.length() ; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        // TODO  수정
                        listItems.add(new ListViewItem(
                                jsonObject.getString("uuid"),
                                jsonObject.getString("title"),
                                jsonObject.getString("nickname"),
                                jsonObject.getString("content"),
                                getBaseContext()
                        ));
                    }

                    list_adapter.notifyDataSetChanged();
                    loadingDialog.dismiss();
                } catch (JSONException je) {
                    Log.e("JSON", "JSON parsing error!!!!!!!\n" + je);
                }
            }
        }.execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        switch (pos) {
            // 그룹 이름
            case 0:
                // 캘린더 보이지 않기
                calendar(0);
                // 전송할 데이터 변경
                select  = "groupname";
                break;
            // 작성자
            case 1:
                // 캘린더 보이지 않기
                calendar(0);
                // 전송할 데이터 변경
                select  = "writer";
                break;
            // 생성일자
            case 2:
                // 캘린더 보여주기
                calendar(1);
                // 전송할 데이터 변경
                select  = "date";
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void calendar(int i) {
        switch (i) {
            default:
            case 0:
                cusor = 1;
                datePicker.setVisibility(View.GONE);
                break;
            case 1:
                datePicker.setVisibility(View.VISIBLE);
                cusor = 0;
                break;

        }
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                searchInput.setText(datePicker.getYear() + "년 " + datePicker.getMonth() + "월 " + datePicker.getDayOfMonth() + "일");
                datePicker.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 서버에 포스트로 request 보내기 위한 함수
     * @param serverUrl 서버 주소
     * @param jsonData  JSON 형식으로 작성된 데이터
     * @return          응답 메세지
     */
    private String requestPost(String serverUrl, String jsonData) throws IOException {
        OkHttpClient    client  = new OkHttpClient();
        // request body 만들기
        RequestBody body = RequestBody.create(Environments.JSON, jsonData);
        // request 객체 만들기
        Request request = new Request.Builder()
                .url(serverUrl)
                .post(body)
                .build();
        // 응답 받아오기
        Response response = client.newCall(request).execute();
        // 응답 메세지 반환
        return response.body().string();
    }

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
            startActivity(new Intent(this, groups_list_main.class));
        } /*else if (id == R.id.my_groups) {
            startActivity(new Intent(this, UserGroupActivity.class));
        }*/ else if (id == R.id.my_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
        } else if (id == R.id.log_out) {

            Intent intent = new Intent(groups_list_main.this, LoginActivity.class);
            startActivity(intent);
//            session.logOutUser();
            //startActivity(new Intent(this, PreActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}