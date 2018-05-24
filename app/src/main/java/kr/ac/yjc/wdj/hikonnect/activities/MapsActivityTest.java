package kr.ac.yjc.wdj.hikonnect.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kr.ac.yjc.wdj.hikonnect.R;

/**
 * UI 동작 확인
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-15
 */
public class MapsActivityTest extends FragmentActivity {
    // UI 변수
    private CardView        userDataBox;    // 자신의 현재 정보를 보여줄 CardView
    private TextView        tvUserSpeed,    // 현재 속도 TextView (값 -> km/h 기준)
                            tvDistance,     // 총 이동 거리 TextView (값 -> km 기준)
                            tvArriveWhen;   // 예상 도착 시간 TextView (값 -> 시간 기준)
//    private RelativeLayout  layout;         // 전체 레이아웃
    private LinearLayout    drawerLayout;   // 무전 버튼을 넣어둘 레이아웃
    private Button          btnSendRecord;  // 무전 시작 버튼

    // 데이터 변수
    private boolean isdataBoxVisible    = false;    // 현재 데이터 박스 상태
    private boolean isRecBtnVisible     = false;    // 현재 녹음 버튼 상태

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_temp);

        initUI();
    }

    /**
     * UI 초기화
     */
    private void initUI() {
        userDataBox     = (CardView)        findViewById(R.id.userDataBox);
        tvUserSpeed     = (TextView)        findViewById(R.id.userSpeed);
        tvDistance      = (TextView)        findViewById(R.id.distance);
        tvArriveWhen    = (TextView)        findViewById(R.id.arriveWhen);
//        layout          = (RelativeLayout)  findViewById(R.id.backGround);
        drawerLayout    = (LinearLayout)    findViewById(R.id.drawer);
        btnSendRecord   = (Button)          findViewById(R.id.sendRecordData);

        // 전체 레이아웃을 한 번 클릭하면 데이터 박스 가시화
       /* layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isdataBoxVisible) {
                    userDataBox.setVisibility(View.GONE);
                } else {
                    userDataBox.setVisibility(View.VISIBLE);
                }
                isdataBoxVisible = !isdataBoxVisible;
            }
        });*/
        // drawerLayout 을 클릭하면 무전 버튼 가시화
        drawerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecBtnVisible) {
                    btnSendRecord.setVisibility(View.GONE);
                } else {
                    btnSendRecord.setVisibility(View.VISIBLE);
                }
                isRecBtnVisible = !isRecBtnVisible;
            }
        });
    }
}
