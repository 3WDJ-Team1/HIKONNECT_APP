package kr.ac.yjc.wdj.hikonnect.activities.group_list;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.ac.yjc.wdj.hikonnect.apis.HttpRequest.HttpRequestConnection;
import kr.ac.yjc.wdj.hikonnect.R;

/**
 * @author  Jiyoon Lee
 * @since   2018-04-10
 */
public class groups_list_main extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter list_adapter;
    private List<ListViewItem> listItems;
    private LinearLayout list;
    private LinearLayout container;
    private Spinner spinner;
    private DatePicker datePicker;
    private Button button;
    private Handler handler;
    private Boolean isScrolling = false;
    private int currentItems, totalItems, scrollOutItems;
    private String result;
    private LinearLayoutManager manager;
    int page = 0;
    private HttpRequestConnection req = new HttpRequestConnection();

    private int cusor = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list_main);

        datePicker = (DatePicker) findViewById(R.id.Datepicker);
        spinner = (Spinner) findViewById(R.id.planets_spinner);
        list = (LinearLayout) findViewById(R.id.list);
        container = (LinearLayout) findViewById(R.id.container);
        button = (Button) findViewById(R.id.set);
        manager = new LinearLayoutManager(this);

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
        inflater.inflate(R.layout.groups_list, list, true);


        ////////////////////////////////////////////////////RecylerView 채우기//////////////////////////////////////////
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        listItems = new ArrayList<>();
        listItems.add(new ListViewItem("제목", getBaseContext()));
        listItems.add(new ListViewItem("아이고", getBaseContext()));
        listItems.add(new ListViewItem("허어...", getBaseContext()));
        listItems.add(new ListViewItem("지친다...", getBaseContext()));
        listItems.add(new ListViewItem("이젠 그만...", getBaseContext()));
        listItems.add(new ListViewItem("하고싶다...", getBaseContext()));
        listItems.add(new ListViewItem("교수님..", getBaseContext()));
        listItems.add(new ListViewItem("집에 보내 주세요...", getBaseContext()));

        recyclerView.setAdapter(new MyAdapter(listItems));
        // TODO url 갈아 끼우고 주석 풀기
        // loadRecyclerViewData();
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//                    isScrolling = true;
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                currentItems    = manager.getChildCount();
//                totalItems      = manager.getItemCount();
//                scrollOutItems  = manager.findFirstVisibleItemPosition();
//
//                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
//                    isScrolling = false;
//                    page += 10;
//                    loadRecyclerViewData();
//                }
//            }
//        });


    }

    public void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = req.request("http://172.25.1.167:8000/group/0/10", null);
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        }).start();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray array = jsonObject.getJSONArray("groupInformations");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject o = array.getJSONObject(i);
//                        ListViewItem item = new ListViewItem(
//                                o.getString("title")
//                        );
//                        listItems.add(item);
                    }
                    list_adapter = new MyAdapter(listItems);
                    recyclerView.setAdapter(list_adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
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
}