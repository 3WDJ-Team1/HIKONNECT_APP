package kr.ac.yjc.wdj.hikonnect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.adapters.RecordListAdapter;
import kr.ac.yjc.wdj.hikonnect.beans.Record;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 녹음 리스트 보여주는 액티비티
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-10
 */
public class RecordListActivity extends AppCompatActivity {
    private RecyclerView        rvRecordList;   // 녹음 리스트 뷰
    private RecordListAdapter   recordAdapter;  // 녹음 리스트 뷰 어댑터
    private ArrayList<Record>   recordDataList; // 녹음 객체 리스트

    private final String        SERVER_IP   = "http://172.26.2.88"; //TODO 요청 url 확인 되면 추가
    private final String        PORT        = "8000";
    private final String        LOG_TAG     = "RecList";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        rvRecordList = (RecyclerView) findViewById(R.id.rvRecordList);
        init();
    }

    /**
     * 초기화
     */
    private void init() {
        String testValue = "1";
        recordDataList = new ArrayList<>();
//        getRecordsFromServer(testValue);
        recordDataList.add(new Record());
        recordDataList.add(new Record());
        recordDataList.add(new Record());
        recordDataList.add(new Record());
        recordDataList.add(new Record());
        recordAdapter = new RecordListAdapter(recordDataList, R.layout.record_list_item);
        rvRecordList.setAdapter(recordAdapter);
        rvRecordList.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
    }

    /**
     * 서버에 요청해서 녹음 리스트 받아오기
     * @param scheduleId    참여하고 있는 일정 아이디
     */
    private void getRecordsFromServer(final String scheduleId) {
        // http request를 위해 스레드 생성
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 클라이언트 생성
                OkHttpClient client = new OkHttpClient();

                // TODO 리퀘스트 객체 생성
                Request request = new Request.Builder()
                        .url("http://" + SERVER_IP + ":" + PORT + "/api/radio/1" + scheduleId)
                        .build();

                try {
                // 실행
                Response response = client.newCall(request).execute();

                // TODO response에서 값 받아와 list에 추가
                    Log.d(LOG_TAG, response.body().string());
                } catch (IOException ie) {
                    Log.d(LOG_TAG, "IOException for http request!!!!\n" + ie.toString());
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException ire) {
            ire.printStackTrace();
        }
    }
}
