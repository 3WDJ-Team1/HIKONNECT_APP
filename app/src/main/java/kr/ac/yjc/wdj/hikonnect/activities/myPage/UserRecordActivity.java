package kr.ac.yjc.wdj.hikonnect.activities.myPage;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.HashMap;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.session.SessionManager;

/**
 * Created by LEE AREUM on 2018-05-03.
 */

public class UserRecordActivity extends AppCompatActivity{
    private SessionManager              session;
    private String                      id;
    private ListView                    listView;
    private UserRecordListViewAdapter   adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_records);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> userId = session.getUserId();
        id = userId.get(SessionManager.KEY_ID);

        adapter = new UserRecordListViewAdapter();

        listView = (ListView) findViewById(R.id.listview1);
        listView.setAdapter(adapter);

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_grade),
                "나의 등급", "Account Box Black 36dp") ;

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_time),
                "총 등산 시간", "Account Circle Black 36dp") ;

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_mnt_distance),
                "총 등산 거리", "Assignment Ind Black 36dp") ;
    }
}
