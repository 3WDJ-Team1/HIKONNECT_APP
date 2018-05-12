package kr.ac.yjc.wdj.hikonnect.activities.groups;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import kr.ac.yjc.wdj.hikonnect.R;


public class GroupTestActivity extends AppCompatActivity {
    String groupName;
    String groupUuid;
    TextView txtView;
    Button noticeBtn;
    Button scheduleListBtn;
    Button memberListBtn;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_home);

        Intent intent = getIntent();
        groupName = intent.getStringExtra("title");
        groupUuid = intent.getStringExtra("groupUuid");

        txtView = (TextView) findViewById(R.id.group_name);
        txtView.setText(groupName);

        noticeBtn = (Button) findViewById(R.id.group_notice_button);
        scheduleListBtn = (Button) findViewById(R.id.group_schedule_button);
        memberListBtn = (Button) findViewById(R.id.group_member_button);

        noticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
                startActivity(intent);
            }
        });

        scheduleListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                intent.putExtra("groupUuid", groupUuid);
                startActivity(intent);
            }
        });

        memberListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MemberListActivity.class);
                startActivity(intent);
            }
        });
    }
}
