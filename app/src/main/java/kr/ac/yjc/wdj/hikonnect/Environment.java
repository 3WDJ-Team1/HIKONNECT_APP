package kr.ac.yjc.wdj.hikonnect;

public abstract class Environment {
    private static final String LOCAL_HOST = "http://172.26.2.233";
    private static final String HIKONNECT = "http://hikonnect.ga";

    public static final String LARAVEL_LOCAL_IP = LOCAL_HOST + ":8000";
    public static final String NODE_LOCAL_IP = LOCAL_HOST+ ":3000";

    public static final String LARAVEL_HIKONNECT_IP = HIKONNECT;
    public static final String NODE_HIKONNECT_IP = HIKONNECT + ":3000";
}
