package kr.ac.yjc.wdj.hikonnect.activities.myPage;

import android.content.ContentValues;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.EntityUtilsHC4;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.groups.GroupActivity;
import kr.ac.yjc.wdj.hikonnect.activities.groups.GroupAdapter;
import kr.ac.yjc.wdj.hikonnect.activities.groups.GroupListItem;
import kr.ac.yjc.wdj.hikonnect.activities.groups.ScheduleAdapter;
import kr.ac.yjc.wdj.hikonnect.activities.groups.ScheduleListItem;
import kr.ac.yjc.wdj.hikonnect.activities.session.SessionManager;
import kr.ac.yjc.wdj.hikonnect.apis.http_request.HttpRequestConnection;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by LEE AREUM on 2018-05-03.
 */

public class UserRecordActivity extends AppCompatActivity{
    private SessionManager              session;
    public String                       id, mGrade, mHour, mMinute, mSeconds, mDistance, responseText;
    private ListView                    listView;
    private LineChart                   chart;
    private UserRecordListViewAdapter   adapter;
    private int                         count;
    private HttpRequestConnection hrc = new HttpRequestConnection();
    private String result;
    private Handler handler;
    private ContentValues contentValues = new ContentValues();
    private static String SERVER_IP = "http://hikonnect.ga";
    String[] data = new String[5];

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

        chart = (LineChart) findViewById(R.id.chart);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);

        //new GetUserRecords(id).execute();

        // 등급, 총 등산 시간, 총 등산 거리 데이터 수신 및 세팅
        Thread thread = new Thread(new Runnable() {
            HttpResponse response = null;

            @Override
            public void run() {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet reqest = new HttpGet();

                    reqest.setURI(new URI(SERVER_IP + "/api/user/" + id));
                    response = client.execute(reqest);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    responseText = EntityUtils.toString(response.getEntity());
                    JSONObject json = new JSONObject(responseText);

                    JSONObject gradeJson = json.getJSONObject("0");
                    mGrade = gradeJson.getString("grade");
                    data[0] = mGrade;

                    mDistance = json.getString("total_distance");
                    data[1] = mDistance;

                    JSONObject timeJson = json.getJSONObject("total_hiking_time");
                    mHour       = timeJson.getString("hour");
                    mMinute     = timeJson.getString("minute");
                    mSeconds    = timeJson.getString("second");
                    data[2] = mHour;
                    data[3] = mMinute;
                    data[4] = mSeconds;

                    /*adapter.addItem(new UserRecordListViewItem(gradeDrawable, "나의 등급", mGrade));
                    adapter.addItem(new UserRecordListViewItem(timeDrawable));*/
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        thread.start();

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_grade),
                "나의 등급", data[0]);

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_time),
                "총 등산 시간", data[2] + "H" + data[3] + "M" + data[4] + "S") ;

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_mnt_distance),
                "총 등산 거리", data[1] + "km") ;


        // 각 월별 등산 횟수 그래프
        // 각 월별 등산 횟수 데이터 가져오기
        contentValues.put("userid", id);
        contentValues.put("year", 2018);

        new Thread(new Runnable() {
            //HttpResponse response = null;

            /*@Override
            public void run() {
                result = hrc.request(SERVER_IP + "/api/schedule", contentValues);
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
                *//*try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost reqest = new HttpPost();

                    reqest.setURI(new URI(SERVER_IP + "/api/graph/" + id + "/" + 2018));
                    response = client.execute(reqest);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    responseText = EntityUtils.toString(response.getEntity());
                    Log.d("result", responseText);
                    //JSONObject json = new JSONObject(responseText);

                    *//**//*JSONArray jsonArray = new JSONArray(responseText);

                    Log.d("result", jsonArray.toString());*//**//*

                } catch (Exception e) {
                    e.printStackTrace();
                }*//*

            }*/
            @Override
            public void run() {
                result = hrc.request(SERVER_IP + "/api/graph", contentValues);
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        }).start();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    Log.d("result:", result);
                    // 결과 배열을 받아온 후 설정하는 파트
                    //yValues.add(new Entry("Jan", 1f * 'result에서 받아온 이달의 횟수'));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // X축 세부설정
        // X축 위치를 아래쪽으로 지정
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Y축 세부설정
        // Y축 왼쪽에만 보여지게 설정
        YAxis yAxis = chart.getAxisRight();
        yAxis.setDrawLabels(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);

        ArrayList<Entry> yValues = new ArrayList<>();

        yValues.add(new Entry(0, 60f));
        yValues.add(new Entry(1, 50f));
        yValues.add(new Entry(2, 70f));
        yValues.add(new Entry(3, 30f));
        yValues.add(new Entry(4, 50f));
        yValues.add(new Entry(5, 60f));
        yValues.add(new Entry(6, 65f));

        LineDataSet set1 = new LineDataSet(yValues, "등산횟수");
        set1.setFillAlpha(110);
        set1.setLineWidth(2f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        chart.setData(data);

    }


    /*public class GetUserRecords extends AsyncTask<Void, Void, String> {
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
    }*/
}
