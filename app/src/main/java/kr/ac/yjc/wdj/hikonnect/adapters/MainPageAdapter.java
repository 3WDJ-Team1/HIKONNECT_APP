package kr.ac.yjc.wdj.hikonnect.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.beans.Group;

/**
 * 메인 페이지에 사용될 어댑터
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-14
 */
public class MainPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Group>    dataList;
    private int                 layout;

    /**
     * 초기화
     * @param dataList  데이터를 가지고 있는 ArrayList
     * @param layout    반복해서 쓰일 레이아웃
     */
    public MainPageAdapter(ArrayList<Group> dataList, int layout) {
        this.dataList   = dataList;
        this.layout     = layout;
    }

    /**
     * 받아온 레이아웃을 통해 뷰홀더 생성
     * @param viewGroup 부모 액티비티 객체
     * @param i
     * @return  뷰홀더 객체
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
        return new MainPageViewHolder(view);
    }

    /**
     * 데이터 값 넣기
     * @param viewHolder    뷰홀더 객체
     * @param i             몇 번 째 데이터인지
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        // TODO 이미지 들어가게 수정하기
//        ((MainPageViewHolder) viewHolder).groupImg...
        ((MainPageViewHolder) viewHolder).groupName.setText(dataList.get(i).getGroupName());
    }

    /**
     * 데이터 수
     * @return  받아온 ArrayList의 길이
     */
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * 메인페이지에 쓰일 뷰홀더
     */
    private static class MainPageViewHolder extends RecyclerView.ViewHolder {
        private ImageView   groupImg;   // 레이아웃에 있는 이미지 뷰
        private TextView    groupName;  // 레이아웃에 있는 텍스트 뷰

        /**
         * 뷰홀더 초기화
         * @param itemView  어댑터에서 입력된 레이아웃
         */
        public MainPageViewHolder(View itemView) {
            super(itemView);

            groupImg    = (ImageView) itemView.findViewById(R.id.groupImg);
            groupName   = (TextView) itemView.findViewById(R.id.groupTitle);
        }
    }
}
