package kr.ac.yjc.wdj.hikonnect.activities.groups;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import kr.ac.yjc.wdj.hikonnect.R;

/**
 * Created by LEE AREUM on 2018-05-04.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    Context             context;
    List<GroupListItem> items;

    public GroupAdapter(Context context, List<GroupListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, null);
        return new GroupAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GroupListItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.writer.setText(item.getWriter());
        holder.groupUuid.setText(item.getGroupUuid());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView writer;
        TextView groupUuid;
        CardView cardview;

        public ViewHolder(final View itemView) {
            super(itemView);
            title       = (TextView) itemView.findViewById(R.id.title);
            writer      = (TextView) itemView.findViewById(R.id.writer);
            groupUuid   = (TextView) itemView.findViewById(R.id.group_uuid);
            cardview    = (CardView) itemView.findViewById(R.id.cardView);

            groupUuid.setVisibility(View.GONE);

            cardview.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, GroupTestActivity.class);

                    intent.putExtra("title", title.getText().toString());
                    intent.putExtra("groupUuid", groupUuid.getText().toString());
                    context.startActivity(intent);
                }
            });
        }
    }
}
