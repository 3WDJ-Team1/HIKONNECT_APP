package kr.ac.yjc.wdj.hikonnect.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.LoadingDialog;
import kr.ac.yjc.wdj.hikonnect.activities.groupDetail.TabsActivity;
import kr.ac.yjc.wdj.hikonnect.activities.schedule_detail.ScheduleDetailActivity;
import kr.ac.yjc.wdj.hikonnect.beans.Bean;
import kr.ac.yjc.wdj.hikonnect.beans.GroupNotice;
import kr.ac.yjc.wdj.hikonnect.beans.GroupSchedule;
import kr.ac.yjc.wdj.hikonnect.beans.GroupUserInfoBean;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * RecycleAdapter for group detail page
 * @author Sungeun Kang (kasueu0814@gmail.com), Areum Lee (leear5799@gmail.com)
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
        if (viewHolder instanceof ViewHolderNotice) {

            // 공지사항 데이터가 없을 경우
            if (((GroupNotice) dataList.get(i)).getTitle() == "데이터가 없습니다.") {
                ((ViewHolderNotice) viewHolder).noticeNo.setText("");
            } else {
                // 공지사항 데이터가 있을 경우
                ((ViewHolderNotice) viewHolder).noticeNo.setText(i + 1 + "");
            }
            ((ViewHolderNotice) viewHolder).noticeTitle.setText(((GroupNotice) dataList.get(i)).getTitle());
            ((ViewHolderNotice) viewHolder).noticeWriter.setText(((GroupNotice) dataList.get(i)).getWriter());
            ((ViewHolderNotice) viewHolder).noticeContent.setText(((GroupNotice) dataList.get(i)).getContent());

        } else if (viewHolder instanceof ViewHolderSchedule) {
            // 스케줄일 때
            final GroupSchedule schedule = (GroupSchedule) dataList.get(i);

            final Button btnShowScheduleDetail = ((ViewHolderSchedule) viewHolder).btnShowScheduleDetail;
            final Button btnJoin = ((ViewHolderSchedule) viewHolder).btnJoin;

            // 스케줄 데이터가 없을 경우
            if (schedule.getTitle() == "데이터가 없습니다.") {
                ((ViewHolderSchedule) viewHolder).scheduleNo.setText("");
                btnShowScheduleDetail.setVisibility(View.GONE);
                btnJoin.setVisibility(View.GONE);
            } else {
                // 스케줄 데이터가 있을 경우
                ((ViewHolderSchedule) viewHolder).scheduleNo.setText(i + 1 + "");
            }

            ((ViewHolderSchedule) viewHolder).scheduleTitle.setText(schedule.getTitle());
            ((ViewHolderSchedule) viewHolder).scheduleLeader.setText(schedule.getLeader());


            // 상세보기 버튼 클릭리스너
            btnShowScheduleDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                }
            });

            // 현재 해당 멤버가 일정에 참가 신청 상태인지에 대해 판별
            // 참가 신청 상태가 아니면 참가 취소 버튼을
            // 참가 신청 상태이면 참가 신청 버튼을 표시

            Intent intent = new Intent(schedule.getBaseContext(), ScheduleDetailActivity.class);

            // 일정 참가신청 버튼 클릭리스너
            btnJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AsyncTask<Void, Integer, String>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            loadingDialog.show();
                        }

                        @Override
                        protected String doInBackground(Void... params) {
                            userId = pref.getString("user_id", "");
                            schedule_no = String.valueOf(schedule.getNo());

                            Log.d("그룹 uuid", TabsActivity.groupId);

                            try {
                                OkHttpClient client = new OkHttpClient();

                                RequestBody body = new FormBody.Builder()
                                        .add("userid", userId)
                                        .add("uuid", TabsActivity.groupId)
                                        .add("schedule_no", schedule_no)
                                        .build();

                                Request request = new Request.Builder()
                                        .url(Environments.LARAVEL_HIKONNECT_IP + "/api/enter_schedule")
                                        .post(body)
                                        .build();

                                Response response = client.newCall(request).execute();
                                return response.body().string();
                            } catch (IOException ie) {
                                ie.printStackTrace();
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);

                            Log.d("result", s);

                            if (s == "false") {
                                Toast.makeText(
                                        context,
                                        "오류로 인해 참가신청에 실패했습니다.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                Toast.makeText(
                                        context,
                                        "일정에 참가신청 되었습니다.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                // 참가신청 버튼 삭제
                                btnJoin.setVisibility(View.GONE);
                            }
                            loadingDialog.dismiss();
                        }
                    }.execute();
                }
            });
        } else if (viewHolder instanceof ViewHolderMember) {
            final Button btnAcceptUser = ((ViewHolderMember) viewHolder).btnAcceptUser;         // 그룹 참가 수락 버튼
            final Button btnRejectUser = ((ViewHolderMember) viewHolder).btnRejectUser;         // 그룹 참가 거절 버튼
            final String memberId = ((GroupUserInfoBean) dataList.get(index)).getNickname();    // 참가 신청한 멤버의 ID                                                  // 참가 신청한 멤버의 Id

            // 그룹 멤버일 때
            // TODO Image 변경되게 바꿀 것
            //((ViewHolderMember) viewHolder).profilePic.setImageDrawable(null);
            ((ViewHolderMember) viewHolder).memberName.setText(((GroupUserInfoBean) dataList.get(index)).getNickname());
            ((ViewHolderMember) viewHolder).profilePic.setImageBitmap(((GroupUserInfoBean) dataList.get(index)).getProfilePic());

            // TODO 그룹 참가 수락 리스너
            btnAcceptUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    new AsyncTask<Void, Integer, String>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            loadingDialog.show();
                        }

                        @Override
                        protected String doInBackground(Void... params) {
                            try {
                                OkHttpClient client = new OkHttpClient();

                                RequestBody body = new FormBody.Builder()
                                        .add("userid", memberId)
                                        .add("uuid", TabsActivity.groupId)
                                        .build();

                                Request request = new Request.Builder()
                                        .url(Environments.LARAVEL_HIKONNECT_IP + "/api/member/true")
                                        .put(body)
                                        .build();

                                Response response = client.newCall(request).execute();
                                return response.body().string();
                            } catch (IOException ie) {
                                ie.printStackTrace();
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);

                            Log.d("result", s);

                            if (s == "false") {
                                Toast.makeText(
                                        context,
                                        "오류로 인해 참가 신청에 실패하였습니다.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                Toast.makeText(
                                        context,
                                        "참가 신청이 수락되었습니다.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                final int position = viewHolder.getAdapterPosition();
                                dataList.remove(position);
                                notifyItemRemoved(position);

                                // 멤버 리스트 갱신
                                notifyDataSetChanged();
                            }
                            loadingDialog.dismiss();
                        }
                    }.execute();
                }
            });

            // TODO 그룹 참가 거절 리스너
            btnRejectUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AsyncTask<Void, Integer, String>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            loadingDialog.show();
                        }

                        @Override
                        protected String doInBackground(Void... params) {
                            try {
                                OkHttpClient client = new OkHttpClient();

                                RequestBody body = new FormBody.Builder()
                                        .add("userid", memberId)
                                        .add("uuid", TabsActivity.groupId)
                                        .build();

                                Request request = new Request.Builder()
                                        .url(Environments.LARAVEL_HIKONNECT_IP + "/api/member/false")
                                        .put(body)
                                        .build();

                                Response response = client.newCall(request).execute();
                                return response.body().string();
                            } catch (IOException ie) {
                                ie.printStackTrace();
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);

                            Log.d("result", s);

                            if (s == "false") {
                                Toast.makeText(
                                        context,
                                        "오류로 인해 참가 신청 거절에 실패하였습니다.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                Toast.makeText(
                                        context,
                                        "참가 신청이 거절되었습니다.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                final int position = viewHolder.getAdapterPosition();
                                dataList.remove(position);
                                notifyItemRemoved(position);

                                notifyDataSetChanged();
                            }

                            loadingDialog.dismiss();
                        }
                    }.execute();
                }
            });

            if (!status.equals("\"guest\"")) {
                ((ViewHolderMember) viewHolder).cardWrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((ViewHolderMember) viewHolder).detailWrapper.getVisibility() == View.GONE) {
                            ((ViewHolderMember) viewHolder).detailWrapper.setVisibility(View.VISIBLE);
                            ArrayList<String> detailList = new ArrayList<>();
                            ArrayList<Drawable> iconList = new ArrayList<>();

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
}

    /**
     * ViewHolder for noitce list up
     */
    class ViewHolderNotice extends RecyclerView.ViewHolder {
        public TextView    noticeNo;       // 공지사항 번호
        public TextView    noticeTitle;    // 공지사항 제목
        public TextView    noticeWriter;   // 공지사항 작성자
        public TextView    noticeContent;  // 내용

        /**
         * 초기화
         * @param itemView 재사용될 레이아웃
         */
        public ViewHolderNotice (View itemView) {
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
    class ViewHolderMember extends RecyclerView.ViewHolder {

        public CircularImageView   profilePic;         // 멤버 프로필 사진
        public TextView            memberName;         // 멤버 이름
        public RelativeLayout      cardWrapper;        // 전체 카드의 wrapper
        public LinearLayout        detailWrapper;      // 상세 정보 나열 wrapper
        public RecyclerView        rvMemberInfoDetail; // 멤버 상세 정보 나열할 RecyclerView

        // buttons
        public Button               btnAcceptUser,      // 멤버 참가 수락 버튼
                                    btnRejectUser;      // 멤버 참가 거절 버튼

        /**
         * 초기화
         * @param itemView 재사용될 레이아웃
         */
        public ViewHolderMember (View itemView) {
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
     * ViewHolder for schedule list up
     */
    class ViewHolderSchedule extends RecyclerView.ViewHolder {

        public TextView         scheduleNo,             // 스케줄 번호
                                scheduleTitle,          // 스케줄 제목
                                scheduleLeader;         // 스케줄 주최자
        public Button           btnShowScheduleDetail,  // 스케줄 상세보기 버튼
                                btnJoin;                // 참가 버튼
        public CardView         wrapper;                // 카드 뷰 ; wrapper

        /**
         * 초기화
         * @param itemView  재사용될 레이아웃
         */
        public ViewHolderSchedule(View itemView) {
            super(itemView);

            scheduleNo              = (TextView)        itemView.findViewById(R.id.tvScheduleNo);
            scheduleTitle           = (TextView)        itemView.findViewById(R.id.tvScheduleTitle);
            scheduleLeader          = (TextView)        itemView.findViewById(R.id.tvScheduleLeader);
            btnShowScheduleDetail   = (Button)          itemView.findViewById(R.id.btnShowScheduleDetail);
            btnJoin                 = (Button)          itemView.findViewById(R.id.btnJoinSchedule);
            wrapper                 = (CardView)        itemView.findViewById(R.id.cvBackground);
        }
    }
