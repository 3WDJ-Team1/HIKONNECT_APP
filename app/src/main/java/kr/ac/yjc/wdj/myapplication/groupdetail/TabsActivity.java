package kr.ac.yjc.wdj.myapplication.groupdetail;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import devlight.io.library.ntb.NavigationTabBar;
import kr.ac.yjc.wdj.myapplication.R;

import java.util.ArrayList;

/**
 * Created by GIGAMOLE on 28.03.2016.
 */
public class TabsActivity extends Activity {

    private ViewPager                           viewPager;
    private NavigationTabBar                    navigationTabBar;
    private ArrayList<NavigationTabBar.Model>   models;
    private String[]                            colors;
    private CoordinatorLayout                   coordinatorLayout;
    private FloatingActionButton                btnEnterGroup;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_detail_home);

        viewPager           = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        navigationTabBar    = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        models              = new ArrayList<>();
        colors              = getResources().getStringArray(R.array.default_preview);
        coordinatorLayout   = (CoordinatorLayout) findViewById(R.id.parent);
        btnEnterGroup       = (FloatingActionButton) findViewById(R.id.btnEnterGroup);

        initUI();
    }

    private void initUI() {
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(final View view, final Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                View view = null;

                switch (position) {
                    // 그룹 공지사항 페이지
                    case 0:
                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_vp_list, null, false);

                        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(
                                        getBaseContext(), LinearLayoutManager.VERTICAL, false
                                )
                        );
                        recyclerView.setAdapter(new RecycleAdapter());

                        container.addView(view);
//                        setPages(view, R.layout.item_vp_list, container);
                        break;
                    // 그룹 일정 페이지
                    case 1:
                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.group_detail_members, null, false);

                        RecyclerView recyclerView2 = (RecyclerView) view.findViewById(R.id.rv2);
                        recyclerView2.setHasFixedSize(true);
                        recyclerView2.setLayoutManager(new LinearLayoutManager(
                                        getBaseContext(), LinearLayoutManager.VERTICAL, false
                                )
                        );
                        recyclerView2.setAdapter(new RecycleAdapter());

                        container.addView(view);
                        // RecycleAdapter 의 생성자 오버로딩 할 것
                        // 리사이클러 뷰를 갈아 끼우는 함수 (setPage) 를 이용할 방법 없는지 확인 할 것
                        break;
                    // 그룹 멤버 리스트
                    case 2:
                        break;
                }
                return view;
            }

            private void setPages(View view, int layout, ViewGroup container) {
                view = LayoutInflater.from(getBaseContext()).inflate(layout, null, false);

                final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(
                                getBaseContext(), LinearLayoutManager.VERTICAL, false
                        )
                );
                recyclerView.setAdapter(new RecycleAdapter());

                container.addView(view);
            }
        });

        addModels(R.drawable.ic_notifications_white_24px, colors[0], "Notice");
        addModels(R.drawable.ic_event_white_24px, colors[1], "Plan");
        addModels(R.drawable.ic_group_white_24px, colors[2], "Members");

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 2);

        //IMPORTANT: ENABLE SCROLL BEHAVIOUR IN COORDINATOR LAYOUT
        navigationTabBar.setBehaviorEnabled(true);

        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {
            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {

            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });
    }

    private void addModels(int drawable, String color, String title) {
        models.add(
                new NavigationTabBar.Model.Builder(
                        getDrawable(drawable),
                        Color.parseColor(color))
                        .title(title)
                        .build()
        );
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.txt.setText(String.format("Navigation Item #%d", position));
        }

        @Override
        public int getItemCount() {
            return 50;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView txt;

            public ViewHolder(final View itemView) {
                super(itemView);
                txt = (TextView) itemView.findViewById(R.id.txt_vp_item_list);
            }
        }
    }
}