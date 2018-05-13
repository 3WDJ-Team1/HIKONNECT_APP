package kr.ac.yjc.wdj.hikonnect.activities.group_list;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.groupDetail.TabsActivity;

/**
 * @author  Jiyoon Lee
 * @since   2018-04-10
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ListViewItem> listItems;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<ListViewItem> listItems) {
        this.listItems = listItems;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.groups_list_item, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ListViewItem listItem = listItems.get(position);
        holder.textViewHead.setText(listItem.getHead());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context parent = listItems.get(position).getParent();
                parent.startActivity(new Intent(parent, TabsActivity.class));
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listItems.size();
    }



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView     textViewHead;
        public LinearLayout linearLayout;
        public Button       joinButton;
        public ViewHolder(View itemView) {
            super(itemView);
            textViewHead    = (TextView)itemView.findViewById(R.id.textViewHead);
            linearLayout    = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            joinButton      = (Button) itemView.findViewById(R.id.joinButton);
            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("df", "1111111111111111111111111111");
                }
            });
        }
    }


}