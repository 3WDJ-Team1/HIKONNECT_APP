package kr.ac.yjc.wdj.hikonnect.apis.walkietalkie;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

import kr.ac.yjc.wdj.hikonnect.Environments;

/**
 * 녹음 파일 받아오기
 * @author Sungeun Kang (kasueu0814@gmail.com)
 * @since 2018-05-08
 */
public class RecordPlayer {
    private         String serverURL;
    private final   String PORT         = "8800";
    private final   String FILE_ROUTE   = Environments.RECORD_FILE_ROUTE; // TODO 라우팅 경로 수정
    private         int     count;
    private         MediaPlayer player;
    /**
     * 초기화
     * @param url   서버 주소
     */
    public RecordPlayer(String url) {
        serverURL = url;
    }

    /**
     * 서버로부터 녹음 받아와 실행
     * @param fileName  받아올 녹음 파일명.확장자
     */
    public void playRecords(String fileName) {
        try {
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource("http://" + serverURL + ":" + PORT + FILE_ROUTE + fileName + ".wav");
            player.prepare();
            player.start();
        } catch (IOException ie){
            ie.printStackTrace();
        }
    }

    public int getDuration() {
        return player.getDuration();
    }
}
