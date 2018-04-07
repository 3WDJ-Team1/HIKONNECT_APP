package kr.ac.yjc.wdj.myapplication.beans;

/**
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-04-06.
 */

public class GroupUserInfoBean extends Bean {
    private String  userUuid;       // user's uuid
    private String  nickname;       // user's nickname
    private String  imagePath;      // image path of user's profile pic
    private String  phone;          // user's phone number
    private String  gender;         // user's gender
/*     private String  ageGroup;       // user's age group
   private Boolean isPhoneOpen;    // open user's phone number public
    private Boolean isGenderOpen;   // open user's gender public
    private Boolean isAgeGroupOpen; // open user's age group public*/

    public GroupUserInfoBean(String     userUuid,
                             String     nickname,
                             String     imagePath,
                             String     phone,
                             int        gender/*,
                         String     ageGroup,
                         Boolean    isPhoneOpen,
                         Boolean    isGenderOpen,
                         Boolean    isAgeGroupOpen*/)
    {
        this.userUuid       = userUuid;
        this.nickname       = nickname;
        this.imagePath      = imagePath;
        this.phone          = phone;
        this.gender         = gender == 0 ? "남자" : "여자";
        /*this.ageGroup       = ageGroup;
        this.isPhoneOpen    = isPhoneOpen;
        this.isGenderOpen   = isGenderOpen;
        this.isAgeGroupOpen = isAgeGroupOpen;*/
    }

    // getters and setters
    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    /*public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public Boolean getPhoneOpen() {
        return isPhoneOpen;
    }

    public void setPhoneOpen(Boolean phoneOpen) {
        isPhoneOpen = phoneOpen;
    }

    public Boolean getGenderOpen() {
        return isGenderOpen;
    }

    public void setGenderOpen(Boolean genderOpen) {
        isGenderOpen = genderOpen;
    }

    public Boolean getAgeGroupOpen() {
        return isAgeGroupOpen;
    }

    public void setAgeGroupOpen(Boolean ageGroupOpen) {
        isAgeGroupOpen = ageGroupOpen;
    }*/
}
