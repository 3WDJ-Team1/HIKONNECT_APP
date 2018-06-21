package kr.ac.yjc.wdj.hikonnect.activities.group_list;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.group.GroupActivity;
import kr.ac.yjc.wdj.hikonnect.activities.myPage.UserJoinedGroup;
import kr.ac.yjc.wdj.hikonnect.activities.myPage.UserProfileActivity;
import kr.ac.yjc.wdj.hikonnect.activities.myPage.UserRecordActivity;

/**
 * 그룹 메뉴 액티비티
 * @author  Areum Lee (leear5799@gmail.com)
 * @since   2018-06-10
 */

public class GroupMenuActivity extends AppCompatActivity {
    // 세션 유지
    private SharedPreferences   preferences;
    private Button              btnToMakeGroup, btnToGroupList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_menu_app_bar);

        preferences     = getSharedPreferences("loginData", MODE_PRIVATE);

        initUI();
    }

    private void initUI() {
        btnToMakeGroup      = (Button) findViewById(R.id.btnToMakeGroup);
        btnToGroupList      = (Button) findViewById(R.id.btnToGroupList);

        // 리스너 달기
        setListeners();
    }

    /**
     * 버튼들에 이동하는 리스너 달기
     */
    private void setListeners() {
        // 그룹 생성 페이지로 이동
        btnToMakeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), GroupActivity.class);
                startActivity(intent);
            }
        });

        // 모집 중인 그룹 리스트 페이지로 이동
        btnToGroupList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), groups_list_main.class);
                startActivity(intent);
            }
        });
    }
}
