package kr.ac.yjc.wdj.hikonnect.beans;

import android.graphics.drawable.Drawable;

/**
 * 등산 중인 멤버 데이터를 담아 둘 클래스
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-18
 * @see     kr.ac.yjc.wdj.hikonnect.adapters.HikingMemberListAdapter
 */
public class HikingMemberListBean {

    private int     memberNo;   // 멤버 번호
    private String  nickname;   // 닉네임
    private double  lat;        // 위도
    private double  lng;        // 경도

    /**
     * 초기화
     * @param memberNo  멤버 번호
     * @param nickname  닉네임
     * @param lat       latitude
     * @param lng       longitude
     */
    public HikingMemberListBean(int memberNo, String nickname, double lat, double lng) {
        this.memberNo   = memberNo;
        this.nickname   = nickname;
        this.lat        = lat;
        this.lng        = lng;
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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
