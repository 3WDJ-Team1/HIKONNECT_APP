package kr.ac.yjc.wdj.hikonnect.activities.groups;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import kr.ac.yjc.wdj.hikonnect.R;


public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    Context context;
    List<NoticeListItem> items;

    public NoticeAdapter(Context context, List<NoticeListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public NoticeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item_cardview, null);
        return new NoticeAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NoticeAdapter.ViewHolder holder, int position) {
        final NoticeListItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());
        holder.date.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;
        TextView date;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            title       = (TextView) itemView.findViewById(R.id.notice_title);
            content     = (TextView) itemView.findViewById(R.id.notice_content);
            date        = (TextView) itemView.findViewById(R.id.notice_created_at);
            cardview    = (CardView) itemView.findViewById(R.id.nCardView);
        }
    }
}
