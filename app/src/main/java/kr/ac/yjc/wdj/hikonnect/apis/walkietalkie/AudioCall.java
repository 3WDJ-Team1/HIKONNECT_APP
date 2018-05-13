package kr.ac.yjc.wdj.hikonnect.apis.walkietalkie;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 무전 연결, 받기
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-06
 */
public class AudioCall {
    // 상수
    private final int       SENDER_PORT     = 11111;            // 전송 시 이용할 포트 번호
    private final int       LISTENER_PORT   = 50003;            // 수신 시 이용할 포트 번호
    private final int       RATE            = 8000;             // 진동수 헤르츠
    private final int       INTERVAL        = 20;
    private final int       SIZE            = 2;
    private final int       BUFFER_SIZE     = INTERVAL * INTERVAL * SIZE * 2; // 버퍼 크기

    // 로그캣 출력 태그
    private final String    SEND_TAG    = "SENDING";
    private final String    RECEIVE_TAG = "RECEIVING";

    // 변수
    private InetAddress     serverAddress;          // 서버 주소
    private boolean         isSending   = false;    // 데이터 전송을 지속하고 있는지
    private boolean         isReceiving = false;    // 데이터 수신을 지속하고 있는지

    /**
     * serverAdderess 초기화
     */
    public AudioCall(String serverIP) {
        try {
            serverAddress = InetAddress.getByName(serverIP);
        } catch (UnknownHostException uhe) {
            Log.e("AudioCall()", "initinating error!!\n" + uhe);
        }
    }

    /**
     * 데이터 전송 시작
     */
    public void sendStart() {
        // Log.d(SEND_TAG, "Sending start!!!!\n To: " + serverAddress.getHostName() + ":" + SENDER_PORT);
        isSending   = true;

        // 스레드 생성
        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket  socket = null;
                AudioRecord     record = null;
                try {
                    // 소켓 생성
                    socket = new DatagramSocket(SENDER_PORT);
                    // 레코더 객체 생성
                    record = new AudioRecord(
                            MediaRecorder.AudioSource.MIC,  // AudioSource
                            RATE,                           // sample rate
                            AudioFormat.CHANNEL_IN_MONO,    // channel
                            AudioFormat.ENCODING_PCM_16BIT, // format
                            AudioRecord.getMinBufferSize(
                                    RATE,
                                    AudioFormat.CHANNEL_IN_MONO,
                                    AudioFormat.ENCODING_PCM_16BIT
                            ) * 10
                    );

                    // 읽은 바이트
                    int readBytes = 0;
                    // 현재까지 송신한 바이트
                    int sentBytes = 0;
                    // 버퍼
                    byte[] buffer = new byte[BUFFER_SIZE];

                    // 녹음 시작
                    record.startRecording();
                    while(isSending) {
                        // 마이크에서 들어온 입력 받아 오기
                        readBytes = record.read(
                                buffer,     // 버퍼
                                0,          // 오프셋
                                BUFFER_SIZE // 버퍼 크기
                        );

                        // 데이터 패킷 생성
                        DatagramPacket packet = new DatagramPacket(
                                buffer,         // 버퍼(데이터)
                                readBytes,      // 입력 사이즈
                                serverAddress,  // 주소
                                SENDER_PORT     // 포트번호
                        );

                        // 전송
                        socket.send(packet);

                        sentBytes += readBytes;
                        Log.d(SEND_TAG, "total sent bytes: " + sentBytes);
                        Thread.sleep(INTERVAL, 0);
                    }
                } catch (IOException ie) {
                    if (ie instanceof  SocketException) {
                        Log.e(SEND_TAG, "while sending, SocketException was occured.\n" + ie.toString());
                        isSending = false;
                    } else {
                        Log.e(SEND_TAG, "while sending, IOException was occured. socket.send(packet);\n" + ie.toString());
                        isSending = false;
                    }
                } catch (InterruptedException ire) {
                    Log.e(SEND_TAG, "while sendiong, InterruptedException was occurd. Thread.sleep\n" + ire.toString());
                    isSending = false;
                } finally {
                    // 자원 반환
                    if (record != null) {
                        record.stop();
                        record.release();
                    }
                    if(socket != null) {
                        socket.disconnect();
                        socket.close();
                    }
                    isSending = false;
                }
            }
        });
        sendThread.start();
    }

    /**
     * 데이터 전송 종료
     */
    public void sendEnd() {
        Log.d(SEND_TAG, "Sending end!!!!");
        isSending = false;
    }

    /**
     * 데이터 수신 시작
     */
    public void receiveStart() {
        Log.d(RECEIVE_TAG, "Receiving start!!!!");
        isReceiving = true;

        // 스레드 생성
        Thread listenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // AudioTrack 객체 생성
                AudioTrack track = new AudioTrack(
                        AudioManager.STREAM_MUSIC,      // 무엇을 스트림하는가
                        RATE,                           // 헤르츠
                        AudioFormat.CHANNEL_OUT_MONO,   // 채널
                        AudioFormat.ENCODING_PCM_16BIT, // 디코딩
                        BUFFER_SIZE,                    // 버퍼
                        AudioTrack.MODE_STREAM          // 모드
                );

                DatagramSocket socket = null;
                // 실행
                track.play();
                try {
                    // 소켓 열기
                    socket = new DatagramSocket(LISTENER_PORT);
                    // 버퍼 지정
                    byte[] buffer = new byte[BUFFER_SIZE];

                    while(isReceiving) {
                        // 패킷 생성
                        DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
                        // 받아오기
                        socket.receive(packet);
                        Log.d(RECEIVE_TAG, "received packet length: " + packet.getLength());
                        // 길이가 0이 아니면
                        if (packet.getLength() != 0) {
                            // 오디오 트랙에 추가
                            track.write(packet.getData(), 0, BUFFER_SIZE);
                            // 음성 입력 방지
                            sendEnd();
                        }
                    }
                } catch (IOException ie) {
                    if (ie instanceof SocketException) {
                        Log.e(RECEIVE_TAG, "SocketException with DatagramSocket!!!\n" + ((SocketException) ie).toString());
                        if (socket != null) {
                            socket.disconnect();
                            socket.close();
                        }
                    } else
                        Log.e(RECEIVE_TAG, "IOException in receiving from socket!!!\n" + ie.toString());
                    isReceiving = false;
                } finally {
                    // 자원 해제
                    if (socket != null) {
                        socket.disconnect();
                        socket.close();
                    }
                    track.stop();
                    track.flush();
                    track.release();
                    isReceiving = false;
                }
            }
        });

        listenThread.start();
    }

    /**
     * 데이터 수신 종료
     */
    public void receiveEnd() {
        Log.d(RECEIVE_TAG, "Receiving end!!!!");
        isReceiving = false;
    }
}