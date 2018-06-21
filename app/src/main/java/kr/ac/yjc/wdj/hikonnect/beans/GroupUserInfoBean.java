package kr.ac.yjc.wdj.hikonnect.beans;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 그룹 멤버 정보 저장할 객체
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-04-06
 * @see     Bean
 */
public class GroupUserInfoBean extends Bean {
    private String  userId;         // 유저 아이디
    private String  nickname;       // 유저 닉네임
    private String  grade;          // 등급
    private String  phone;          // 전화번호
    private String  gender;         // 성별
    private String  ageGroup;       // 연령대
    private String  enterDate;

    private boolean isPhoneOpen;    // 휴대전화 번호 공개 여부
    private boolean isGenderOpen;   // 성별 공개 여부
    private boolean isAgeGroupOpen; // 연령대 공개 여부
    private boolean openGroup;      // 공개 범위 (그룹)
    private boolean openAll;        // 공개 범위 (전체)

    private Context baseContext;
    private Bitmap  profilePic;     // 프로필 사진 비트맵


    /**
     * 스케줄 내부 멤버 볼 때 사용할 생성자
     * @param userId        String  유저 아이디
     * @param nickname      String  유저 닉네임
     * @param gender        int     성별
     * @param age_group     int     연령대
     * @param scope         int     공개범위
     * @param phone         String  휴대전화 번호
     * @param grade         String  등급
     * @param baseContext   Context 해당 액티비티의 baseContext
     */
    public GroupUserInfoBean(
                                String  userId,
                                String  nickname,
                                int     gender,
                                int     age_group,
                                int     scope,
                                String  phone,
                                String  grade,
                                Context baseContext
                            )
    {
        this(userId, nickname, grade, phone, null, gender, age_group, scope, baseContext);
    }

    /**
     * 초기화
     * @param userId        String  유저 아이디
     * @param nickname      String  유저 닉네임
     * @param grade         String  유저 등급
     * @param phone         String  유저 핸드폰 번호
     * @param enterDate     String  유저 들어온 날짜
     * @param gender        int     유저 성별
     * @param ageGroup      int     유저 연령대
     * @param scope         int     정보 공개 설정
     * @param baseContext   Context 해당 액티비티의 baseContext
     */
    public GroupUserInfoBean(
                                 String     userId,
                                 String     nickname,
                                 String     grade,
                                 String     phone,
                                 String     enterDate,
                                 int        gender,
                                 int        ageGroup,
                                 int        scope,
                                 Context    baseContext
                            )
    {
        this.userId         = userId;
        this.nickname       = nickname;
        this.grade          = grade;
        this.phone          = phone;
        this.enterDate      = enterDate;
        this.gender         = gender == 0 ? "남자" : "여자";
        this.ageGroup       = ageGroup + "대";
        this.baseContext    = baseContext;

        String strScope = scope + "";

        // 공개여부 문자열 길이에 따라 값 조정
        switch (strScope.length()) {
            case 3:
                openGroup   = false;
                openAll     = false;
                break;
            case 4:
                openGroup   = true;
                openAll     = false;
                initOpenSettings(strScope, 4);
                break;
            case 5:
                openGroup   = true;
                openAll     = true;
                initOpenSettings(strScope, 5);
                break;
        }
    }

    /**
     * 공개여부 초기화 --> 최소 3자리, 최대 5자리 숫자(Type String)를 이용해 공개여부 초기화
     * @param strScope  String  공개여부를 표시하는 문자열
     * @param length    int     strScope의 길이
     */
    private void initOpenSettings(String strScope, int length) {
        this.isPhoneOpen    = (strScope.charAt(length - 3) == '1') ? true : false;
        this.isGenderOpen   = (strScope.charAt(length - 2) == '1') ? true : false;
        this.isAgeGroupOpen = (strScope.charAt(length - 1) == '1') ? true : false;
    }

    // getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getEnterDate() {
        return enterDate;
    }

    public void setEnterDate(String enterDate) {
        this.enterDate = enterDate;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public boolean isPhoneOpen() {
        return isPhoneOpen;
    }

    public void setPhoneOpen(boolean phoneOpen) {
        isPhoneOpen = phoneOpen;
    }

    public boolean isGenderOpen() {
        return isGenderOpen;
    }

    public void setGenderOpen(boolean genderOpen) {
        isGenderOpen = genderOpen;
    }

    public boolean isAgeGroupOpen() {
        return isAgeGroupOpen;
    }

    public void setAgeGroupOpen(boolean ageGroupOpen) {
        isAgeGroupOpen = ageGroupOpen;
    }

    public boolean isOpenGroup() {
        return openGroup;
    }

    public void setOpenGroup(boolean openGroup) {
        this.openGroup = openGroup;
    }

    public boolean isOpenAll() {
        return openAll;
    }

    public void setOpenAll(boolean openAll) {
        this.openAll = openAll;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Context getBaseContext() {
        return baseContext;
    }

    public void setBaseContext(Context baseContext) {
        this.baseContext = baseContext;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }
}
