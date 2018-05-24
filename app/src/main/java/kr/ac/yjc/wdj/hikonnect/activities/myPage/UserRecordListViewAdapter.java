package kr.ac.yjc.wdj.hikonnect.activities.myPage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.R;

/**
 * Created by LEE AREUM on 2018-05-17.
 */

public class UserRecordListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<UserRecordListViewItem> listViewItemList = new ArrayList<UserRecordListViewItem>();

    public UserRecordListViewAdapter() {}

    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.user_records_listview_item, null, true);
        }

        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.menuIcon);
        TextView titleTextVeiw = (TextView) convertView.findViewById(R.id.itemTitle);
        TextView descTextVeiw = (TextView) convertView.findViewById(R.id.itemContent);

        UserRecordListViewItem listViewItem = listViewItemList.get(position);

        iconImageView.setImageDrawable(listViewItem.getIcon());
        titleTextVeiw.setText(listViewItem.getTitle());
        descTextVeiw.setText(listViewItem.getDesc());

        return convertView;
    }

    class ViewHolder {
        ImageView   iconImageView;
        TextView    titleTxtView;
        TextView    contentTxtView;

        ViewHolder(View v) {
            iconImageView   = (ImageView) v.findViewById(R.id.menuIcon);
            titleTxtView    = (TextView) v.findViewById(R.id.itemTitle);
            contentTxtView  = (TextView) v.findViewById(R.id.itemContent);
        }

    }

    public void addItem(Drawable icon, String title, String desc) {
        UserRecordListViewItem item = new UserRecordListViewItem();

        item.setIcon(icon);
        item.setTitle(title);
        item.setDesc(desc);

        listViewItemList.add(item);
    }
}
