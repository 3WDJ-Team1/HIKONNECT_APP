package kr.ac.yjc.wdj.hikonnect.beans;

import android.graphics.drawable.Drawable;

/**
 * Created by 강성은 on 2018-03-30.
 */

public class HikingMemberListBean {
    private Drawable    profilePic;
    private String      userName;
    private Double      distToDestination;
    private Double      distFromMe;
    private Boolean     isShown;

    public HikingMemberListBean(Drawable profilePic, String userName, Double distToDestination, Double distFromMe, Boolean isShown) {
        this.profilePic         = profilePic;
        this.userName           = userName;
        this.distToDestination  = distToDestination;
        this.distFromMe         = distFromMe;
        this.isShown            = isShown;
    }

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
