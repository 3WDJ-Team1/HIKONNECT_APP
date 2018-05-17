package kr.ac.yjc.wdj.hikonnect.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.Othersinfo;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.beans.HikingMemberListBean;

/**
 * 등산 중인 멤버 리스트 안에 들어갈 어댑터
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-18
 * @see     kr.ac.yjc.wdj.hikonnect.beans.HikingMemberListBean
 * TODO    많은 수정
 */
public class HikingMemberListAdapter extends RecyclerView.Adapter<HikingMemberListAdapter.HikingMemberHolder> {

    private ArrayList<HikingMemberListBean> dataList;
    private int                             layout;
    private Activity                        parent;

    /**
     * 초기화
     * @param layout    재사용할 레이아웃
     * @param dataList  순회할 데이터
     */
    public HikingMemberListAdapter(int layout, ArrayList<HikingMemberListBean> dataList, Activity parent) {
        this.layout     = layout;
        this.dataList   = dataList;
        this.parent     = parent;
    }

    @Override
    public HikingMemberHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(parent).inflate(layout, viewGroup, false);
        return new HikingMemberHolder(view);
    }

    @Override
    public void onBindViewHolder(HikingMemberHolder viewHolder, int i) {
        final HikingMemberListBean bean = dataList.get(i);

        viewHolder.memberName.setText(dataList.get(i).getNickname());
        viewHolder.cardWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("user_number", bean.getMemberNo());
                intent.putExtra("user_lat", bean.getLat());
                intent.putExtra("user_lng", bean.getLng());

                parent.setResult(Activity.RESULT_OK, intent);
                parent.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * 클릭 시 데이터 전송
     */
    private void resultData(int member_num) {

    }

    class HikingMemberHolder extends RecyclerView.ViewHolder {
        private TextView        memberName;         // 멤버 이름
        private RelativeLayout  cardWrapper;

        public HikingMemberHolder(View itemView) {
            super(itemView);
            memberName  = (TextView)        itemView.findViewById(R.id.memberName);
            cardWrapper = (RelativeLayout)  itemView.findViewById(R.id.cardWrapper);
        }
    }
}
