package kr.ac.yjc.wdj.hikonnect.adapters;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.schedule_detail.ScheduleDetailActivity;
import kr.ac.yjc.wdj.hikonnect.beans.Bean;
import kr.ac.yjc.wdj.hikonnect.beans.GroupNotice;
import kr.ac.yjc.wdj.hikonnect.beans.GroupSchedule;
import kr.ac.yjc.wdj.hikonnect.beans.GroupUserInfoBean;


/**
 * RecycleAdapter for group detail page
 * @author Sungeun Kang (kasueu0814@gmail.com)
 * @since  2018-04-06
 */
public class RecycleAdapterForGDetail extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int             listLayout;
    private ArrayList<Bean> dataList;

    // for member
    private String          status;
    private ArrayList<Bean> dataListJoined  = new ArrayList<>();
    private ArrayList<Bean> tempList        = new ArrayList<>();
    private int             index;

    /**
     * @param listLayout    layout to be used
     */
    public RecycleAdapterForGDetail(int listLayout, ArrayList<Bean> dataList) {
        this.listLayout = listLayout;
        this.dataList   = dataList;
    }

    public RecycleAdapterForGDetail(int listLayout, ArrayList<Bean> dataList, ArrayList<Bean> dataListJoined,String status) {
        this.listLayout     = listLayout;
        this.dataList       = dataList;
        this.dataListJoined = dataListJoined;
        this.status         = status;
    }

    /**
     * Overrided
     * @return  ViewHolder viewHolder
     * set view which will be used in this class
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View                    view       = LayoutInflater.from(viewGroup.getContext()).inflate(listLayout, viewGroup, false);
        RecyclerView.ViewHolder viewHolder = null;

        // if list layout
        switch (listLayout) {
            // is notice_item
            case R.layout.notice_item:
                viewHolder = new ViewHolderNotice(view);
                break;
            // is group_detail_plan
            case R.layout.schedule_item_cardview_:
                viewHolder = new ViewHolderSchedule(view);
                break;
            // is member_list
            case R.layout.member_list:
                // use object of ViewHolderMember
                viewHolder = new ViewHolderMember(view);
                break;
        }

        return viewHolder;
    }

    /**
     * 뷰홀더를 통해 값 넣기
     * @param viewHolder    viewHolder that was initialized by onCreateViewHolder
     * @param i             index of list
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {
        // 공지사항일 때
        if(viewHolder instanceof ViewHolderNotice) {

            ((ViewHolderNotice) viewHolder).noticeNo.setText(i + 1 + "");
            ((ViewHolderNotice) viewHolder).noticeTitle.setText(((GroupNotice) dataList.get(i)).getTitle());
            ((ViewHolderNotice) viewHolder).noticeWriter.setText(((GroupNotice) dataList.get(i)).getWriter());
            ((ViewHolderNotice) viewHolder).noticeContent.setText(((GroupNotice) dataList.get(i)).getContent());

        } else if (viewHolder instanceof ViewHolderSchedule) {
            // 스케쥴일 때
            final GroupSchedule schedule = (GroupSchedule) dataList.get(i);

            ((ViewHolderSchedule) viewHolder).scheduleNo.setText(i + 1 + "");
            ((ViewHolderSchedule) viewHolder).scheduleTitle.setText(schedule.getTitle());
            ((ViewHolderSchedule) viewHolder).scheduleLeader.setText(schedule.getLeader());

            // 스케줄 레이아웃에 클릭리스너
            ((ViewHolderSchedule) viewHolder).wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(schedule.getBaseContext(), ScheduleDetailActivity.class);
                    // 데이터 이동
                    // 산 이름
                    intent.putExtra("mntId", schedule.getMntId());
                    // 경로
                    intent.putExtra("route", schedule.getRoute().toString());
                    // 일자
                    intent.putExtra("startDate", schedule.getStartDate());
                    // 내용
                    intent.putExtra("content", schedule.getContent());
                    // 그룹의 손님/참가자/오너
                    intent.putExtra("status", status);
                    // 스케줄 번호
                    intent.putExtra("scheduleNo", schedule.getNo());

                    // 상세 페이지로 이동
                    schedule.getBaseContext().startActivity(intent);
                }
            });

        } else if (viewHolder instanceof ViewHolderMember) {
            // 그룹 멤버일 때

            if (i < dataList.size() && status.equals("owner")) {
                tempList    = dataList;
                index       = i;
            } else if ( i >= dataList.size()) {
                tempList    = dataListJoined;
                index       = i - dataList.size();
            }

            // TODO Image 변경되게 바꿀 것
//            ((ViewHolderMember) viewHolder).profilePic.setImageDrawable(null);
            if (tempList.size() > index)
                ((ViewHolderMember) viewHolder).memberName.setText(((GroupUserInfoBean) tempList.get(index)).getNickname());

            Log.d("templist", tempList.size() + "");

            if (tempList == dataListJoined) {
                ((ViewHolderMember) viewHolder).btnAcceptUser.setVisibility(View.GONE);
                ((ViewHolderMember) viewHolder).btnRejectUser.setVisibility(View.GONE);
            }
            // TODO 생각 좀 해보고...
            if (!status.equals("guest")) {
                ((ViewHolderMember) viewHolder).cardWrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ViewHolderMember) viewHolder).detailWrapper.setVisibility(View.VISIBLE);

                        ArrayList<String> detailList = new ArrayList<>();
                        ArrayList<Drawable> iconList = new ArrayList<>();

                        // dataList init
                        detailList.add(((GroupUserInfoBean) tempList.get(index)).getGrade());
                        detailList.add(((GroupUserInfoBean) tempList.get(index)).getGender());
                        detailList.add(((GroupUserInfoBean) tempList.get(index)).getAgeGroup());
                        detailList.add(((GroupUserInfoBean) tempList.get(index)).getPhone());

                        // iconList init
                        iconList.add(((GroupUserInfoBean) tempList.get(index)).getBaseContext()
                                .getResources().getDrawable(R.drawable.ic_rating_svgrepo_com));
                        iconList.add(((GroupUserInfoBean) tempList.get(index)).getBaseContext()
                                .getResources().getDrawable(R.drawable.ic_gender_svgrepo_com));
                        iconList.add(((GroupUserInfoBean) tempList.get(index)).getBaseContext()
                                .getResources().getDrawable(R.drawable.ic_group_svgrepo_com));
                        iconList.add(((GroupUserInfoBean) tempList.get(index)).getBaseContext()
                                .getResources().getDrawable(R.drawable.ic_telephone_call_svgrepo_com));

                        ((ViewHolderMember) viewHolder).rvMemberInfoDetail.setHasFixedSize(true);

                        ((ViewHolderMember) viewHolder).rvMemberInfoDetail.setAdapter(new MemberDetailAdapter(
                                R.layout.member_info_detail_item,
                                detailList,
                                iconList
                        ));
                        ((ViewHolderMember) viewHolder).rvMemberInfoDetail.setLayoutManager(new LinearLayoutManager(((GroupUserInfoBean) tempList.get(index)).getBaseContext(), LinearLayoutManager.VERTICAL, false));
                    }
                });
            }

        }
    }

    /**
     * @return int
     * return count of items
     */
    @Override
    public int getItemCount() {
        return dataList.size() + dataListJoined.size();
    }

    /**
     * ViewHolder for noitce list up
     */
    private static class ViewHolderNotice extends RecyclerView.ViewHolder {
        private TextView    noticeNo;       // 공지사항 번호
        private TextView    noticeTitle;    // 공지사항 제목
        private TextView    noticeWriter;   // 공지사항 작성자
        private TextView    noticeContent;  // 내용

        /**
         * 초기화
         * @param itemView 재사용될 레이아웃
         */
        private ViewHolderNotice (View itemView) {
            super(itemView);
            this.noticeNo       = (TextView) itemView.findViewById(R.id.noticeNo);
            this.noticeTitle    = (TextView) itemView.findViewById(R.id.noticeTitle);
            this.noticeWriter   = (TextView) itemView.findViewById(R.id.noticeWriter);
            this.noticeContent  = (TextView) itemView.findViewById(R.id.groupNoticeContent);
        }
    }

    /**
     * ViewHolder for member list up
     */
    private static class ViewHolderMember extends RecyclerView.ViewHolder {

        private ImageView       profilePic;         // 멤버 프로필 사진
        private TextView        memberName;         // 멤버 이름
        private RelativeLayout  cardWrapper;        // 전체 카드의 wrapper
        private LinearLayout    detailWrapper;      // 상세 정보 나열 wrapper
        private RecyclerView    rvMemberInfoDetail; // 멤버 상세 정보 나열할 RecyclerView

        // buttons
        private Button          btnAcceptUser,      // 멤버 참가 수락 버튼
                                btnRejectUser;      // 멤버 참가 거절 버튼

        /**
         * 초기화
         * @param itemView 재사용될 레이아웃
         */
        private ViewHolderMember (View itemView) {
            super(itemView);
            profilePic          = (ImageView)       itemView.findViewById(R.id.profilePic);
            memberName          = (TextView)        itemView.findViewById(R.id.memberName);
            cardWrapper         = (RelativeLayout)  itemView.findViewById(R.id.cardWrapper);
            detailWrapper       = (LinearLayout)    itemView.findViewById(R.id.detailWrapper);
            rvMemberInfoDetail  = (RecyclerView)    itemView.findViewById(R.id.rvMemberInfoDetail);
            btnAcceptUser       = (Button)          itemView.findViewById(R.id.btnAcceptUser);
            btnRejectUser       = (Button)          itemView.findViewById(R.id.btnRejectUser);
        }
    }

    /**
     * 스케줄 뷰홀더
     * @author  Sungeun Kang (kasueu0814@gmail.com)
     * @since   2018-05-15
     */
    private static class ViewHolderSchedule extends RecyclerView.ViewHolder {

        private TextView    scheduleNo,     // 스케줄 번호
                            scheduleTitle,  // 스케줄 제목
                            scheduleLeader; // 스케줄 주최자
        private Button      btnJoin;        // 참가 버튼
        private CardView    wrapper;        // 카드 뷰 ; wrapper

        /**
         * 초기화
         * @param itemView  재사용될 레이아웃
         */
        private ViewHolderSchedule(View itemView) {
            super(itemView);

            scheduleNo      = (TextView) itemView.findViewById(R.id.tvScheduleNo);
            scheduleTitle   = (TextView) itemView.findViewById(R.id.tvScheduleTitle);
            scheduleLeader  = (TextView) itemView.findViewById(R.id.tvScheduleLeader);
            btnJoin         = (Button)   itemView.findViewById(R.id.btnJoinSchedule);
            wrapper         = (CardView) itemView.findViewById(R.id.cvBackground);

        }
    }
}
