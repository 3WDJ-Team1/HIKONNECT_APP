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

/**
 * Created by LEE AREUM on 2018-05-08.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    Context context;
    List<ScheduleListItem> items;

    public ScheduleAdapter(Context context, List<ScheduleListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item_cardview, null);
        return new ScheduleAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ScheduleAdapter.ViewHolder holder, int position) {
        final ScheduleListItem item = items.get(position);
        holder.title.setText(item.getGroup(item.title));
        holder.leader.setText(item.getLeader(item.leader));
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView leader;
        CardView cardview;
        Button scheduleViewBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            title       = (TextView) itemView.findViewById(R.id.title);
            leader      = (TextView) itemView.findViewById(R.id.leader);
            cardview    = (CardView) itemView.findViewById(R.id.cardView);
            scheduleViewBtn = (Button) itemView.findViewById(R.id.shedule_view_button);

            cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //title = item.title;

                    Intent intent = new Intent(context, SchedulViewActivitiy.class);

                /*if (intent != null) {
                    intent.putExtra("title", title);
                    context.startActivity(intent);
                }*/
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            scheduleViewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent  = new Intent(context, SchedulViewActivitiy.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }
}

