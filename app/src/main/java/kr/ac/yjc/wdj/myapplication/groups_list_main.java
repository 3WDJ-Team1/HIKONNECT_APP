package kr.ac.yjc.wdj.myapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class groups_list_main extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String     URL_DATA = "https://simplifiedcoding.net/demos/marvel/";
    private RecyclerView            recyclerView;
    private RecyclerView.Adapter    list_adapter;
    private List<ListViewItem>      listItems;
    private LinearLayout            list;
    private LinearLayout            container;
    private Spinner                 spinner;
    private DatePicker              datePicker;
    private Button                  button;

    int cusor = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list_main);

        datePicker      = (DatePicker) findViewById(R.id.Datepicker);
        spinner         = (Spinner) findViewById(R.id.planets_spinner);
        list            = (LinearLayout) findViewById(R.id.list);
        container       = (LinearLayout) findViewById(R.id.container);
        button          = (Button) findViewById(R.id.set);


        ////////////////////////////////////////////검색창 프래그먼트 생성/////////////////////////////////////////////


        FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
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
                if(cusor == 0)  {
                    calendar(1);
                }
            }
        });


        ////////////////////////////////////////////////////그룹 리스트//////////////////////////////////////////////////

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(R.layout.groups_list, list, true);


        ////////////////////////////////////////////////////RecylerView 채우기//////////////////////////////////////////
                recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                listItems = new ArrayList<>();

                loadRecyclerViewData();


    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if (pos == 2)   calendar(1);
        else            calendar(0);

    }

    private void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            JSONArray array = jsonObject.getJSONArray("groupInformations");
                            JSONArray array = new JSONArray(response);
                            for(int i = 0; i < array.length(); i++) {
                                JSONObject o = array.getJSONObject(i);
                                ListViewItem item = new ListViewItem(
                                        o.getString("name")
                                );
                                listItems.add(item);
                            }

                            list_adapter = new MyAdapter(listItems, getApplicationContext());
                            recyclerView.setAdapter(list_adapter);

                        } catch (JSONException e)   {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void calendar(int i)  {
        Fragment fragment;
        switch (i)   {
            default:
            case 0: {
                fragment=new edit_view();
                cusor = 1;
                break;
            }
            case 1: {
                datePicker.setVisibility(datePicker.VISIBLE);
                fragment=new text_view();
                cusor = 0;
                break;
            }
        }
        FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
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

