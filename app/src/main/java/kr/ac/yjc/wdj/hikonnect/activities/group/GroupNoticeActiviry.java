package kr.ac.yjc.wdj.hikonnect.activities.group;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import kr.ac.yjc.wdj.hikonnect.R;

/**
 * The Activity used when make group notice
 * @author  Areum Lee (leear5799@gmail.com)
 * @since   2018-06-13
 */

public class GroupNoticeActiviry extends AppCompatActivity {
    // UI 변수
    private Button  findImgFileBtn,     // 이미지 파일 찾기 버튼
                    okBtn,              // 확인 버튼
                    cancelBtn;          // 취소 버튼

    // Session
    private SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_notice_app_bar);

        // 변수 초기화
        // UI 변수
        findImgFileBtn  = (Button) findViewById(R.id.fileFindBtn);
        okBtn           = (Button) findViewById(R.id.okBtn);
        cancelBtn       = (Button) findViewById(R.id.cancelBtn);

        // 각 버튼에 클릭 리스너 달기
        setBtnClickListner();
    }

    // 버튼별 클릭 리스너
    private void setBtnClickListner() {
        // 파일 찾기
        findImgFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });

        // 확인
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });

        // 취소
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이전 화면으로 돌아가기
                GroupNoticeActiviry.super.onBackPressed();
            }
        });
    }
}
