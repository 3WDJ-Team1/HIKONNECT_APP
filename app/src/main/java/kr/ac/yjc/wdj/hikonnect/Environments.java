package kr.ac.yjc.wdj.hikonnect;

import okhttp3.MediaType;

public abstract class Environments {
    // local
    private static final String LOCAL_HOST              = "http://172.26.1.46";    // localhost
    public  static final String LARAVEL_LOCAL_IP        = LOCAL_HOST + ":8000";     // laravel(local)
    public  static final String NODE_LOCAL_IP           = LOCAL_HOST+ ":3000";      // node (local)

    // hikonnect server
    private static final String HIKONNECT               = "http://hikonnect.ga";    // server host
    public  static final String LARAVEL_HIKONNECT_IP    = HIKONNECT;                // laravel
    public  static final String NODE_HIKONNECT_IP       = HIKONNECT + ":3000";      // node

    // walkietalkie server(local)
    public  static final String WALKIE_TALKIE_SERVER_IP = "172.26.2.114";
    public  static final String WALKIE_TALKIE_HTTP_PORT = "8800";

    // record
    public  static final String RECORD_FILE_ROUTE       = "/";

    // local laravel server
    private static final String SOL_SERVER              = "http://172.26.2.88";
    public  static final String LARAVEL_SOL_SERVER      = SOL_SERVER + ":8000/api";
    public  static final String NODE_SOL_SERVER         = SOL_SERVER + ":3000";

    public  static final MediaType  JSON                = MediaType.parse("application/json; charset=utf-8");
}
