package kr.ac.yjc.wdj.hikonnect.APIs.walkietalkie;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by 강성은 on 2018-05-09.
 */

public class RecordPlayer {
    private         String serverURL;
    private final   String FILE_ROUTE = "/"; // TODO 라우팅 경로 수정

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
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource("http://" + serverURL + FILE_ROUTE + fileName);
            player.prepare();
            player.start();
        } catch (IOException ie){
            ie.printStackTrace();
        }
    }
}
