package kr.ac.yjc.wdj.hikonnect.beans;

/**
 * 메인페이지 리사이클러 뷰에서 사용할 데이터 객체
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-14
 */
public class Group {
    private String  imgUrl;     // 그룹 이미지 url
    // TODO  그룹 상세 페이지 url 붙이기
//    private String  groupUrl;   // 그룹 상세페이지 url
    private String  groupName;  // 그룹 이름

    /**
     * 테스트용 생성자
     * @param groupName     그룹 명
     */
    public Group(String groupName) {
        this(null, groupName);
    }

    /**
     * 생성자
     * @param imgUrl    그룹 사진 url
     * @param groupName 그룹명
     */
    public Group(String imgUrl, String groupName) {
        this.imgUrl     = imgUrl;
        this.groupName  = groupName;
    }

    // getters and setters
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
