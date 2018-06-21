package kr.ac.yjc.wdj.hikonnect.beans;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.util.Comparator;

/**
 * 등산 중인 멤버 데이터를 담아 둘 클래스
 * @author  Sungeun Kang (kasueu0814@gmail.com) 기본 데이터 초기화
 * @author  Beomsu Kwon (rnjs9957@gmail.com)    Comparator 추가
 * @since   2018-05-18
 * @see     kr.ac.yjc.wdj.hikonnect.adapters.HikingMemberListAdapter
 */
public class HikingMemberListBean {

    public static Comparator descending = new Comparator<HikingMemberListBean>() {
        @Override
        public int compare(HikingMemberListBean o1, HikingMemberListBean o2) {
            return o1.rank - o2.rank;
        }
    };

    private int     memberNo;   // 멤버 번호
    private String  nickname;   // 닉네임
    private double  distance;   // 현재까지 등산 한 거리
    private int     rank;       // 산행 거리 순위
    private Bitmap  profileImg; // 유저 프로필 이미지
    private double  latitude;   // 유저 위도
    private double  longitude;  // 유저 경도

    /**
     * 초기화
     * @param memberNo  멤버 번호
     * @param nickname  닉네임
     * @param distance  산행 거리
     * @param rank      산행 거리 순위
     */
    public HikingMemberListBean(int memberNo, String nickname, double distance, int rank, @Nullable Bitmap profileImg, double latitude, double longitude) {
        this.memberNo   = memberNo;
        this.nickname   = nickname;
        this.distance   = distance;
        this.rank       = rank;
        this.profileImg = profileImg;
        this.latitude   = latitude;
        this.longitude  = longitude;
    }

    // getters and setters
    public int getMemberNo() {
        return memberNo;
    }

    public void setMemberNo(int memberNo) {
        this.memberNo = memberNo;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Bitmap getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(Bitmap profileImg) {
        this.profileImg = profileImg;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
