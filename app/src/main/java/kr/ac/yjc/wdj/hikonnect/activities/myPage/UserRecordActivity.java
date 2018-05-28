package kr.ac.yjc.wdj.hikonnect.activities.myPage;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONObject;

import java.util.ArrayList;


import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 유저 등산 기록 보기 액티비티
 * @author  Areum Lee, Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-03
 */
public class UserRecordActivity extends AppCompatActivity {
    private ImageButton                 btnGoBack;
    public  String                      id, year, mGrade, mHour, mMinute, mSeconds, mDistance, responseText, result;
    private ListView                    listView;
    private LineChart                   chart;
    private UserRecordListViewAdapter   adapter;

    private static String LOG_TAG       = "result";

    ArrayList<Entry>    yValues         = new ArrayList<>();
    String[]            resultArray;

    // 세션
    private SharedPreferences           preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_records_app_bar);

        preferences = getSharedPreferences("loginData", MODE_PRIVATE);
        id          = preferences.getString("user_id", "");

        adapter = new UserRecordListViewAdapter();

        listView = (ListView) findViewById(R.id.listview1);

        chart = (LineChart) findViewById(R.id.chart);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);

        // 뒤로가기 버튼
        btnGoBack = (ImageButton) findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 등급, 총 등산 시간, 총 등산 거리 데이터 수신 및 세팅
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {

                try {
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url(Environments.LARAVEL_HIKONNECT_IP + "/api/user/" + id)
                            .build();

                    Response response = client.newCall(request).execute();

                    return response.body().string();

                } catch (Exception e) {

                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                Log.d(LOG_TAG, s);

                try {
                    JSONObject json = new JSONObject(s);

                    JSONObject gradeJson = json.getJSONObject("0");
                    mGrade = gradeJson.getString("grade");
                    adapter.addItem(ContextCompat.getDrawable(UserRecordActivity.this, R.drawable.ic_rating_svgrepo_com),
                            "나의 등급", mGrade);

                    mDistance = json.getString("total_distance");
                    adapter.addItem(ContextCompat.getDrawable(UserRecordActivity.this, R.drawable.ic_mountain_svgrepo_com),
                            "총 등산 거리", mDistance + "km");

                    JSONObject timeJson = json.getJSONObject("total_hiking_time");
                    mHour       = timeJson.getString("hour");
                    mMinute     = timeJson.getString("minute");
                    mSeconds    = timeJson.getString("second");
                    adapter.addItem(ContextCompat.getDrawable(UserRecordActivity.this, R.drawable.ic_baseline_alarm_24px),
                            "총 등산 시간", mHour + "시간" + mMinute + "분" + mSeconds + "초");

                    listView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();


        // 각 월별 등산 횟수 그래프
        // 각 월별 등산 횟수 데이터 가져오기
        year = "2018";
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {

                try {

                    OkHttpClient client = new OkHttpClient();

                    RequestBody body = new FormBody.Builder()
                            .add("userid",id)
                            .add("year",year)
                            .build();

                    Request request = new Request.Builder()
                            .url(Environments.LARAVEL_HIKONNECT_IP + "/api/graph")
                            .post(body)
                            .build();

                    Response response = client.newCall(request).execute();

                    return response.body().string();

                } catch (Exception e) {

                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                Log.d(LOG_TAG, s);

                // '[,]' 기호 제거
                result = s.substring(1,25);
                Log.i(LOG_TAG, result);

                // ','를 기준으로 문자열 분리
                makeGraph(result);
            }
        }.execute();
    }

    // 등산횟수 그래프 생성
    public void makeGraph(String rs) {

        resultArray = rs.split(",");

        for (int count = 0 ; count < resultArray.length ; count++) {
            switch (resultArray[count]) {
                case "0":
                    yValues.add(new Entry(count, 0f));
                    break;
                case "1":
                    yValues.add(new Entry(count, 1f));
                    break;
                case "2":
                    yValues.add(new Entry(count, 2f));
                    break;
                case "3":
                    yValues.add(new Entry(count, 3f));
                    break;
                case "4":
                    yValues.add(new Entry(count, 4f));
                    break;
                case "5":
                    yValues.add(new Entry(count, 5f));
                    break;
                case "6":
                    yValues.add(new Entry(count, 6f));
                    break;
                case "7":
                    yValues.add(new Entry(count, 7f));
                    break;
                case "8":
                    yValues.add(new Entry(count, 8f));
                    break;
                case "9":
                    yValues.add(new Entry(count, 9f));
                    break;
                case "10":
                    yValues.add(new Entry(count, 10f));
                    break;
            }
        }

        // X축 세부설정
        // 아래쪽으로 글이 오게 설정
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Y축 세부설정
        // Y축 왼쪽에만 보여지게 설정
        YAxis yAxis = chart.getAxisRight();
        yAxis.setDrawLabels(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);

        /*yValues.add(new Entry(0, 60f));
        yValues.add(new Entry(1, 50f));
        yValues.add(new Entry(2, 70f));
        yValues.add(new Entry(3, 30f));
        yValues.add(new Entry(4, 50f));
        yValues.add(new Entry(5, 60f));
        yValues.add(new Entry(6, 65f));*/

        LineDataSet set1 = new LineDataSet(yValues, "등산횟수");
        set1.setFillAlpha(110);
        set1.setLineWidth(2f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        // X축 월 지정
        final String[] xValues = {"1월", "2월", "3월", "4월", "5월", "6월",
                                    "7월", "8월", "9월", "10월", "11월", "12월"};

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xValues[(int) value % xValues.length];
            }
        });

        LineData data = new LineData(dataSets);

        chart.setData(data);
    }
}