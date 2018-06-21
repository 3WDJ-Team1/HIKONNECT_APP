package kr.ac.yjc.wdj.hikonnect.beans;

import kr.ac.yjc.wdj.hikonnect.Environments;

/**
 * 녹음 파일 관련 정보 Db로 부터 받아와 저장할 객체 (현재 사용 x)
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-10
 */
public class Record {
    private int     no;         // 녹음 고유 번호
    private int     scheduleNo; // 녹음을 소유하고 있는 그룹(일정)
    private String  sender;     // 발신인
    private String  url;        // url
    private String  fileName;   // 파일명
    private String  createdAt;  // 만들어진 시간
    private String  updatedAt;  // 변경 시간

    /**
     * 테스트용
     */
    public Record() {
//        setUrl("http://" + Environments.WALKIE_TALKIE_SERVER_IP + ":"
//                + Environments.WALKIE_TALKIE_HTTP_PORT + Environments.RECORD_FILE_ROUTE + "test111.wav");
    }

    /**
     * db로 부터 값 받아온 후 초기화
     * @param no            녹음 번호
     * @param scheduleNo    소유 그룹
     * @param sender        발신인
     * @param url           파일 경로
     * @param createdAt     생성 시간
     * @param updatedAt     변경 시간
     */
    public Record(int no, int scheduleNo, String sender, String url, String createdAt, String updatedAt) {
        this.no         = no;
        this.scheduleNo = scheduleNo;
        this.sender     = sender;
        setUrl(url);
        this.createdAt  = createdAt;
        this.updatedAt  = updatedAt;
    }

    // getters and setters
    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getScheduleNo() {
        return scheduleNo;
    }

    public void setScheduleNo(int scheduleNo) {
        this.scheduleNo = scheduleNo;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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

    public String getFileName() {
        return fileName;
    }

    public String getUrl() {
        return url;
    }

    /**
     * url 을 설정하며 fileName도 함께 초기화
     * @param url 파일 요청 경로
     */
    public void setUrl(String url) {
        this.url            = url;
        String[] splitedUrl = url.split("/");
        this.fileName       = splitedUrl[splitedUrl.length - 1];
    }
}
