package kr.ac.yjc.wdj.hikonnect.beans;

import kr.ac.yjc.wdj.hikonnect.beans.Bean;

/**
 * The bean class to store group notice
 * @author Sungeun Kang (kasueu0814@gmail.com)
 * @since  2018-04-06
 * @see     Bean
 */
public class GroupNotice extends Bean{
    private String  uuid;       // notice uuid
    private String  writer;     // writer of notice
    private String  title;      // title of notice
    private String  content;    // content of notice
    private int     hits;       // hits of notice
    private String  createdAt;  // created date of notice
    private String  updatedAt;  // updated date of notice

    // init member variables
    public GroupNotice(String   uuid,
                       String   writer,
                       String   title,
                       String   content,
                       int      hits,
                       String   createdAt,
                       String   updatedAt)
    {
        this.uuid       = uuid;
        this.writer     = writer;
        this.title      = title;
        this.content    = content;
        this.hits       = hits;
        this.createdAt  = createdAt;
        this.updatedAt  = updatedAt;
    }

    // getters and setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
