package kr.ac.yjc.wdj.hikonnect.adapters;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.beans.GroupUserInfoBean;

/**
 * 멤버 정보 상세보기의 어댑터
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-16
 */
public class MemberDetailAdapter extends RecyclerView.Adapter {
    private int                 layout;
    private ArrayList<String>   dataList;
    private ArrayList<Drawable> iconList;

    /**
     * 초기화
     * @param layout    재사용할 레이아웃
     * @param dataList  레이아웃을 채울 데이터 리스트
     * @param iconList  아이콘 리스트
     */
    public MemberDetailAdapter(int layout, ArrayList<String> dataList, ArrayList<Drawable> iconList) {
        this.layout     = layout;
        this.dataList   = dataList;
        this.iconList   = iconList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, null, false);
        return new ViewHolderMemberInfoDetail(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolderMemberInfoDetail holder = (ViewHolderMemberInfoDetail) viewHolder;

        holder.icon.setImageDrawable(iconList.get(i));
        holder.memberInfo.setText(dataList.get(i));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * 멤버 상세 정보 레이아웃과 데이터를 연결할 뷰홀더
     */
    private static class ViewHolderMemberInfoDetail extends RecyclerView.ViewHolder {

        private ImageView   icon;       // 사용할 아이콘
        private TextView    memberInfo; // 내용 (정보)

        public ViewHolderMemberInfoDetail(View itemView) {
            super(itemView);

            icon        = (ImageView)   itemView.findViewById(R.id.ivIcon);
            memberInfo  = (TextView)    itemView.findViewById(R.id.tvMemberInfoDetail);
        }
    }
}
