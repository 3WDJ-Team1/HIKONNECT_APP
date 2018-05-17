package kr.ac.yjc.wdj.hikonnect.adapters;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.beans.GroupUserInfoBean;

/**
 * 멤버 리스트 나열할 어댑터
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-17
 */
public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.MemberHolder> {
    private int                             layout;
    private ArrayList<GroupUserInfoBean>    dataList;
    private String                          status;

    /**
     * 초기화
     * @param layout    재사용할 레이아웃
     * @param dataList  재사용될 데이터
     * @param status    그룹원인지, 손님인지
     */
    public MemberListAdapter(int layout, ArrayList<GroupUserInfoBean> dataList, String status) {
        this.layout     = layout;
        this.dataList   = dataList;
        this.status     = status;
    }

    @Override
    public MemberHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, null, false);
        return new MemberHolder(view);
    }

    @Override
    public void onBindViewHolder(final MemberHolder memberHolder, int i) {
        memberHolder.memberName.setText(dataList.get(i).getNickname());

        final int index = i;
        // TODO 생각 좀 해보고...
        Log.d("STATUS:", status + "");
//        if (!status.equals("guest")) {
            memberHolder.cardWrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (memberHolder.detailWrapper.getVisibility() == View.GONE) {
                        memberHolder.detailWrapper.setVisibility(View.VISIBLE);

                        ArrayList<String> detailList = new ArrayList<>();
                        ArrayList<Drawable> iconList = new ArrayList<>();

                        // dataList init
                        detailList.add(dataList.get(index).getGrade());
                        detailList.add(dataList.get(index).getGender());
                        detailList.add(dataList.get(index).getAgeGroup());
                        detailList.add(dataList.get(index).getPhone());

                        // iconList init
                        iconList.add(dataList.get(index).getBaseContext()
                                .getResources().getDrawable(R.drawable.ic_rating_svgrepo_com));
                        iconList.add(dataList.get(index).getBaseContext()
                                .getResources().getDrawable(R.drawable.ic_gender_svgrepo_com));
                        iconList.add(dataList.get(index).getBaseContext()
                                .getResources().getDrawable(R.drawable.ic_group_svgrepo_com));
                        iconList.add(dataList.get(index).getBaseContext()
                                .getResources().getDrawable(R.drawable.ic_telephone_call_svgrepo_com));

                        memberHolder.rvMemberInfoDetail.setHasFixedSize(true);

                        memberHolder.rvMemberInfoDetail.setAdapter(new MemberDetailAdapter(
                                R.layout.member_info_detail_item,
                                detailList,
                                iconList
                        ));
                        memberHolder.rvMemberInfoDetail.setLayoutManager(new LinearLayoutManager(dataList.get(index).getBaseContext(), LinearLayoutManager.VERTICAL, false));
                    } else {
                        memberHolder.detailWrapper.setVisibility(View.GONE);
                    }
                }
            });
//        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MemberHolder extends RecyclerView.ViewHolder {
        private TextView        memberName;         // 멤버 이름
        private RelativeLayout  cardWrapper;        // 전체 카드의 wrapper
        private LinearLayout    detailWrapper;      // 상세 정보 나열 wrapper
        private RecyclerView    rvMemberInfoDetail; // 멤버 상세 정보 나열할 RecyclerView

        /**
         * 초기화
         * @param itemView 재사용될 레이아웃
         */
        private MemberHolder (View itemView) {
            super(itemView);
            memberName          = (TextView)        itemView.findViewById(R.id.memberName);
            cardWrapper         = (RelativeLayout)  itemView.findViewById(R.id.cardWrapper);
            detailWrapper       = (LinearLayout)    itemView.findViewById(R.id.detailWrapper);
            rvMemberInfoDetail  = (RecyclerView)    itemView.findViewById(R.id.rvMemberInfoDetail);
        }
    }
}
