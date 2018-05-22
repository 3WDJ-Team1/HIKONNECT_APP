package kr.ac.yjc.wdj.hikonnect.activities.myPage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
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
    private BarChart                    barChart;
    private UserRecordListViewAdapter   adapter;
    private String                      SERVER_ADDRESS = "http://172.26.2.88:8000";

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

        barChart = (BarChart) findViewById(R.id.user_record_graph);

        new GetUserRecords(id).execute();

        SetBarGraph();
    }

    public class GetUserRecords extends AsyncTask<Void, Void, String> {
        String userid;

        public GetUserRecords(String userid) {
            this.userid = userid;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String url = SERVER_ADDRESS + "/api/hiking_history/" + userid;

            try {
                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000 * 30);

                //return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        /*adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_grade),
                "나의 등급", "Account Box Black 36dp") ;

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_time),
                "총 등산 시간", "Account Circle Black 36dp") ;

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_mnt_distance),
                "총 등산 거리", "Assignment Ind Black 36dp") ;*/
    }

        public void SetBarGraph() {
            ArrayList<BarEntry> barEntries = new ArrayList<>();

            barEntries.add(new BarEntry(44f, 0));
            barEntries.add(new BarEntry(88f, 1));
            barEntries.add(new BarEntry(66f, 2));
            barEntries.add(new BarEntry(12f, 3));
            barEntries.add(new BarEntry(19f, 4));
            barEntries.add(new BarEntry(91f, 5));

            BarDataSet dataSet = new BarDataSet(barEntries, "Dates");

            ArrayList<String> dates = new ArrayList<>();

            dates.add("Jan");
            dates.add("Feb");
            dates.add("Mar");
            dates.add("Apr");
            dates.add("May");
            dates.add("June");

            //BarData barData = new BarData(dates, dataSet);
            //barChart.setData(barData);
    }
}
