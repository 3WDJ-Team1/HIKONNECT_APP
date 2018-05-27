package kr.ac.yjc.wdj.hikonnect.activities.groups;

/**
 * @file        kr.ac.yjc.wdj.hikonnect.activities.MainActivity.java
 * @author      Areum Lee (leear5799@gmail.com)
 * @since       2018-04-30
 * @brief       The Activity used when a user logs up
 */

public class GroupListItem {
    String title;
    String writer;
    String groupUuid;

    String getTitle() {
        return this.title;
    }

    String getWriter() {
        return this.writer;
    }

    String getGroupUuid() {
        return this.groupUuid;
    }

    GroupListItem(String title, String writer, String groupUuid) {
        this.title  = title;
        this.writer = writer;
        this.groupUuid = groupUuid;
    }
}
