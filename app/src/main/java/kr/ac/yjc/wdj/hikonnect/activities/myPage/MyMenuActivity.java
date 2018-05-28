package kr.ac.yjc.wdj.hikonnect.activities.myPage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.io.InputStream;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 내 메뉴 액티비티
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-26
 */
public class MyMenuActivity extends AppCompatActivity {

    // UI 변수
    private CircularImageView   userProfilePic;     // 유저 프로필 사진
    private TextView            userNickname,       // 유저 닉네임
                                userId;             // 유저 아이디
    private Button              btnToProfile,       // 프로필로 이동하는 버튼
                                btnToRecord,        // 기록 보기로 이동하는 버튼
                                btnToMyGroups;      // 내 그룹 보기로 이동하는 버튼

    // 데이터 변수
    private String              loginedUserId;      // 로그인 한 사용자 아이디

    // 세션 유지
    private SharedPreferences   preferences;

    // 상수
    private final String MY_MENU_LOG_TAG = "MYMENU";    // 로그캣 출력 태그

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_menu_app_bar);

        preferences     = getSharedPreferences("loginData", MODE_PRIVATE);
        loginedUserId   = preferences.getString("user_id", "");

        initUI();
    }

    /**
     * UI 변수 초기화
     */
    private void initUI() {
        userProfilePic  = (CircularImageView)   findViewById(R.id.userProfilePic);
        userNickname    = (TextView)            findViewById(R.id.userNickname);
        userId          = (TextView)            findViewById(R.id.userId);
        btnToProfile    = (Button)              findViewById(R.id.btnToProfile);
        btnToRecord     = (Button)              findViewById(R.id.btnToRecord);
        btnToMyGroups   = (Button)              findViewById(R.id.btnToMyGroups);

        // 내부 데이터 초기화
        userNickname.setText(preferences.getString("user_name", ""));
        userId.setText(loginedUserId);

        // 프로필 사진 받아오기
        getProfileImageFromServer();

        // 리스너 달기
        setListeners();
    }

    /**
     * 서버에서 유저의 프로필 이미지 받아와 초기화
     */
    private void getProfileImageFromServer() {

        new AsyncTask<Void, Integer, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {

                    // http Request
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url(Environments.NODE_HIKONNECT_IP + "/images/UserProfile/" + loginedUserId + ".jpg")
                            .build();

                    Response response = client.newCall(request).execute();

                    InputStream is = response.body().byteStream();

                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    return bitmap;

                } catch (IOException ie) {
                    Log.e(MY_MENU_LOG_TAG, "IOException was occurred while loading profile image from server!!!! \n" + ie);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                userProfilePic.setImageBitmap(bitmap);
            }
        }.execute();

    }

    /**
     * 버튼들에 이동하는 리스너 달기
     */
    private void setListeners() {
        // 프로필로 이동
        btnToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), UserProfileActivity.class);
                startActivity(intent);
            }
        });

        // 기록 보기로 이동
        btnToRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), UserRecordActivity.class);
                startActivity(intent);
            }
        });

        // 내가 참여한 그룹 보기로 이동
        btnToMyGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), UserJoinedGroup.class);
                startActivity(intent);
            }
        });
    }
}
