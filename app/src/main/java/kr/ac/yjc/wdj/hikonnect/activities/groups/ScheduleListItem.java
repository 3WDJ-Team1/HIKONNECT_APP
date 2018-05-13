package kr.ac.yjc.wdj.hikonnect.activities.groups;

/**
 * Created by LEE AREUM on 2018-05-08.
 */

public class ScheduleListItem {
    String title;
    String leader;

    String getGroup(String title) {
        this.title = title;
        return this.title;
    }

    String getLeader(String leader) {
        this.leader = leader;
        return this.leader;
    }

    ScheduleListItem(String title, String leader) {
        this.title  = title;
        this.leader = leader;
    }

}
