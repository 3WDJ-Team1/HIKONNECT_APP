package kr.ac.yjc.wdj.hikonnect.beans;

/**
 * 메인액티비티에 보여질 스케줄
 * @author  Beomsu Kwon (rnjs9957@gmail.com)
 */
public class MainActivitySchedule {
    private int     num;
    private String  title;
    private String  mntName;
    private String  leader;
    private String  startDate;

    public void setNum(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
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
}
