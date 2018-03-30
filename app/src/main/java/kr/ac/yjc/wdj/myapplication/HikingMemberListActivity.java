package kr.ac.yjc.wdj.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.ListView;

import kr.ac.yjc.wdj.myapplication.adapters.HikingMemberListAdapter;

/**
 * Created by 강성은 on 2018-03-30.
 */

public class HikingMemberListActivity extends Activity {
    private ListView                memberListView;
    private HikingMemberListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_member_list_now_hiking);

        memberListView  = (ListView)findViewById(R.id.hikingMemberList);
        adapter         = new HikingMemberListAdapter();

        memberListView.setAdapter(adapter);

        adapter.addMembers(ContextCompat.getDrawable(this, R.drawable.common_google_signin_btn_icon_light_focused), "KSE", 0.15, 0.26, false);
        adapter.addMembers(ContextCompat.getDrawable(this, R.drawable.common_google_signin_btn_icon_light_focused), "KSE", 0.15, 0.26, true);
        adapter.addMembers(ContextCompat.getDrawable(this, R.drawable.common_google_signin_btn_icon_light_focused), "KSE", 0.15, 0.26, true);
    }
}
