package kr.ac.yjc.wdj.myapplication.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.yjc.wdj.myapplication.R;
import kr.ac.yjc.wdj.myapplication.beans.HikingMemberListBean;

/**
 * @file        kr.ac.yjc.wdj.myapplication.HikingMemberListAdaptor.java
 * @author      Sungeun Kang (kasueu0814@gmail.com)
 * @since       2018-03-30
 * @brief       등산 중인 멤버 리스트 안에 들어갈 어댑터
 * @see         kr.ac.yjc.wdj.myapplication.beans.HikingMemberListBean
 */

public class HikingMemberListAdapter extends BaseAdapter {
    // 각 멤버의 정보들을 모아 저장하는  ArrayList
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
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int       position    = i;
        final Context   context     = viewGroup.getContext();

        // view 가 없을 경우
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.member_list_item, viewGroup, false);
        }

        // view 연결
        ImageView   profilePic  = (ImageView) view.findViewById(R.id.memberProfilePic);
        TextView    userName    = (TextView) view.findViewById(R.id.userName);
        TextView    distToDest  = (TextView) view.findViewById(R.id.distToDestination);
        TextView    distFromMe  = (TextView) view.findViewById(R.id.distFromMe);
        CheckBox    isShown     = (CheckBox) view.findViewById(R.id.isShown);

        // 해당하는 순서의 memberBean 가져 오기
        HikingMemberListBean memberBean = hikingMemberList.get(position);

        // bean에 있는 정보와 뷰를 연결
        profilePic.setImageDrawable(memberBean.getProfilePic());
        userName.setText(memberBean.getUserName());
        distToDest.setText(memberBean.getDistToDestination() + "");
        distFromMe.setText(memberBean.getDistFromMe() + "");
        isShown.setChecked(memberBean.getIsShown());

        return view;
    }

    /**
     * @param profilePic    유저 프로필 사진 경로
     * @param userName      유저 이름
     * @param distToDest    목적지 까지의 거리
     * @param distFromMe    나로부터의 거리
     * @param isShown       체크 박스 값
     */
    public void addMembers(Drawable profilePic, String userName, Double distToDest, Double distFromMe, Boolean isShown) {
        // bean 객체 생성
        HikingMemberListBean member = new HikingMemberListBean(profilePic, userName, distToDest, distFromMe, isShown);
        // ArrayList에 삽입
        hikingMemberList.add(member);
    }

    /**
     * @param makeChecked   체크 값 true/false
     */
    public void checkAll(Boolean makeChecked) {
        for (HikingMemberListBean member : hikingMemberList) {
            member.setIsShown(makeChecked);
        }
    }

    /**
     * @param memberName    멤버 이름을 받아와 해당 멤버 검색
     */
    public void searchMember(String memberName) {
        ArrayList<HikingMemberListBean> tmpMemberList = new ArrayList<>();
        for (HikingMemberListBean member : hikingMemberList) {
            if (member.getUserName().contains(memberName))
                tmpMemberList.add(member);
        }
        hikingMemberList = tmpMemberList;
    }

    // 멤버 리스트 초기화
    public void initMemberList() {

    }
}
