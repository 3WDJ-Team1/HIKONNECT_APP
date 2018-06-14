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
import kr.ac.yjc.wdj.hikonnect.activities.groupDetail.TabsActivity;
import kr.ac.yjc.wdj.hikonnect.activities.group_list.GroupMenuActivity;
import kr.ac.yjc.wdj.hikonnect.activities.group_list.groups_list_main;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * The Activity used when make group notice
 * @author  Areum Lee (leear5799@gmail.com)
 * @since   2018-06-13
 */

public class GroupNoticeActiviry extends AppCompatActivity {
    // UI 변수
    private Button          findImgFileBtn,     // 이미지 파일 찾기 버튼
                            okBtn,              // 확인 버튼
                            cancelBtn;          // 취소 버튼
    private EditText        noticeTitle,        // 공지사항 제목
                            noticeContents;     // 공지사항 내용
    private LoadingDialog   loadingDialog;      // 로딩 화면

    // 데이터 담을 변수
    private String          userId;             // 사용자 id

    // Session
    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_notice_app_bar);

        preferences         = getSharedPreferences("loginData", MODE_PRIVATE);
        userId              = preferences.getString("user_id", "");

        // 변수 초기화
        // UI 변수
        loadingDialog       = new LoadingDialog(this);

        findImgFileBtn      = (Button) findViewById(R.id.fileFindBtn);
        okBtn               = (Button) findViewById(R.id.okBtn);
        cancelBtn           = (Button) findViewById(R.id.cancelBtn);

        noticeTitle         = (EditText) findViewById(R.id.noticeTitle);
        noticeContents      = (EditText) findViewById(R.id.noticeContents);

        // 각 버튼에 클릭 리스너 달기
        setBtnClickListner();
    }

    // 버튼별 클릭 리스너
    private void setBtnClickListner() {
        // 파일 찾기
        findImgFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });

        // 확인
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

                            RequestBody body = new FormBody.Builder()
                                    .add("writer", userId)
                                    .add("uuid", TabsActivity.groupId)
                                    .add("title", noticeTitle.getText().toString())
                                    .add("content", noticeContents.getText().toString())
                                    .add("picture", "")
                                    .build();

                            Request request = new Request.Builder()
                                    .url(Environments.LARAVEL_HIKONNECT_IP + "/api/notice")
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
                                    "공지사항 작성에 실패했습니다.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            loadingDialog.dismiss();

                            Intent intent = new Intent(getBaseContext(), GroupMenuActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(
                                    getBaseContext(),
                                    "공지사항이 작성되었습니다.",
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

        // 취소
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이전 화면으로 돌아가기
                GroupNoticeActiviry.super.onBackPressed();
            }
        });
    }
}
