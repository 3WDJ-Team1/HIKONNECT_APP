package kr.ac.yjc.wdj.hikonnect.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.beans.HikingMemberListBean;

/**
 * Created by 강성은 on 2018-03-30.
 */

public class HikingMemberListAdapter extends BaseAdapter {
    private ArrayList<HikingMemberListBean> hikingMemberList;

    public HikingMemberListAdapter() {
        hikingMemberList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return hikingMemberList.size();
    }

    @Override
    public Object getItem(int i) {
        return hikingMemberList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int position = i;
        final Context context = viewGroup.getContext();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.member_list_item, viewGroup, false);
        }

        ImageView   profilePic  = (ImageView) view.findViewById(R.id.memberProfilePic);
        TextView    userName    = (TextView) view.findViewById(R.id.userName);
        TextView    distToDest  = (TextView) view.findViewById(R.id.distToDestination);
        TextView    distFromMe  = (TextView) view.findViewById(R.id.distFromMe);
        CheckBox    isShown     = (CheckBox) view.findViewById(R.id.isShown);

        HikingMemberListBean memberBean = hikingMemberList.get(position);

        profilePic.setImageDrawable(memberBean.getProfilePic());
        userName.setText(memberBean.getUserName());
        distToDest.setText(memberBean.getDistToDestination() + "");
        distFromMe.setText(memberBean.getDistFromMe() + "");
        isShown.setChecked(memberBean.getIsShown());

        return view;
    }

    public void addMembers(Drawable profilePic, String userName, Double distToDest, Double distFromMe, Boolean isShown) {
        HikingMemberListBean member = new HikingMemberListBean(profilePic, userName, distToDest, distFromMe, isShown);
        hikingMemberList.add(member);
    }
}
