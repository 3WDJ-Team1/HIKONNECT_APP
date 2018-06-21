package kr.ac.yjc.wdj.hikonnect.beans;

/**
 * 메인액티비티에 보여질 스케줄
 * @author  Beomsu Kwon (rnjs9957@gmail.com)
 */
public class MainActivitySchedule {
    private String groupName;
    private String title;
    private String mntName;
    private String leader;
    private String startDate;
    private String scheduleNum;

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setMntName(String mntName) {
        this.mntName = mntName;
    }

    public String getMntName() {
        return mntName;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getLeader() {
        return leader;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setScheduleNum(String scheduleNum) {
        this.scheduleNum = scheduleNum;
    }

    public String getScheduleNum() {
        return scheduleNum;
    }
}
