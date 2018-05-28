package kr.ac.yjc.wdj.hikonnect.activities.myPage;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 유저 프로필 수정 액티비티
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-28
 */
public class UserProfileModifyActivity extends AppCompatActivity {

    // UI 변수
    private ImageView           userProfilePic;     // 사용자 프로필 사진
    private EditText            userName,           // 사용자 닉네임
                                phoneNum;           // 전화번호
    private Spinner             spinGender,         // 성별
                                spinAge,            // 연령대
                                spinOpenRange;      // 공개범위
    private Switch              isPhonePublic,      // 전화번호 공개 여부
                                isGenderPublic,     // 성별 공개 여부
                                isAgePublic;        // 나이대 공개 여부
    private TextView            tvPhoneOpen,        // 전화번호 공개여부 상태 표시
                                tvGenderOpen,       // 성별 공개여부 상태 표시
                                tvAgeOpen;          // 연령대 공개여부 상태 표시
    private Button              btnSubmit,          // 수정 완료 버튼
                                btnCancel;          // 수정 취소 버튼

    // 세션
    private SharedPreferences   preferences;

    // 데이터 변수
    // 기존에 존재하던 데이터
    private String              phoneOpen,          // 전화번호 공개여부
                                genderOpen,         // 성별 공개여부
                                ageOpen,            // 연령대 공개 여부
                                openRange;          // 공개 범위
    // R.array 변환 데이터
    private List<String>        genderList,         // R.array.gender
                                ageList,            // R.array.age_group
                                openRangeList;      // R.array.open_range

    // OkHttp
    private OkHttpClient        client;

