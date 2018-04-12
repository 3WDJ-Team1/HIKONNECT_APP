package kr.ac.yjc.wdj.myapplication.beans;

/**
 * store group user's information
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-04-06
 * @see     kr.ac.yjc.wdj.myapplication.beans.Bean
 */
public class GroupUserInfoBean extends Bean {
    private String  userUuid;       // user's uuid
    private String  nickname;       // user's nickname
    private String  imagePath;      // image path of user's profile pic
    private String  phone;          // user's phone number
    private String  gender;         // user's gender
    private String  ageGroup;       // user's age group
    private String  openScope;      // open scope of user's information
    private Boolean isPhoneOpen;    // open user's phone number public
    private Boolean isGenderOpen;   // open user's gender public
    private Boolean isAgeGroupOpen; // open user's age group public

    public GroupUserInfoBean(/*String     userUuid,*/
                             String     nickname,
                             String     imagePath,
                             String     phone,
                             int        gender,
                             int        ageGroup,
                             int        scope)
    {
        /*this.userUuid       = userUuid;*/
        this.nickname       = nickname;
        this.imagePath      = imagePath;
        this.phone          = phone;
        this.gender         = gender == 0 ? "남자" : "여자";
        this.ageGroup       = ageGroup + "대";

        String strScope = scope + "";

        switch (strScope.length()) {
            case 3:
                this.openScope = "none";
                initOpenSettings(strScope, 3);
                break;
            case 4:
                this.openScope = "group";
                initOpenSettings(strScope, 4);
                break;
            case 5:
                this.openScope = "all";
                initOpenSettings(strScope, 5);
                break;
        }


    }

    private void initOpenSettings(String strScope, int length) {
        this.isPhoneOpen = (strScope.charAt(length - 3) == '1') ? true : false;
        this.isGenderOpen   = (strScope.charAt(length - 2) == '1') ? true : false;
        this.isAgeGroupOpen = (strScope.charAt(length - 1) == '1') ? true : false;
    }

    // getters and setters
   /* public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }*/

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
