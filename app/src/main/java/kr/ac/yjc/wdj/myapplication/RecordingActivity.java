package kr.ac.yjc.wdj.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by LEE AREUM on 2018-03-30.
 */

public class RecordingActivity extends Activity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION    = 200;
    private static final String PATH                            = Environment.getExternalStorageDirectory().getAbsolutePath().substring(8);
    private static final String RECORDED_FILE                   = "/recorded.mp4";
    private static final String TAG                             = "RecordingActivity";

    Button recordBtn;
    Button recordStopBtn;
    Button playBtn;
    Button playStopBtn;
    MediaPlayer player;
    MediaRecorder recorder;

    int playbackPosition = 0;

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        recordBtn       = (Button)findViewById(R.id.recordBtn);
        recordStopBtn   = (Button)findViewById(R.id.recordStopBtn);
        playBtn         = (Button)findViewById(R.id.playBtn);
        playStopBtn     = (Button)findViewById(R.id.playStopBtn);

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recorder != null) {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                }

                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(PATH+RECORDED_FILE);

                try {
                    recorder.prepare();
                    recorder.start();
                    Toast.makeText(getApplicationContext(), "녹음을 시작합니다.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e(TAG, "Recording Error");
                }
            }
        });

        recordStopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (recorder == null) {
                    return;
                }

                recorder.stop();
                recorder.release();
                recorder = null;

                Toast.makeText(getApplicationContext(), "녹음이 중지되었습니다.", Toast.LENGTH_LONG).show();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    playAudio(RECORDED_FILE);

                    Toast.makeText(getApplicationContext(), "파일 재생이 시작되었습니다.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        playStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null) {
                    playbackPosition = player.getCurrentPosition();
                    player.pause();
                    Toast.makeText(getApplicationContext(), "파일 재생이 중지되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void playAudio(String url) throws Exception{
        killMediaPlayer();

        player = new MediaPlayer();
        player.setDataSource(url);
        player.prepare();
        player.start();
    }

    protected void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
    }

    private void killMediaPlayer() {
        if (player != null) {
            try {
                player.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void onPause() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }

        super.onPause();
    }
}
