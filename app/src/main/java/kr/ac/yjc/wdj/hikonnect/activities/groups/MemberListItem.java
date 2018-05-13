package kr.ac.yjc.wdj.hikonnect.activities.groups;


public class MemberListItem {
    int    image;
    String nickname;

    int getImage() {
        return this.image;
    }

    String getNickname() {
        return this.nickname;
    }

    MemberListItem(int image, String nickname) {
        this.image    = image;
        this.nickname = nickname;
    }
}
