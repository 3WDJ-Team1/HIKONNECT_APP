package kr.ac.yjc.wdj.hikonnect.beans;

import android.content.Context;

/**
 * store group user's information
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-04-06
 * @see     Bean
 */
public class GroupUserInfoBean extends Bean {
    private String  userId;         // user's id
    private String  nickname;       // user's nickname
    private String  profile;        // image path of user's profile pic
    private String  grade;
    private String  phone;          // user's phone number
    private String  gender;         // user's gender
    private String  ageGroup;       // user's age group
    private String  enterDate;

    private boolean isPhoneOpen;    // open user's phone number public
    private boolean isGenderOpen;   // open user's gender public
    private boolean isAgeGroupOpen; // open user's age group public
    private boolean openGroup;
    private boolean openAll;

    private Context baseContext;

    /**
     * 초기화
     * @param userId    String  유저 아이디
     * @param nickname  String  유저 닉네임
     * @param profile   String  유저 프로필 사진 경로
     * @param grade     String  유저 등급
     * @param phone     String  유저 핸드폰 번호
     * @param enterDate String  유저 들어온 날짜
     * @param gender    int     유저 성별
     * @param ageGroup  int     유저 연령대
     * @param scope     int     정보 공개 설정
     * @param baseContext
     */
    public GroupUserInfoBean(
                             String     userId,
                             String     nickname,
                             String     profile,
                             String     grade,
                             String     phone,
                             String     enterDate,
                             int        gender,
                             int        ageGroup,
                             int        scope,
                             Context    baseContext)
    {
        this.userId         = userId;
        this.nickname       = nickname;
        this.profile        = profile;
        this.grade          = grade;
        this.phone          = phone;
        this.enterDate      = enterDate;
        this.gender         = gender == 0 ? "남자" : "여자";
        this.ageGroup       = ageGroup + "대";
        this.baseContext    = baseContext;

        String strScope = scope + "";

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

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
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
}
