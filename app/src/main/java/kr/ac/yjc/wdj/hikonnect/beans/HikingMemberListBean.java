package kr.ac.yjc.wdj.hikonnect.beans;

import android.graphics.drawable.Drawable;

/**
 * 등산 중인 멤버 데이터를 담아 둘 클래스
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-03-30
 * @see     kr.ac.yjc.wdj.hikonnect.adapters.HikingMemberListAdapter
 */
public class HikingMemberListBean {
    private Drawable    profilePic;             // 리스트에 있는 프로필 사진 경로
    private String      userName;               // 리스트에 있는 유저 이름
    private Double      distToDestination;      // 리스트에 있는 목적지까지의 거리
    private Double      distFromMe;             // 리스트에 있는 나와의 거리
    private Boolean     isShown;                // 리스트에 있는 체크 박스의 값

    /**
     * @param profilePic            프로필 사진 경로
     * @param userName              유저 이름
     * @param distToDestination     목적지 까지의 거리
     * @param distFromMe            나와의 거리
     * @param isShown               체크 박스 불린 값
     */
    public HikingMemberListBean(Drawable profilePic, String userName, Double distToDestination, Double distFromMe, Boolean isShown) {
        // init member variables
        this.profilePic         = profilePic;
        this.userName           = userName;
        this.distToDestination  = distToDestination;
        this.distFromMe         = distFromMe;
        this.isShown            = isShown;
    }

    // getters and setters
    public Drawable getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Drawable profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getDistToDestination() {
        return distToDestination;
    }

    public void setDistToDestination(Double distToDestination) {
        this.distToDestination = distToDestination;
    }

    public Double getDistFromMe() {
        return distFromMe;
    }

    public void setDistFromMe(Double distFromMe) {
        this.distFromMe = distFromMe;
    }

    public Boolean getIsShown() {
        return isShown;
    }

    public void setIsShown(Boolean shown) {
        isShown = shown;
    }
}
