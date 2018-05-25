package kr.ac.yjc.wdj.hikonnect.beans;

import android.content.Context;

import org.json.JSONArray;

/**
 * 그룹 스케줄 정보 저장할 bean 객체
 * @author  Sungeun Kang (kasueu0814@gmail.com
 * @since   2018-05-15
 */
public class GroupSchedule extends Bean {

    private int         no;         // 스케줄 번호
    private String      title;      // 제목
    private String      content;    // 내용
    private String      leader;     // 주최자
    private double      mntId;      // 등산할 산 코드
    private String      startDate;  // 시작일
    private String      route;      // 경로
    private Context     baseContext;

    /**
     * 객체 초기화하며 만들기 위한 생성자
     * @param no            int         스케줄 번호
     * @param title         String      제목
     * @param content       String      내용
     * @param leader        String      주최자
     * @param mntId         double      산코드
     * @param startDate     String      산행 시작일
     * @param route         String      경로
     * @param baseContext   Context     현재 액티비티의 Context
     */
    public GroupSchedule(int no, String title, String content, String leader, double mntId, String startDate, String route, Context baseContext) {
        this.no         = no;
        this.title      = title;
        this.content    = content;
        this.leader     = leader;
        this.mntId      = mntId;
        this.startDate  = startDate;
        this.route      = route;
        this.baseContext= baseContext;
    }

    // getters and setters
    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
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

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public double getMntId() {
        return mntId;
    }

    public void setMntId(double mntId) {
        this.mntId = mntId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public Context getBaseContext() {
        return baseContext;
    }

    public void setBaseContext(Context baseContext) {
        this.baseContext = baseContext;
    }
}
