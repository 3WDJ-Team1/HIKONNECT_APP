package kr.ac.yjc.wdj.myapplication.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import kr.ac.yjc.wdj.myapplication.R;
import kr.ac.yjc.wdj.myapplication.beans.Bean;
import kr.ac.yjc.wdj.myapplication.beans.GroupNotice;
import kr.ac.yjc.wdj.myapplication.beans.GroupUserInfoBean;
import kr.ac.yjc.wdj.myapplication.models.Conf;


/**
 * RecycleAdapter for group detail page
 * @author Sungeun Kang (kasueu0814@gmail.com)
 * @since  2018-04-06
 */
public class RecycleAdapterForGDetail extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int             listLayout;
    private ArrayList<Bean> dataList;

    /**
     * @param listLayout    layout to be used
     */
    public RecycleAdapterForGDetail(int listLayout, ArrayList<Bean> dataList) {
        this.listLayout = listLayout;
        this.dataList   = dataList;
    }

    /**
     * Overrided
     * @return  ViewHolder viewHolder
     * set view which will be used in this class
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View                    view       = LayoutInflater.from(viewGroup.getContext()).inflate(listLayout, viewGroup, false);
        RecyclerView.ViewHolder viewHolder = null;

        // if list layout
        switch (listLayout) {
            // is notice_item
            case R.layout.notice_item:
                viewHolder = new ViewHolderNotice(view);
                break;
            // is group_detail_plan
            case R.layout.group_detail_plan:
                viewHolder = new ViewHolderPlan(view);
                break;
            // is member_list
            case R.layout.member_list:
                // use object of ViewHolderMember
                viewHolder = new ViewHolderMember(view);
                break;
        }

        return viewHolder;
    }

    /**
     * @param viewHolder    viewHolder that was initialized by onCreateViewHolder
     * @param i             index of list
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof ViewHolderNotice) {
            ((ViewHolderNotice) viewHolder).noticeNo.setText(i + 1 + "");
            ((ViewHolderNotice) viewHolder).noticeTitle.setText(((GroupNotice) dataList.get(i)).getTitle());
            ((ViewHolderNotice) viewHolder).noticeWriter.setText(((GroupNotice) dataList.get(i)).getWriter());
            ((ViewHolderNotice) viewHolder).noticeHits.setText(((GroupNotice) dataList.get(i)).getHits() + "");
        } else if (viewHolder instanceof ViewHolderPlan) {

        } else if (viewHolder instanceof ViewHolderMember) {
            final ViewHolderMember tmpViewHolder = (ViewHolderMember) viewHolder;
            final int index = i;
            new AsyncTask<Void, Integer, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    Bitmap bitmap = null;

                    try {
                        URL url = new URL(((GroupUserInfoBean) dataList.get(index)).getImagePath());

                        // 웹에서 이미지 가져와서
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();

                        InputStream is = connection.getInputStream();
                        // 비트맵으로 변환
                        bitmap = BitmapFactory.decodeStream(is);

                    } catch (MalformedURLException me) {
                        me.printStackTrace();
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                    return bitmap;
                }
                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    tmpViewHolder.profilePic.setImageBitmap(bitmap);
                }
            }.execute();
            ((ViewHolderMember) viewHolder).userName.setText(((GroupUserInfoBean) dataList.get(i)).getNickname());
        }
    }

    /**
     * @return int
     * return count of items
     */
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // ViewHolder for noitce list up
    static class ViewHolderNotice extends RecyclerView.ViewHolder {
        private TextView    noticeNo;
        private TextView    noticeTitle;
        private TextView    noticeWriter;
        private TextView    noticeHits;

        public ViewHolderNotice (View itemView) {
            super(itemView);
            this.noticeNo       = (TextView) itemView.findViewById(R.id.noticeNo);
            this.noticeTitle    = (TextView) itemView.findViewById(R.id.noticeTitle);
            this.noticeWriter   = (TextView) itemView.findViewById(R.id.noticeWriter);
            this.noticeHits     = (TextView) itemView.findViewById(R.id.noticeHits);
        }
    }

    // ViewHolder for showing plan
    public class ViewHolderPlan extends RecyclerView.ViewHolder {


        public ViewHolderPlan (View itemView) {
            super(itemView);

        }
    }

    // ViewHolder for member list up
    public class ViewHolderMember extends RecyclerView.ViewHolder {
        public ImageView    profilePic;     // user's profile picture
        public TextView     userName;       // user's name

        // init member variable
        public ViewHolderMember (View itemView) {
            super(itemView);
            profilePic  = (ImageView) itemView.findViewById(R.id.profilePic);
            userName    = (TextView) itemView.findViewById(R.id.memberName);
        }
    }
}
