package kr.ac.yjc.wdj.hikonnect.activities.group_list;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.ac.yjc.wdj.hikonnect.Environment;
import kr.ac.yjc.wdj.hikonnect.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author  Jiyoon Lee, Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-04-10
 */
public class groups_list_main extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // UI 변수
    private RecyclerView            recyclerView;
    private MyAdapter               list_adapter;
    private List<ListViewItem>      listItems;
    private LinearLayout            list, container;
    private Spinner                 spinner;
    private DatePicker              datePicker;
    private Button                  button;
    private ProgressBar             groupListPbar;

    // 어댑터/핸들러/레이아웃 매니저
    private LinearLayoutManager     manager;

    // 기타 변수
    private Boolean isScrolling = false;
    private int     currentItems, totalItems, scrollOutItems;
//    private String  result;
                    int   page      = 0;
    private         int   cusor     = 1;
    private final   int   REQ_COUNT = 10;

    // Http Request 관련 변수 및 상수
    private         String          select  = null;
    private         String          input   = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list_main);

        datePicker  = (DatePicker) findViewById(R.id.Datepicker);
        spinner     = (Spinner) findViewById(R.id.planets_spinner);
        list        = (LinearLayout) findViewById(R.id.list);
        container   = (LinearLayout) findViewById(R.id.container);
        button      = (Button) findViewById(R.id.set);
        manager     = new LinearLayoutManager(this);

        ////////////////////////////////////////////검색창 프래그먼트 생성/////////////////////////////////////////////


        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        edit_view fragment = new edit_view();

        // 실행 시 띄워 줄 프래그먼트 적용
        fragmentTransaction.add(R.id.container, fragment).commit();


        ////////////////////////////////////////////////////스피너////////////////////////////////////////////////////
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.groups_list_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // 스피너 array 클릭시 이벤트 발생
        spinner.setOnItemSelectedListener(this);

        // 컨테이너 클릭시 TextView 일 시 캘린더 호출
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cusor == 0) {
                    calendar(1);
                }
            }
        });


        ////////////////////////////////////////////////////그룹 리스트//////////////////////////////////////////////////

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View groupList = inflater.inflate(R.layout.groups_list, list, true);

        // 프로그레스 바 초기화
        groupListPbar = (ProgressBar) groupList.findViewById(R.id.groupListPbar);

        ////////////////////////////////////////////////////RecylerView 채우기//////////////////////////////////////////
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        listItems = new ArrayList<>();

        list_adapter = new MyAdapter(listItems);
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
                    isScrolling =   false;
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
                groupListPbar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... params) {
                String result = "";

                try {
                    Log.d("page", page + "");
                    // JSON 형식 객체 생성
                    String jsonString = "{" +
                            "\"select\":\"" + select    + "\"," +
                            "\"input\":\""  + input     + "\"," +
                            "\"page\":"     + page      +
                            "}";
                    Log.d("request", jsonString);
                    // 서버에 요청
                    result = requestPost(
                            Environment.LARAVEL_SOL_SERVER + "/groupList",
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
                                /*jsonObject.getString("leader")*/"test",
                                jsonObject.getString("content"),
                                getBaseContext()
                        ));
//                        listItems.add(new ListViewItem(jsonObject.getString("title"), getBaseContext()));
                    }

                    list_adapter.notifyDataSetChanged();
                } catch (JSONException je) {
                    Log.e("JSON", "JSON parsing error!!!!!!!\n" + je);
                }

                groupListPbar.setVisibility(View.GONE);
            }
        }.execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if (pos == 2) calendar(1);
        else calendar(0);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void calendar(int i) {
        Fragment fragment;
        switch (i) {
            default:
            case 0: {
                fragment = new edit_view();
                cusor = 1;
                break;
            }
            case 1: {
                datePicker.setVisibility(datePicker.VISIBLE);
                fragment = new text_view();
                cusor = 0;
                break;
            }
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).commit();
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // textView.setText(datePicker.getDayOfMonth() + "." + datePicker.getMonth() + "." + datePicker.getYear());
                text_view.textView0.setText(datePicker.getDayOfMonth() + "." + datePicker.getMonth() + "." + datePicker.getYear());
                datePicker.setVisibility(datePicker.INVISIBLE);
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
        RequestBody body = RequestBody.create(Environment.JSON, jsonData);
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
}