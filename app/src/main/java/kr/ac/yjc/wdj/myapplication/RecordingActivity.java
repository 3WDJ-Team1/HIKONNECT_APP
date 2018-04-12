package kr.ac.yjc.wdj.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.Socket;


/**
 * Created by LEE AREUM on 2018-03-30.
 */

public class RecordingActivity extends Activity implements View.OnClickListener{
    private static final int    REQUEST_RECORD_AUDIO_PERMISSION     = 200;
    private static final String PATH                                = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String TAG                                 = "RecordingActivity";
    private static final String filename                            = PATH+"/recorded.mp4";
    private static final String serverIP                            = "http://hikonnect.ga";
    private static final int    serverPort                          = 9206;
    private DataInputStream     recInputStream;
    private DataOutputStream    recOutputStream;
    private Socket              RecordSocket;
    private InetAddress         serverAddr;
    private String              deviceAddress;
    private String              groupAddress;
    private byte[]              buf;

    Button          recordBtn;
    Button          recordStopBtn;
    Button          playBtn;
    Button          playStopBtn;
    MediaPlayer     player;
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

        recordBtn       = (Button)findViewById(R.id.recordBtn);
        recordBtn.setOnClickListener(this);

        recordStopBtn   = (Button)findViewById(R.id.recordStopBtn);
        recordStopBtn.setOnClickListener(this);

        playBtn         = (Button)findViewById(R.id.playBtn);
        playBtn.setOnClickListener(this);

        playStopBtn     = (Button)findViewById(R.id.playStopBtn);
        playStopBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recordBtn:
                startRec();
                break;
            case R.id.recordStopBtn:
                stopRec();
                break;
            case R.id.playBtn:
                playRec();
                break;
            case R.id.playStopBtn:
                playStopRec();
                break;
        }
    }

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
        recordSocket();
    }

    public void playRec() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }

        player = new MediaPlayer();

        try {
            player.setDataSource(filename);
            player.prepare();
        } catch (Exception e) {
            Log.d(TAG, "Player Connection Error");
            e.printStackTrace();
        }

        Toast.makeText(this, "play Record", Toast.LENGTH_LONG).show();
        player.start();
    }

    public void playStopRec() {
        if (player == null)
            return;

        Toast.makeText(this, "stop Record", Toast.LENGTH_LONG).show();
        player.stop();
        player.release();
        player = null;
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
