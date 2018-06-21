package kr.ac.yjc.wdj.hikonnect.beans;

/**
 * 그룹 공지사항 객체
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-04-06
 */
public class GroupNotice extends Bean{

    private String  writer;     // 공지사항 작성자
    private String  title;      // 공지사항 제목
    private String  content;    // 공지사항 내용
    private String  picture;    // 공지사항 사진 (option)
    private String  createdAt;  // 생성 일자

    /**
     * 객체 초기화
     * @param writer    String    작성자
     * @param title     String    제목
     * @param content   String    내용
     * @param picture   String    사진 경로
     * @param createdAt String    생성일자
     */
    public GroupNotice(String   writer,
                       String   title,
                       String   content,
                       String   picture,
                       String   createdAt)
    {
        this.writer     = writer;
        this.title      = title;
        this.content    = content;
        this.picture    = picture;
        this.createdAt  = createdAt;
    }

    // getters and setters
    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
