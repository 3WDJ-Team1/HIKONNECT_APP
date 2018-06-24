package kr.ac.yjc.wdj.hikonnect.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.LoadingDialog;
import kr.ac.yjc.wdj.hikonnect.activities.schedule_detail.ScheduleDetailActivity;
import kr.ac.yjc.wdj.hikonnect.beans.Bean;
import kr.ac.yjc.wdj.hikonnect.beans.GroupNotice;
import kr.ac.yjc.wdj.hikonnect.beans.GroupSchedule;
import kr.ac.yjc.wdj.hikonnect.beans.GroupUserInfoBean;


/**
 * 그룹 상세보기 페이지에 사용될 어댑터
 * @author Sungeun Kang (kasueu0814@gmail.com)
 * @since  2018-04-06
 */
public class RecycleAdapterForGDetail extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LoadingDialog loadingDialog;      // 로딩 화면

    private int             listLayout;
    private ArrayList<Bean> dataList;
    private Context         context;

    // for member
    private String          status;
    private int             index;

    // 참가신청
    private String          userId,
                            schedule_no;

    // Session
    private SharedPreferences pref;

    /**
     * @param listLayout    layout to be used
     */
    public RecycleAdapterForGDetail(int listLayout, ArrayList<Bean> dataList, Context context) {
        this.listLayout = listLayout;
        this.dataList   = dataList;
        this.context    = context;
        loadingDialog   = new LoadingDialog(context);
        pref            = context.getSharedPreferences("loginData", Context.MODE_PRIVATE);
    }

    public RecycleAdapterForGDetail(int listLayout, ArrayList<Bean> dataList, String status, Context context) {
        this.listLayout = listLayout;
        this.dataList   = dataList;
        this.status     = status;
        this.context    = context;
        loadingDialog   = new LoadingDialog(context);
        pref            = context.getSharedPreferences("loginData", Context.MODE_PRIVATE);
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

        // listLayout이
        switch (listLayout) {
            // notice_item 이라면
            case R.layout.notice_item:
                viewHolder = new ViewHolderNotice(view);
                break;
            // schedule_item_cardview_ 라면
            case R.layout.schedule_item_cardview_:
                viewHolder = new ViewHolderSchedule(view);
                break;
            // member_list 라면
            case R.layout.member_list:
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

            // 번호
            ((ViewHolderNotice) viewHolder).noticeNo.setText(i + 1 + "");
            // 제목
            ((ViewHolderNotice) viewHolder).noticeTitle.setText(((GroupNotice) dataList.get(i)).getTitle());
            // 작성자
            ((ViewHolderNotice) viewHolder).noticeWriter.setText(((GroupNotice) dataList.get(i)).getWriter());
            // 내용
            ((ViewHolderNotice) viewHolder).noticeContent.setText(((GroupNotice) dataList.get(i)).getContent());

        } else if (viewHolder instanceof ViewHolderSchedule) {
            // 스케쥴일 때
            final GroupSchedule schedule = (GroupSchedule) dataList.get(i);

            // 스케줄 번호
            ((ViewHolderSchedule) viewHolder).scheduleNo.setText(i + 1 + "");
            // 스케줄 제목
            ((ViewHolderSchedule) viewHolder).scheduleTitle.setText(schedule.getTitle());
            // 스케줄 주최자
            ((ViewHolderSchedule) viewHolder).scheduleLeader.setText(schedule.getLeader());

            // 스케줄 레이아웃에 클릭리스너
            ((ViewHolderSchedule) viewHolder).wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((ViewHolderSchedule) viewHolder).wrapper.setClickable(false);

                    Intent intent = new Intent(schedule.getBaseContext(), ScheduleDetailActivity.class);
                    // 데이터 이동
                    // 산 이름
                    intent.putExtra("mntId", schedule.getMntId());
                    // 일자
                    intent.putExtra("startDate", schedule.getStartDate());
                    // 내용
                    intent.putExtra("content", schedule.getContent());
                    // 그룹의 손님/참가자/오너
                    intent.putExtra("status", status);
                    // 스케줄 번호
                    intent.putExtra("scheduleNo", schedule.getNo());
                    // 스케줄 제목
                    intent.putExtra("scheduleTitle", schedule.getTitle());
                    // 스케줄 경로 FID 배열(String)
                    intent.putExtra("scheduleRoute", schedule.getRoute());

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    // 상세 페이지로 이동
                    schedule.getBaseContext().startActivity(intent);

                    ((ViewHolderSchedule) viewHolder).wrapper.setClickable(true);
                }
            });

        } else if (viewHolder instanceof ViewHolderMember) {
            // 그룹 멤버일 때
            ((ViewHolderMember) viewHolder).memberName.setText(((GroupUserInfoBean) dataList.get(index)).getNickname());
            ((ViewHolderMember) viewHolder).profilePic.setImageBitmap(((GroupUserInfoBean) dataList.get(index)).getProfilePic());

            // TODO 그룹 참가 수락 리스너

            // TODO 그룹 참가 거절 리스너

            if (!status.equals("\"guest\"")) {
                ((ViewHolderMember) viewHolder).cardWrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((ViewHolderMember) viewHolder).detailWrapper.getVisibility() == View.GONE) {
                            ((ViewHolderMember) viewHolder).detailWrapper.setVisibility(View.VISIBLE);
                            ArrayList<String>   detailList  = new ArrayList<>();
                            ArrayList<Drawable> iconList    = new ArrayList<>();

                            // dataList init
                            detailList.add(((GroupUserInfoBean) dataList.get(index)).getGrade());
                            detailList.add(((GroupUserInfoBean) dataList.get(index)).getGender());
                            detailList.add(((GroupUserInfoBean) dataList.get(index)).getAgeGroup());
                            detailList.add(((GroupUserInfoBean) dataList.get(index)).getPhone());

                            // iconList init
                            iconList.add(((GroupUserInfoBean) dataList.get(index)).getBaseContext()
                                    .getResources().getDrawable(R.drawable.ic_rating_svgrepo_com));
                            iconList.add(((GroupUserInfoBean) dataList.get(index)).getBaseContext()
                                    .getResources().getDrawable(R.drawable.ic_gender_svgrepo_com));
                            iconList.add(((GroupUserInfoBean) dataList.get(index)).getBaseContext()
                                    .getResources().getDrawable(R.drawable.ic_group_svgrepo_com));
                            iconList.add(((GroupUserInfoBean) dataList.get(index)).getBaseContext()
                                    .getResources().getDrawable(R.drawable.ic_telephone_call_svgrepo_com));

                            ((ViewHolderMember) viewHolder).rvMemberInfoDetail.setHasFixedSize(true);

                            ((ViewHolderMember) viewHolder).rvMemberInfoDetail.setAdapter(new MemberDetailAdapter(
                                    R.layout.member_info_detail_item,
                                    detailList,
                                    iconList
                            ));
                            ((ViewHolderMember) viewHolder).rvMemberInfoDetail.setLayoutManager(new LinearLayoutManager(((GroupUserInfoBean) dataList.get(index)).getBaseContext(), LinearLayoutManager.VERTICAL, false));
                        } else {
                            ((ViewHolderMember) viewHolder).detailWrapper.setVisibility(View.GONE);
                        }
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
        return dataList.size();
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

        private CircularImageView   profilePic;         // 멤버 프로필 사진
        private TextView            memberName;         // 멤버 이름
        private RelativeLayout      cardWrapper;        // 전체 카드의 wrapper
        private LinearLayout        detailWrapper;      // 상세 정보 나열 wrapper
        private RecyclerView        rvMemberInfoDetail; // 멤버 상세 정보 나열할 RecyclerView

        // buttons
        private Button              btnAcceptUser,      // 멤버 참가 수락 버튼
                                    btnRejectUser;      // 멤버 참가 거절 버튼

        /**
         * 초기화
         * @param itemView 재사용될 레이아웃
         */
        private ViewHolderMember (View itemView) {
            super(itemView);
            profilePic          = (CircularImageView)   itemView.findViewById(R.id.profilePic);
            memberName          = (TextView)            itemView.findViewById(R.id.memberName);
            cardWrapper         = (RelativeLayout)      itemView.findViewById(R.id.cardWrapper);
            detailWrapper       = (LinearLayout)        itemView.findViewById(R.id.detailWrapper);
            rvMemberInfoDetail  = (RecyclerView)        itemView.findViewById(R.id.rvMemberInfoDetail);
            btnAcceptUser       = (Button)              itemView.findViewById(R.id.btnAcceptUser);
            btnRejectUser       = (Button)              itemView.findViewById(R.id.btnRejectUser);
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
