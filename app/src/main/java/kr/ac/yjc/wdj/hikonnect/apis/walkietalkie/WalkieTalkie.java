package kr.ac.yjc.wdj.hikonnect.apis.walkietalkie;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

import kr.ac.yjc.wdj.hikonnect.Environments;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 전체적인 무전 기능 수행
 * @author  Sungeun Kang (kauseu0814@gmail.com)
 * @since   2018-05-10
 */
public class WalkieTalkie {
    // 변수
    private AudioCall       audioCall;
    private RecordPlayer    player;
    private String          myIPAddress;    // 현재 디바이스의 IP 주소

    // 상수
    // TODO : 상수값 변경 필요
    public  final String  SERVER_IP   = Environments.WALKIE_TALKIE_SERVER_IP;    // 데이터 보낼 서버 아이피
    private final String  HTTP_PORT   = Environments.WALKIE_TALKIE_HTTP_PORT;    // 서버 http 포트 번호
    private final String  LOG_TAG     = "WalkTalk";

    /**
     * 초기화
     */
    public WalkieTalkie() {
        audioCall   = new AudioCall(SERVER_IP);
        player      = new RecordPlayer(SERVER_IP + ":" + HTTP_PORT);
        myIPAddress = "";

        sendHttpToServer();
    }

    /**
     * 수신 시작
     */
    public void receiveStart() {
        audioCall.receiveStart();
    }

    /**
     * 발신 시작
     */
    public void sendStart() {
        audioCall.sendStart();
    }

    /**
     * 발신 종료
     */
    public  void sendEnd() {
        audioCall.sendEnd();
    }

    /**
     * 서버에 IP 주소를 보낸다
     */
    private void sendHttpToServer() {
        Thread httpThread = new Thread(new Runnable() {

            @Override
            public void run() {

                while (true) {
                    // 기존 IP 어드레스 저장
                    String temp = myIPAddress;

                    try {
                        // 소켓 열어 해당 호스트 주소 확인
                        Socket socket = new Socket("www.google.com", 80);
                        myIPAddress = socket.getLocalAddress().getHostAddress();
                        socket.close();
                    } catch (IOException ie) {
                        ie.printStackTrace();
                        myIPAddress = null;
                    }

                    Log.d(LOG_TAG, "myIPAddress: " + myIPAddress);

                    // myIPAddress가 빈 값이 아니고, 그 이전의 값과도 다를 때에만 전송
                    if (myIPAddress != "" && myIPAddress != temp) {

                        try {
                            // 서버에 전송
                            OkHttpClient client = new OkHttpClient();

                            // 리퀘스트 객체 생성
                            Request request = new Request.Builder()
                                    .url("http://" + SERVER_IP + ":" + HTTP_PORT + "/walkietalkie.php?ipaddr=" + myIPAddress)
                                    .build();

                            // 실행
                            Response response = client.newCall(request).execute();

                            // test
                            try {
                                Log.d(LOG_TAG, "httpResponse: " + response.body().string());
                            } catch (NullPointerException ne) {
                                Log.e(LOG_TAG, "response body is null!!\n" + ne.toString());
                            }
                        } catch (IOException ie) {
                            Log.e(LOG_TAG, "IOException in sendHttpToServer!!\n" + ie);
                        }

                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ie) {
                            Log.e(LOG_TAG, "Thread can't sleep!!\n" + ie);
                        }
                    }
                }
            }
        });

        httpThread.start();
    }
}
