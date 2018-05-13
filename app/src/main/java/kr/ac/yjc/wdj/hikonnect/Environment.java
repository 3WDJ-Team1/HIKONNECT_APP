package kr.ac.yjc.wdj.hikonnect;

public abstract class Environment {
    // local
    private static final String LOCAL_HOST              = "http://172.26.2.233";    // localhost
    public  static final String LARAVEL_LOCAL_IP        = LOCAL_HOST + ":8000";     // laravel(local)
    public  static final String NODE_LOCAL_IP           = LOCAL_HOST+ ":3000";      // node (local)

    // hikonnect server
    private static final String HIKONNECT               = "http://hikonnect.ga";    // server host
    public  static final String LARAVEL_HIKONNECT_IP    = HIKONNECT;                // laravel
    public  static final String NODE_HIKONNECT_IP       = HIKONNECT + ":3000";      // node
}
