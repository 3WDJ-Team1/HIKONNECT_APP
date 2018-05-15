package kr.ac.yjc.wdj.hikonnect.activities.group_list;

import android.content.Context;

/**
 * @author  Jiyoon Lee, Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-04-10
 */
public class ListViewItem {

    private String  groupId;    // group id
    private String  head;       // title
    private String  writer;     // writer
    private String  content;    // content
    private Context parent;     // parent context

    /**
     * 객체 초기화
     * @param groupId   DB 에서 받아온 그룹 아이디
     * @param head      DB 에서 받아온 제목
     * @param writer    DB 에서 받아온 글쓴이
     * @param content   DB 에서 받아온 내용
     * @param parent    액티비티 Context 객체
     */
    public ListViewItem(String groupId, String head, String writer, String content, Context parent){
        this.groupId    = groupId;
        this.head       = head;
        this.writer     = writer;
        this.content    = content;
        this.parent     = parent;
    }

    // getters and setters
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Context getParent() {
        return parent;
    }

    public void setParent(Context parent) {
        this.parent = parent;
    }
}