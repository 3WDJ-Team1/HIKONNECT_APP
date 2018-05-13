package kr.ac.yjc.wdj.hikonnect.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.beans.AfterHikingMenu;

/**
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-12
 */
public class AfterHikingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int                         layout;     // 재사용될 레이아웃
    private ArrayList<AfterHikingMenu>  menuList;   // 값을 바꿀 리스트

    /**
     * 초기화
     * @param menuList  값을 넣을 ArrayList
     * @param layout    재사용될 레이아웃
     */
    public AfterHikingListAdapter(ArrayList<AfterHikingMenu> menuList, int layout) {
        this.menuList   = menuList;
        this.layout     = layout;
    }

    /**
     * 뷰홀더 생성
     * @param parent    현재 어댑터를 만드는 위치
     * @param viewType  뷰홀더에 넣을 레이아웃
     * @return          뷰홀더 객체
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new AfterHikingListViewHolder(view);
    }

    /**
     * 레이아웃과 연결된 뷰홀더를 이용해 데이터 값 조정
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((AfterHikingListViewHolder) holder).itemImage.setImageDrawable(menuList.get(position).getImageDrawable());
        ((AfterHikingListViewHolder) holder).itemTitle.setText(menuList.get(position).getMenuTitle());
        ((AfterHikingListViewHolder) holder).itemValue.setText(menuList.get(position).getMenuValue());
    }

    /**
     * 총 반복될 아이템 수
     * @return  입력 받은 메뉴 수
     */
    @Override
    public int getItemCount() {
        return menuList.size();
    }

    /**
     * 뷰홀더 --> 레이아웃과 데이터를 연결
     */
    private class AfterHikingListViewHolder extends RecyclerView.ViewHolder {
        private ImageView   itemImage;  // 메뉴 이미지
        private TextView    itemTitle;  // 메뉴 제목
        private TextView    itemValue;  // 메뉴 내용

        /**
         * 레이아웃 초기화
         * @param itemView  어댑터에서 입력된 재사용될 레이아웃
         */
        private AfterHikingListViewHolder(View itemView) {
            super(itemView);

            itemImage = (ImageView) itemView.findViewById(R.id.itemImage);
            itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            itemValue = (TextView) itemView.findViewById(R.id.itemValue);
        }
    }
}
