package kr.ac.yjc.wdj.hikonnect.activities.myPage;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 유저 등산 기록 보기 액티비티
 * @author  Areum Lee (leear5799@gmail.com),
 *          Sungeun Kang (kasueu0814@gmail.com)  세션, 앱바
 * @since   2018-05-03
 */
public class UserRecordActivity extends AppCompatActivity {
    // UI 변수
    private ImageButton btnGoBack;              // 뒤로가기 버튼

    // 데이터를 담기 위한 변수
    public  String      id,                     // 사용자 ID
                        year,                   // 올해 년도
                        result;                 // 월 별 등산횟수
    private ImageView   gradeImgView,           // 등산 등급 아이콘
                        timeImgView,            // 총 등산 시간 아이콘
                        distanceImgView;        // 총 등산 거리 아이콘
    private TextView    user_grade,             // 등산 등급
                        user_hiking_time,       // 총 등산 시간
                        user_hiking_distance;   // 총 등산 거리

    private static String LOG_TAG = "result";

    // 차트 변수
    private LineChart           chart;
    private ArrayList<Entry>    yValues = new ArrayList<>();
    private String[]            resultArray;

    // 세션
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_records_app_bar);

        preferences = getSharedPreferences("loginData", MODE_PRIVATE);
        id          = preferences.getString("user_id", "");

        gradeImgView            = (ImageView) findViewById(R.id.gradeImg);

        user_grade              = (TextView) findViewById(R.id.user_grade);
        user_hiking_time        = (TextView) findViewById(R.id.user_hiking_time);
        user_hiking_distance    = (TextView) findViewById(R.id.user_hiking_distance);

        chart = (LineChart) findViewById(R.id.myLineChart);
        chart.getDescription().setEnabled(false);               // description label 제거

        //gradeImgView.setColorFilter(R.color.blue_200);

        // 뒤로가기 버튼
        btnGoBack = (ImageButton) findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 사용자의 등급, 총 등산 시간, 총 등산 거리 데이터 수신 및 셋팅
        getUserHikingData();

        // 사용자의 각 월별 등산 횟수 수신 및 그래프 생성
        getUserHikingDetailData();
    }

    // 등급, 총 등산 시간, 총 등산 거리 데이터 수신
    private void getUserHikingData() {
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
                    // 데이터 수신
                    JSONObject json = new JSONObject(s);

                    // 나의 등급
                    JSONObject gradeJson = json.getJSONObject("0");
                    user_grade.setText(gradeJson.getString("grade"));

                    // 총 등산 거리
                    user_hiking_distance.setText(json.getString("total_distance") + "km");

                    // 총 등산 시간
                    JSONObject timeJson = json.getJSONObject("total_hiking_time");
                    user_hiking_time.setText(timeJson.getString("hour") + "시간" + timeJson.getString("minute") + "분"
                                            + timeJson.getString("second") + "초");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // 각 월별 등산 횟수 데이터 가져오기
    private void getUserHikingDetailData() {
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
                Log.i("result1", result);

                result = s.substring(1,24);
                Log.i("result2", result);

                // ','를 기준으로 문자열 분리
                makeGraph(result);
            }
        }.execute();
    }

    // (월별)등산 횟수 그래프 생성
    private void makeGraph(String rs) {

        resultArray = rs.split(",");

        for (int count = 0 ; count < resultArray.length ; count++) {
            yValues.add(new Entry(count, Integer.parseInt(resultArray[count])));
        }

        // X축 세부설정
        // 아래쪽으로 글이 오게 설정
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Y축 세부설정
        // 왼쪽에만 숫자가 보여지게 설정
        YAxis yAxis = chart.getAxisRight();
        yAxis.setDrawLabels(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);

        LineDataSet set1 = new LineDataSet(yValues, "등산횟수(소수점으로 표시될 경우, Y축 소수점 X 10)");

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        // X축 월 지정
        final String[] xValues = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xValues[(int) value % xValues.length];
            }
        });

        LineData data = new LineData(dataSets);

        chart.setData(data);
        chart.animateY(4000);
    }
}