package kr.ac.yjc.wdj.hikonnect.activities.groups;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import kr.ac.yjc.wdj.hikonnect.R;


public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    Context context;
    List<MemberListItem> items;

    public MemberAdapter(Context context, List<MemberListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item_cardview, null);
        return new MemberAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MemberAdapter.ViewHolder holder, int position) {
        final MemberListItem item = items.get(position);
        holder.userImg.setBackground(context.getResources().getDrawable(item.getImage()));
        holder.nickname.setText(item.getNickname());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImg;
        TextView nickname;
        CardView cardview;

        public ViewHolder(final View itemView) {
            super(itemView);
            userImg  = (ImageView) itemView.findViewById(R.id.user_imageView);
            nickname = (TextView) itemView.findViewById(R.id.group_member_nickname);
            cardview = (CardView) itemView.findViewById(R.id.mCardView);

            /*cardview.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, GroupTestActivity.class);

                    intent.putExtra("title", title.getText().toString());
                    intent.putExtra("groupUuid", groupUuid.getText().toString());
                    context.startActivity(intent);
                }
            });*/
        }
    }
}
