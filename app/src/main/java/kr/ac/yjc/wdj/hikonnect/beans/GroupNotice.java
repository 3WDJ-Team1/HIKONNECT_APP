package kr.ac.yjc.wdj.hikonnect.beans;

/**
 * The bean class to store group notice
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-04-06
 * @see     Bean
 */
public class GroupNotice extends Bean{

    private String  writer;     // writer of notice
    private String  title;      // title of notice
    private String  content;    // content of notice
    private String  picture;    // picture of notice
    private String  createdAt;  // created date of notice

    // init member variables
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
