package kr.ac.yjc.wdj.hikonnect.activities.group;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * The Activity used when make group schedule
 * @author  Areum Lee (leear5799@gmail.com)
 * @since   2018-06-13
 */

public class GroupScheduleActivity extends AppCompatActivity{
    // UI 변수
    private EditText                scheduleTitle,              // 일정 제목
                                    scheduleContents,           // 일정 내용
                                    nowScheduleDate,
                                    nowScheduleTime;
    private Button                  mapFindBtn;                 // 지도 찾기 버튼
    private AutoCompleteTextView    nowScheduleDestination;     // 목적지

    private ArrayAdapter<String>    adapter;    // 산 이름 자동 완성 adapter

    // 데이터를 담을 변수
    private String[]                items;      // 입력된 글자로 검색한 산이름들의 리스트
    private int                     mntId;      // 산코드

    private static final String LOG_TAG         = "GroupScheduleAcitivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_schedule_app_bar);

        // 변수 초기화
        // UI 변수
        scheduleTitle           = (EditText) findViewById(R.id.scheduleTitle);
        scheduleContents        = (EditText) findViewById(R.id.scheduleContents);
        nowScheduleDate         = (EditText) findViewById(R.id.nowScheduleDate);
        nowScheduleTime         = (EditText) findViewById(R.id.nowScheduleTime);
        mapFindBtn              = (Button) findViewById(R.id.mapFindBtn);
        nowScheduleDestination  = (AutoCompleteTextView) findViewById(R.id.nowScheduleDestination);

        // 버튼에 리스너 달기
        BtnListeners();
    }

    private void BtnListeners() {
        mapFindBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("클릭했다",  nowScheduleDestination.getText().toString());

                // MapFragment 호출
                // MapFragment 해당 산의 이름 전달
                Intent intent = new Intent(getBaseContext(), PopUpActivity.class);
                intent.putExtra("mntName", nowScheduleDestination.getText().toString());

                startActivity(intent);
            }
        });
    }

    private void mntNameAutoComplete() {
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {

                try {

                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url(Environments.LARAVEL_HIKONNECT_IP + "/api/searchMount/" +
                                    nowScheduleDestination.getText())
                            .build();

                    Response response = client.newCall(request).execute();

                    return response.body().string();

                } catch (IOException ie) {
                    Log.e(LOG_TAG, ie.getMessage());
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

                        items[i] = object.getString("mnt_name");
                    }

                    adapter  = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_dropdown_item_1line, items);

                    nowScheduleDestination.setAdapter(adapter);

                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }.execute();
    }
}
