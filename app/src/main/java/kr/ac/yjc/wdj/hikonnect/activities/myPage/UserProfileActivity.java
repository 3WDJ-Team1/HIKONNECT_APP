package kr.ac.yjc.wdj.hikonnect.activities.myPage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.session.SessionManager;

/**
 * 유저 상세 프로필 액티비티
 * @author  Sungeun Kang (kasueu0814@gmail.com), Areum Lee
 * @since   2018-05-17
 */
public class UserProfileActivity extends AppCompatActivity{
    // UI 변수
    private ImageView               userImg;
    private TextView                tvUserId,           // 유저 아이디
                                    tvNickName,         // 닉네임
                                    tvPhone,            // 휴대전화 번호
                                    tvGender,           // 성별
                                    tvAge,              // 연령대
                                    tvRange,            // 공개 범위
                                    tvPhoneOpenRange,   // 전화번호 공개 여부
                                    tvGenderOpenRange,  // 성별 공개 여부
                                    tvAgeOpenRange;     // 연령대 공개 여부
    private FloatingActionButton    fabModify;          // 수정 버튼

    // 데이터 변수
    private SharedPreferences       preferences;
    private String                  id;
    private String                  openRange,
                                    openPhone,          // 전화번호 공개 여부
                                    openGender,         // 성별 공개 여부
                                    openAge;            // 연령대 공개 여부

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        preferences = getSharedPreferences("loginData", MODE_PRIVATE);
        id          = preferences.getString("user_id", "");

        init();
    }

    /**
     * 프로필 이미지 다운로드 받아올 AsyncTask 상속 객체
     */
    public class DownloadImage extends AsyncTask<Void, Void, Bitmap> {
        String userid;

        public DownloadImage(String userid) {
            this.userid = userid;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            String url = Environments.NODE_HIKONNECT_IP + "/images/UserProfile/" + userid + ".jpg";

            try {
                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000 * 30);

                return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                userImg.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * UI 변수 초기화
     */
    private void init() {
        userImg             = (ImageView)   findViewById(R.id.user_profile_img);
        tvUserId            = (TextView)    findViewById(R.id.user_id_txt);
        tvNickName          = (TextView)    findViewById(R.id.user_nickname_txt);
        tvPhone             = (TextView)    findViewById(R.id.user_phoneNum_txt);
        tvGender            = (TextView)    findViewById(R.id.user_gender_txt);
        tvAge               = (TextView)    findViewById(R.id.user_age_txt);
        tvRange             = (TextView)    findViewById(R.id.user_open_range);
        tvPhoneOpenRange    = (TextView)    findViewById(R.id.phoneOpenRange);
        tvGenderOpenRange   = (TextView)    findViewById(R.id.genderOpenRange);
        tvAgeOpenRange      = (TextView)    findViewById(R.id.ageOpenRange);

        fabModify           = (FloatingActionButton) findViewById(R.id.fab);

        // 이미지 설정
        new DownloadImage(id).execute();

        // 기타 값 설정
        tvUserId.setText(id);
        tvNickName.setText(preferences.getString("user_name", ""));
        tvPhone.setText(preferences.getString("user_phone", ""));
        tvGender.setText(preferences.getString("user_gender", ""));
        tvAge.setText(preferences.getString("user_age_group", "") + "대");

        // 공개 범위 설정
        String openScope    = preferences.getString("user_open_scope", "");

        switch (openScope.length()) {
            case 3:
                openPhone   = "비공개";
                openGender  = "비공개";
                openAge     = "비공개";
                openRange   = "비공개";
                break;
            case 4:
                initOpenSettings(openScope, 4);
                openRange   = "그룹 내 공개";
                break;
            case 5:
                initOpenSettings(openScope, 5);
                openRange   = "전체 공개";
                break;
        }

        tvRange.setText(openRange);
        tvPhoneOpenRange.setText(openPhone);
        tvGenderOpenRange.setText(openGender);
        tvAgeOpenRange.setText(openAge);

        // 수정버튼에 리스너 달기
        fabModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), UserProfileModifyActivity.class);

                intent.putExtra("openRange", openRange);
                intent.putExtra("phoneOpen", openPhone);
                intent.putExtra("genderOpen", openGender);
                intent.putExtra("ageOpen", openAge);

                startActivity(intent);
                finish();
            }
        });
    }

    private void initOpenSettings(String strScope, int length) {
        openPhone   = (strScope.charAt(length - 3) == '1') ? "공개" : "비공개";
        openGender  = (strScope.charAt(length - 2) == '1') ? "공개" : "비공개";
        openAge     = (strScope.charAt(length - 1) == '1') ? "공개" : "비공개";
    }

}