    // 상수
    private final String        LOG_TAG = "MODPROFILE";     // 로그캣 출력 태그

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_user_profile);

        initData();
        initUI();
    }

    /**
     * 데이터 초기화
     */
    private void initData() {
        Intent intent = getIntent();

        // 사용자 데이터
        phoneOpen       = intent.getStringExtra("phoneOpen");
        genderOpen      = intent.getStringExtra("genderOpen");
        ageOpen         = intent.getStringExtra("ageOpen");
        openRange       = intent.getStringExtra("openRange");

        // OkHttp
        client          = new OkHttpClient();

        // 세션
        preferences     = getSharedPreferences("loginData", MODE_PRIVATE);

        // R.array...
        genderList      = Arrays.asList(getResources().getStringArray(R.array.gender));
        ageList         = Arrays.asList(getResources().getStringArray(R.array.age_group));
        openRangeList   = Arrays.asList(getResources().getStringArray(R.array.open_range));
    }

    /**
     * UI 초기화
     */
    private void initUI() {
        userProfilePic  = (ImageView)   findViewById(R.id.user_profile_img);
        userName        = (EditText)    findViewById(R.id.user_nickname_mTxt);
        phoneNum        = (EditText)    findViewById(R.id.user_phoneNum_mTxt);
        spinGender      = (Spinner)     findViewById(R.id.gender_spinner);
        spinAge         = (Spinner)     findViewById(R.id.age_spinner);
        spinOpenRange   = (Spinner)     findViewById(R.id.range_spinner);
        isPhonePublic   = (Switch)      findViewById(R.id.phoneNum_lock_switch);
        isGenderPublic  = (Switch)      findViewById(R.id.gender_lock_switch);
        isAgePublic     = (Switch)      findViewById(R.id.age_lock_switch);
        tvPhoneOpen     = (TextView)    findViewById(R.id.tvIsPhoneOpen);
        tvGenderOpen    = (TextView)    findViewById(R.id.tvIsGenderOpen);
        tvAgeOpen       = (TextView)    findViewById(R.id.tvIsAgeOpen);
        btnSubmit       = (Button)      findViewById(R.id.btnSubmit);
        btnCancel       = (Button)      findViewById(R.id.btnCancel);

        setClickListners();
        setProfilePic();

        userName.setText(preferences.getString("user_name", ""));
        phoneNum.setText(preferences.getString("user_phone", ""));
        spinGender.setSelection(genderList.indexOf(preferences.getString("user_gender", "")));
        spinAge.setSelection(ageList.indexOf(preferences.getString("user_age_group", "") + "대"));
        spinOpenRange.setSelection(openRangeList.indexOf(openRange));
        setSwitchValue(isPhonePublic, phoneOpen, tvPhoneOpen);
        setSwitchValue(isGenderPublic, genderOpen, tvGenderOpen);
        setSwitchValue(isAgePublic, ageOpen, tvAgeOpen);
    }

    /**
     * 스위치에 값을 넣는 함수
     * @param s         초기화 될 스위치 객체
     * @param value     초기화 시킬 시드 값
     * @param statusTv  상태를 나타낼 TextView
     */
    private void setSwitchValue(Switch s, String value, TextView statusTv) {
        if (value.equals("공개")) {
            s.setChecked(true);
        } else {
            s.setChecked(false);
        }

        statusTv.setText(value);
    }

    /**
     * 버튼에 클릭 리스너 달기
     */
    private void setClickListners() {
        // 수정 버튼
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spinOpenRange.getSelectedItem().equals("전체 공개"))
                    openRange = "all";
                else
                    openRange = "group";

                final String inputUserName      = userName.getText().toString();
                final String inputUserPhone     = phoneNum.getText().toString();
                final String inputUserGender    = spinGender.getSelectedItem().toString();
                final String inputUserAge       = spinAge.getSelectedItem().toString();
                final String isOpenPhone        = String.valueOf(isPhonePublic.isChecked());
                final String isOpenGender       = String.valueOf(isGenderPublic.isChecked());
                final String isOpenAge          = String.valueOf(isAgePublic.isChecked());

                new AsyncTask<Void, Integer, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        try {

                            RequestBody body = new FormBody.Builder()
                                    .add("idv", preferences.getString("user_id", ""))
                                    .add("nn", inputUserName)
                                    .add("pwv", preferences.getString("user_password", ""))
                                    .add("phone", inputUserPhone)
                                    .add("gender", inputUserGender)
                                    .add("age", inputUserAge)
                                    .add("phonesc", isOpenPhone)
                                    .add("gendersc", isOpenGender)
                                    .add("agesc", isOpenAge)
                                    .add("groupsc", openRange)
                                    .build();

                            Request request = new Request.Builder()
                                    .url(Environments.LARAVEL_HIKONNECT_IP + "/api/user/" + preferences.getString("user_id", ""))
                                    .put(body)
                                    .build();

                            Response response = client.newCall(request).execute();

                            return response.body().string();

                        } catch (IOException ie) {

                            Log.e(LOG_TAG, "IOException occurred while modifing user's profile!!!!\n" + ie);
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);

                        // 실패하면 반환
                        if (!s.equals("\"true\""))
                            return;

                        // 성공하면 세션 업데이트
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putString("user_name", inputUserName);
                        editor.putString("user_phone", inputUserPhone);
                        editor.putString("user_gender", inputUserGender);
                        editor.putString("user_age_group", inputUserAge.replace("대", ""));
                        editor.putString("user_open_scope", makeOpenScope(isOpenPhone, isOpenGender, isOpenAge, openRange));
                        editor.apply();

                        Toast.makeText(getBaseContext(), "성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();

                        toProfilePage();
                    }
                }.execute();
            }
        });

        // 취소 버튼
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toProfilePage();
            }
        });
    }

    /**
     * 프로필 페이지로 이동
     */
    private void toProfilePage() {
        Intent intent = new Intent(getBaseContext(), UserProfileActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 프로필 사진 넣는 함수
     */
    private void setProfilePic() {
        new AsyncTask<String, Integer, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... params) {
                try {

                    HttpUrl httpUrl = HttpUrl
                            .parse(Environments.NODE_HIKONNECT_IP + "/images/UserProfile/" + params[0] + ".jpg")
                            .newBuilder()
                            .build();

                    Request req = new Request.Builder().url(httpUrl).build();

                    Response res = client.newCall(req).execute();

                    InputStream is = res.body().byteStream();

                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    return bitmap;

                } catch (IOException ie) {

                    Log.e(LOG_TAG, "IOException was occurred while getting profile pic!!!!\n" + ie);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                userProfilePic.setImageBitmap(bitmap);
            }
        }.execute(preferences.getString("user_id", ""));
    }

    /**
     * 공개 여부 정보를 통해 문자열 만들기
     * @param pOpen 휴대전화 공개
     * @param gOpen 성별 공개
     * @param aOpen 연령대 공개
     * @param range 공개 범위
     * @return      공개 여부 나타내는 문자열 (4~5자리)
     */
    private String makeOpenScope(String pOpen, String gOpen, String aOpen, String range) {
        String result = "";

        // 공개 범위 10 전체 01 그룹
        if (range.equals("전체 공개"))
            result += "10";
        else
            result += "1";

        // 휴대전화 공개여부 1 true 0 false
        if (Boolean.parseBoolean(pOpen))
            result += "1";
        else
            result += "0";

        // 성별 공개여부 1 true 0 false
        if (Boolean.parseBoolean(gOpen))
            result += "1";
        else
            result += "0";

        // 연령대 공개여부 1 true 0 false
        if (Boolean.parseBoolean(aOpen))
            result += "1";
        else
            result += "0";

        return result;
    }

    // 뒤로가기 버튼 실수로 눌렀을 경우를 대비해 다이얼로그 띄우기
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());

        // 제목 세팅
        builder.setTitle("수정 취소");

        // 다이얼로그 세팅
        builder .setMessage("수정을 취소하고 나가시겠습니까?")
                .setPositiveButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });

        // 다이얼로그 생성
        AlertDialog dialog = builder.create();

        // 보여주기
        dialog.show();
    }
}
