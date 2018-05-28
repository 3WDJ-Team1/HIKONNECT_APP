package kr.ac.yjc.wdj.hikonnect.activities.myPage;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.apis.http_request.HttpRequestConnection;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by LEE AREUM on 2018-05-03.
 */

public class UserRecordActivity extends AppCompatActivity{
    public  String                      id,
                                        mGrade,
                                        mHour,
                                        mMinute,
                                        mSeconds,
                                        mDistance,
                                        responseText;
    private ListView                    listView;
    private LineChart                   chart;
    private UserRecordListViewAdapter   adapter;
    private int                         count;
    private HttpRequestConnection       hrc             = new HttpRequestConnection();
    private String                      result;
    private Handler                     handler;
    private ContentValues               contentValues   = new ContentValues();
    private OkHttpClient                client;

    String[] data = new String[5];

    // 세션
    private SharedPreferences           preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_records);

        preferences = getSharedPreferences("loginData", MODE_PRIVATE);
        id          = preferences.getString("user_id", "");

        adapter = new UserRecordListViewAdapter();

        listView = (ListView) findViewById(R.id.listview1);
        listView.setAdapter(adapter);

        chart = (LineChart) findViewById(R.id.chart);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);

        client = new OkHttpClient();

        //new GetUserRecords(id).execute();

        // 등급, 총 등산 시간, 총 등산 거리 데이터 수신 및 세팅
        Thread thread = new Thread(new Runnable() {
            Response response = null;

            @Override
            public void run() {
                try {

                    Request reqest = new Request.Builder()
                            .url(Environments.LARAVEL_HIKONNECT_IP + "/api/user/" + id)
                            .build();

                    response = client.newCall(reqest).execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    responseText = response.body().string();

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

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_rating_svgrepo_com),
                "나의 등급", data[0]);

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_baseline_alarm_24px),
                "총 등산 시간", data[2] + "H" + data[3] + "M" + data[4] + "S") ;

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_mountain_svgrepo_com),
                "총 등산 거리", data[1] + "km") ;


        // 각 월별 등산 횟수 그래프
        // 각 월별 등산 횟수 데이터 가져오기
        // 현재 날짜 구하기
        long now = System.currentTimeMillis();

        Date date = new Date(now);

        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        final String thisYear = format.format(date);

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
                try {
                    RequestBody body = new FormBody.Builder()
                            .add("userid", id)
                            .add("year", thisYear)
                            .build();

                    Request request = new Request.Builder()
                            .url(Environments.LARAVEL_HIKONNECT_IP + "/api/graph")
                            .post(body)
                            .build();

                    result = client.newCall(request).execute().body().string();
                } catch (IOException ie) {
                    Log.e("record", "IOException was occured!!!!! \n" + ie);
                }
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
