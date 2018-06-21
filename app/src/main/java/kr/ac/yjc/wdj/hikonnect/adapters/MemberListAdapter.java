package kr.ac.yjc.wdj.hikonnect.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
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

import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.beans.GroupUserInfoBean;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

        if (dataList.get(i).getProfilePic() == null)
            initProfilePic(memberHolder.profilePic, dataList.get(i).getUserId(), dataList.get(i).getBaseContext(), i);
        else
            memberHolder.profilePic.setImageBitmap(dataList.get(i).getProfilePic());

        final int index = i;

        if (status != null && !status.equals("\"guest\"")) {
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
        }
    }

    /**
     * 유저 프로필 사진 등록
     * @param imageView 유저 사진 나올 이미지 뷰
     * @param id        아이디
     */
    private void initProfilePic(final CircularImageView imageView, final String id, final Context baseContext, final int position) {
        new AsyncTask<Void, Integer, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    HttpUrl httpUrl = HttpUrl
                            .parse(Environments.NODE_HIKONNECT_IP + "/images/UserProfile/" + id + ".jpg")
                            .newBuilder()
                            .build();

                    Request req = new Request.Builder().url(httpUrl).build();

                    Response res = new OkHttpClient().newCall(req).execute();

                    InputStream is = res.body().byteStream();

                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    return bitmap;
                } catch (IOException ie) {

                    ie.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                if (bitmap != null) {

                    Bitmap image = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
                    imageView.setImageBitmap(image);
                    dataList.get(position).setProfilePic(image);

                } else {

                    BitmapDrawable drawable    = (BitmapDrawable) ContextCompat.getDrawable(baseContext, R.drawable.circle_solid_profile_512px);
                    Bitmap          defaultImg  = drawable.getBitmap();

                    Bitmap          image2      = Bitmap.createScaledBitmap(defaultImg, 50, 50, true);

                    imageView.setImageBitmap(image2);
                    dataList.get(position).setProfilePic(image2);
                }
            }
        }.execute();
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MemberHolder extends RecyclerView.ViewHolder {
        private TextView            memberName;         // 멤버 이름
        private CircularImageView   profilePic;         // 프로필 사진
        private RelativeLayout      cardWrapper;        // 전체 카드의 wrapper
        private LinearLayout        detailWrapper;      // 상세 정보 나열 wrapper
        private RecyclerView        rvMemberInfoDetail; // 멤버 상세 정보 나열할 RecyclerView

        /**
         * 초기화
         * @param itemView 재사용될 레이아웃
         */
        private MemberHolder (View itemView) {
            super(itemView);
            memberName          = (TextView)            itemView.findViewById(R.id.memberName);
            profilePic          = (CircularImageView)   itemView.findViewById(R.id.profilePic);
            cardWrapper         = (RelativeLayout)      itemView.findViewById(R.id.cardWrapper);
            detailWrapper       = (LinearLayout)        itemView.findViewById(R.id.detailWrapper);
            rvMemberInfoDetail  = (RecyclerView)        itemView.findViewById(R.id.rvMemberInfoDetail);
        }
    }
}
