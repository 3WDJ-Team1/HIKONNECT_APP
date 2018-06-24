package kr.ac.yjc.wdj.hikonnect.activities.group;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.LoadingDialog;
import kr.ac.yjc.wdj.hikonnect.activities.group_list.GroupMenuActivity;
import kr.ac.yjc.wdj.hikonnect.activities.group_list.groups_list_main;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * The Activity used when make hiking group
 * @author  Areum Lee (leear5799@gmail.com)
 * @since   2018-06-10
 */

public class GroupActivity extends AppCompatActivity {
    private LoadingDialog loadingDialog; // 로딩 화면

    private EditText    groupName,      // 그룹명
                        groupContents,  // 내용
                        minCount,       // 최소 모집 인원
                        maxCount;       // 최대 모집 인원
    private Button      okBtn,          // 그룹 생성 진행 버튼
                        cancelBtn;      // 그룹 생성 취소 버튼

    private SharedPreferences preferences;
    private String            id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_group_app_bar);

        // 변수 초기화
        loadingDialog       = new LoadingDialog(this);

        groupName       = (EditText) findViewById(R.id.GroupName);
        groupContents   = (EditText) findViewById(R.id.GroupContetns);
        minCount        = (EditText) findViewById(R.id.minNum);
        maxCount        = (EditText) findViewById(R.id.maxNum);

        okBtn       = (Button)findViewById(R.id.okBtn);
        cancelBtn   = (Button) findViewById(R.id.cancelBtn);

        preferences = getSharedPreferences("loginData", MODE_PRIVATE);
        id          = preferences.getString("user_id", "");

        setListnerToButton();
    }

    private void setListnerToButton() {

        // 확인 버튼 클릭 시
        // 그룹 생성 진행
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                            Log.d("id", id);
                            Log.d("title", groupName.getText().toString());
                            Log.d("content", groupContents.getText().toString());
                            Log.d("min", minCount.getText().toString());
                            Log.d("max", maxCount.getText().toString());

                            RequestBody body = new FormBody.Builder()
                                    .add("writer", id)
                                    .add("title", groupName.getText().toString())
                                    .add("content", groupContents.getText().toString())
                                    .add("min", minCount.getText().toString())
                                    .add("max", maxCount.getText().toString())
                                    .build();

                            Request request = new Request.Builder()
                                    .url(Environments.LARAVEL_HIKONNECT_IP + "/api/hikingGroup")
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
                        Log.d("return", s);

                        if (s == "false") {
                            Toast.makeText(
                                    getBaseContext(),
                                    "그룹 생성에 실패했습니다.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            loadingDialog.dismiss();

                            Intent intent = new Intent(getBaseContext(), GroupMenuActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(
                                    getBaseContext(),
                                    "그룹 생성에 성공했습니다.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            loadingDialog.dismiss();

                            Intent intent = new Intent(getBaseContext(), groups_list_main.class);
                            startActivity(intent);
                        }
                    }
                }.execute();
            }

        });

        // 취소 버튼 클릭 시
        // 그룹 메뉴 페이지로 return
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), GroupMenuActivity.class);
                startActivity(intent);
            }
        });
    }
}
