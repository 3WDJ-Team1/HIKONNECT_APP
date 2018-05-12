package kr.ac.yjc.wdj.hikonnect.activities.groups;

import java.util.Date;


public class NoticeListItem {
    String title;
    String content;
    String date;

    String getTitle() {
        this.title = title;
        return this.title;
    }

    String getContent() {
        this.content = content;
        return this.content;
    }

    String getDate() {
        this.date = date;
        return this.date;
    }

    NoticeListItem(String title, String content, String date) {
        this.title      = title;
        this.content    = content;
        this.date       = date;
    }
}
