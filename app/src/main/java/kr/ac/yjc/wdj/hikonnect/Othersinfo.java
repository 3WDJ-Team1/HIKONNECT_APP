package kr.ac.yjc.wdj.hikonnect;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.ac.yjc.wdj.hikonnect.apis.HttpRequest.HttpRequestConnection;

/**
 * Created by jungyu on 2018-05-02.
 */

public class Othersinfo extends Activity {
    private String              nickname, hiking_group;
    private Double              distancee;
    private Double              velocity;
    private List<String>        list;       // 데이터를 넣은 리스트변수
    private ListView            listView;   // 검색을 보여줄 리스트변수
    private EditText            editSearch; // 검색어를 입력할 Input 창
    private SearchAdapter       adapter;    // 리스트뷰에 연결할 아답터
    private ArrayList<String>   nicknames;
    private ArrayList<Integer>  memberNos;
    private int                 member_num;

    Handler                 handler;
    ContentValues           contentValues   = new ContentValues();
    String                  result, user;
    int                     my_num;
    HttpRequestConnection   hrc             = new HttpRequestConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        editSearch  = (EditText) findViewById(R.id.editSearch);
        listView    = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Log.d("HIKONNECT", "nickname: " + nicknames.get(pos));
                Log.d("HIKONNECT", "memberNo: " + memberNos.get(pos));
                // Intent intent = new Intent(Othersinfo.this,MapsActivity.class);
                // intent.putExtra("fromother")
            }
        });

        // 리스트를 생성한다.
        list = new ArrayList<>();
        nicknames = new ArrayList<>();
        memberNos = new ArrayList<>();

        // 검색에 사용할 데이터을 미리 저장한다.
        Intent intent = getIntent();
        my_num = intent.getIntExtra("my_num",0);
        contentValues.put("member_no",my_num);
        new Thread(new Runnable() {

            @Override
            public void run() {
                result = hrc.request(Environment.LARAVEL_HIKONNECT_IP+"/api/getScheduleMembers",contentValues);
                Log.i("result", result);

                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        }).start();
        handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                try {
                    JSONArray jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        nickname = jsonObject.getString("nickname");
                        member_num = jsonObject.getInt("member_no");

                        settingList(nickname);
                        nicknames.add(nickname);
                        memberNos.add(member_num);
                        list.add(nickname);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };


        // 리스트에 연동될 아답터를 생성한다.
        adapter = new SearchAdapter(list, this);

        // 리스트뷰에 아답터를 연결한다.
        listView.setAdapter(adapter);

        // input창에 검색어를 입력시 "addTextChangedListener" 이벤트 리스너를 정의한다.
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                search(text);
            }
        });


    }

    // 검색을 수행하는 메소드
    public void search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        list.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            list.addAll(nicknames);
        }
        // 문자 입력을 할때..
        else
        {
            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0; i < nicknames.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (nicknames.get(i).toLowerCase().contains(charText))
                {
                    // 검색된 데이터를 리스트에 추가한다.
                    list.add(nicknames.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }


    // 검색에 사용될 데이터를 리스트에 추가한다.
    private void settingList(String nickname){
        list.add(nickname);
    }
}
