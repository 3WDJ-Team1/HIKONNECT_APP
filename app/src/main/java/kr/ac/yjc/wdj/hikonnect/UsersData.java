package kr.ac.yjc.wdj.hikonnect;

/**
 * 유저 정보 저장하고 있을 테이블
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-14
 */
public class UsersData {
    // 유저 정보
    public static String    USER_ID;                // 유저 아이디
    public static String    USER_PASSWORD;
    public static String    USER_NAME;              // 유저 닉네임
    public static String    PHONE;                  // 유저 전화번호
    public static String    GENDER;                 // 유저 성별
    public static int       AGE_GROUP;              // 연령대
    public static int       OPEN_SCOPE;             // 공개 여부
    public static String    PROFILE_URL;            // 사진 경로

    // 그룹 아이디
    public static String NOW_HIKING_SCHEDULE;   // 현재 등산 중인 스케줄
}
