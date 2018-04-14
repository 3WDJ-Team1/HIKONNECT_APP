package kr.ac.yjc.wdj.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import kr.ac.yjc.wdj.myapplication.WifiP2p.HttpConnection;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by LEE AREUM on 2018-03-30.
 */

public class RecordingActivity extends Activity implements View.OnTouchListener{
    private static final int    REQUEST_RECORD_AUDIO_PERMISSION     = 200;
    private static final String PATH                                = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String TAG                                 = "RecordingActivity";
    private static final String filename                            = PATH + "/recorded.mp4";
    private static final String serverIP                            = "http://hikonnect.ga";
    private static final int    serverPort                          = 9206;
    private static final MediaType JSON                             = MediaType.parse("application/json; charset=utf-8");
    private static final String URL                                 = "http://hikonnect.ga/api/group/request";
    private String json;
    private String body;
    private String device;

    private DataInputStream     recInputStream;
    private DataOutputStream    recOutputStream;
    private Socket              RecordSocket;
    private InetAddress         serverAddr;
    private byte[]              buf;
    private HttpConnection httpConn = HttpConnection.getInstance();

    Button          recordBtn;
    MediaRecorder   recorder;

    private boolean permissionToRecordAccepted          = false;
    private String  [] permissions                      = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        recordBtn = (Button) findViewById(R.id.recordBtn);
        recordBtn.setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Recording Button이 눌렸을 경우
                // 1. Server에 HttpRequest 요청
                //  1-1. 요청 시, 발신인 + 수신그룹 정보 함께 전송
                //    1-1-1. 해당 그룹의 멤버 중, 현재 무전 중인 멤버가 없다면 무전 허가 + ServerSocket 생성
                //    1-1-2. 해당 그룹의 멤버 중, 현재 무전 중인 멤버가 있다면 무전 거부 => 자동 종료(추후구현 예정)
                //  1-2. 무전 허가를 받은 받은 기기로 녹음 시작 + 녹음 시작 MSG 출력
                OkHttpClient client = new OkHttpClient();

                sendData();

                startRec();
                Toast.makeText(this, "start Record", Toast.LENGTH_LONG).show();
                break;
            case MotionEvent.ACTION_UP:
                // Recording Button에서 손이 떼었을 경우
                // 1-3. 녹음 종료
                // 1-4. 녹음 파일 압축 (추후구현 예정)
                // 1-5. ClientSocket 생성 및 녹음 파일 전송
                stopRec();
                Toast.makeText(this, "stop Record", Toast.LENGTH_LONG).show();

                recordSocket();
                break;
        }
        return true;
    }

    public void sendData(){
        // 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출 해줄 것!!
        new Thread() {
            public void run() {
                httpConn.requestWebServer("test","hiking", callback);
            }
        }.start();
    }

    private final Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d(TAG, "콜백오류:"+e.getMessage());
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            body = response.body().string();
            Log.d(TAG, "서버에서 응답한 Body:"+body);
        }
    };

    public void startRec() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(filename);

        try {
            recorder.prepare();
        } catch (Exception e){
            recorder = null;
            Log.d(TAG, "Recorder Connection Error");
            e.printStackTrace();
        }

        recorder.start();

        Toast.makeText(this, "start Record", Toast.LENGTH_LONG).show();
    }

    public void stopRec() {
        if (recorder == null)
            return;

        recorder.stop();
        recorder.release();
        recorder = null;

        Toast.makeText(this, "stop Record", Toast.LENGTH_LONG).show();
    }

    public void recordSocket() {
        try {
            serverAddr          =   InetAddress.getByName(serverIP);
            RecordSocket        =   new Socket(serverAddr, serverPort);
            try {
                recInputStream  =   new DataInputStream(new FileInputStream(new File(filename)));
                recOutputStream =   new DataOutputStream(RecordSocket.getOutputStream());

                buf             =   new byte[1024];
                while (recInputStream.read(buf) > 0) {
                    recOutputStream.write(buf);
                    recOutputStream.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                RecordSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}