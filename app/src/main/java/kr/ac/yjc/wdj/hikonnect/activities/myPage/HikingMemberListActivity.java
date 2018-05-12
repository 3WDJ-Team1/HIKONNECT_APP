package kr.ac.yjc.wdj.hikonnect.activities.myPage;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.adapters.HikingMemberListAdapter;

/**
 * The Activity used while hiking (show members who is now hiking)
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-03-30
 * @see     kr.ac.yjc.wdj.hikonnect.adapters.HikingMemberListAdapter
 */

public class HikingMemberListActivity extends Activity {
    private SwipeMenuListView       memberListView;     // swipe menu list view
    private HikingMemberListAdapter adapter;            // listview adaptor
    private Button                  btnSelectAll;       // select all button
    private Button                  btnUnselectAll;     // unselect all button
    private EditText                searchData;         // text data for searching
    private Button                  btnSearchMember;    // button for starting to search

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hiking_mem_list);

        // find views
        memberListView  = (SwipeMenuListView)findViewById(R.id.memberListView);
        btnSelectAll    = (Button)findViewById(R.id.btnSelectAll);
        btnUnselectAll  = (Button)findViewById(R.id.btnUnselectAll);

        searchData      = (EditText)findViewById(R.id.searchMemberData);
        btnSearchMember = (Button)findViewById(R.id.btnSearchMember);

        // set menu of listview
        memberListView.setMenuCreator(getCreaterOfListView());

        // 리스트뷰에 연결할 어댑터 생성
        adapter         = new HikingMemberListAdapter();
        // 연결
        memberListView.setAdapter(adapter);

        // 메뉴 아이템(스와이프 시 나오는 버튼) 클릭 시 설정
        memberListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch(index) {
                    case 0:
                        Log.d("debug", "open");
                        break;
                    case 1:
                        Log.d("debug", "delete");
                        break;
                }
                return false;
            }
        });

        // 스와이프 방향 설정
        memberListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

/*        // 어댑터에 데이터 삽입
        adapter.addMembers(ContextCompat.getDrawable(this, R.drawable.common_google_signin_btn_icon_light_focused), "권범수", 0.15, 0.26, false);
        adapter.addMembers(ContextCompat.getDrawable(this, R.drawable.common_google_signin_btn_icon_light_focused), "새개끼", 0.15, 0.26, true);
        adapter.addMembers(ContextCompat.getDrawable(this, R.drawable.common_google_signin_btn_icon_light_focused), "발시놈", 0.15, 0.26, true);*/

        // 전체 선택 버튼 클릭 시
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 전체 선택
                adapter.checkAll(true);
                // 변경 사항 적용
                adapter.notifyDataSetChanged();
            }
        });

        // 전체 해제 버튼 클릭 시
        btnUnselectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 전체 해제
                adapter.checkAll(false);
                // 변경 사항 적용
                adapter.notifyDataSetChanged();
            }
        });

        // 검색 버튼 클릭 시
        btnSearchMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 어댑터에서 멤버 찾기
                adapter.searchMember(searchData.getText().toString());
                // 변경 사항 적용
                adapter.notifyDataSetChanged();
            }
        });
    }

    // 스와이프 메뉴 생성 시 쓰이는 객체를 받아오는 함수
    private SwipeMenuCreator getCreaterOfListView() {
        SwipeMenuCreator    creator  = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // 스와이프 메뉴 아이템 생성
                SwipeMenuItem   openItem = new SwipeMenuItem(getApplicationContext());
                // 설정
                openItem.setBackground(R.color.white);
                openItem.setTitle("Open");
                openItem.setWidth(200);
                openItem.setTitleSize(18);
                openItem.setTitleColor(R.color.black);
                // 삽입
                menu.addMenuItem(openItem);

                // 스와이프 메뉴 아이템 생성
                SwipeMenuItem   deleteItem = new SwipeMenuItem(getApplicationContext());
                // 설정
                deleteItem.setBackground(R.color.lightGrey);
                deleteItem.setWidth(200);
                deleteItem.setIcon(R.drawable.common_full_open_on_phone);
                // 삽입
                menu.addMenuItem(deleteItem);
            }
        };

        // 반환
        return creator;
    }
}
