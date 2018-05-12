package kr.ac.yjc.wdj.hikonnect.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.beans.Record;
import kr.ac.yjc.wdj.hikonnect.apis.walkietalkie.RecordPlayer;

/**
 * 녹음 리스트 어댑터
 * @author  Sungeun Kang (kasueu0814@gmail.com)
 * @since   2018-05-10
 */
public class RecordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Record>   recordList; // 녹음 객체 리스트
    private int                 itemLayout; // 레이아웃

    /**
     * 생성자
     * @param recordList    녹음 객체 리스트
     * @param itemLayout    총 리스트 개수
     */
    public RecordListAdapter(ArrayList<Record> recordList, int itemLayout) {
        this.recordList = recordList;
        this.itemLayout = itemLayout;
    }

    /**
     * 레이아웃 받아와서 뷰 홀더 생성
     * @param parent    부모 뷰그룹
     * @param viewType  타입
     * @return          뷰홀더
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new RecordViewHolder(view);
    }

    /**
     * 뷰홀더에 내용 넣기
     * @param holder    만들어진 뷰 홀더
     * @param position  몇 번째 리스트 아이템인지
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((RecordViewHolder) holder).recordNo.setText(position + 1 + "");
        ((RecordViewHolder) holder).recordName.setText(recordList.get(position).getFileName());
        ((RecordViewHolder) holder).layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordPlayer player = new RecordPlayer("172.26.1.140");

                player.playRecords(recordList.get(position).getFileName());
            }
        });
    }

    /**
     * 리스트 아이템 총 갯수
     * @return  받아온 데이터 리스트의 크기
     */
    @Override
    public int getItemCount() {
        return recordList.size();
    }

    /**
     * 녹음 리스트
     */
    private static class RecordViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout    layout;         // 카드와 같은 역할
        private TextView        recordNo;       // 녹음 번호 텍스트뷰
        private TextView        recordName;     // 녹음 파일명 텍스트뷰

        /**
         * 초기화
         * @param itemView  리스트 아이템 레이아웃
         */
        private RecordViewHolder(View itemView) {
            super(itemView);

            layout      = (LinearLayout) itemView.findViewById(R.id.recordItems);
            recordNo    = (TextView) itemView.findViewById(R.id.recordNo);
            recordName  = (TextView) itemView.findViewById(R.id.recordFileName);
        }
    }
}
