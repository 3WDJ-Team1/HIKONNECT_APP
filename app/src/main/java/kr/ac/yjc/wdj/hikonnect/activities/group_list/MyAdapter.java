package kr.ac.yjc.wdj.hikonnect.activities.group_list;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import kr.ac.yjc.wdj.hikonnect.Environment;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.UsersData;
import kr.ac.yjc.wdj.hikonnect.activities.groupDetail.TabsActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author  Jiyoon Lee, Sungeun Kang (kasueu0814@gmail.com)
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ListViewItem listItem = listItems.get(position);

        // 값 설정
        holder.textViewHead.setText(listItem.getHead());
        holder.textViewWriter.setText(listItem.getWriter());
        holder.textViewContent.setText(listItem.getContent());

        // 리스너 장착
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 부모 액티비티의 Context 객체 받아오기
                Context parent = listItems.get(position).getParent();

                Intent intent = new Intent(parent, TabsActivity.class);
                // 그룹 아이디 값 보내기
                intent.putExtra("groupId", listItem.getGroupId());

                // 액티비티 전환
                parent.startActivity(intent);
            }
        });

        // 참가 버튼 누르면 참가신청
        holder.joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 그룹 아이디 받아와서
                final String groupId = listItem.getGroupId();

                // http 수행
                new AsyncTask<Void, Integer, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            OkHttpClient client = new OkHttpClient();

                            String sendData = "{" +
                                    "\"userid\":\"" + UsersData.USER_ID + "\"," +
                                    "\"uuid\":\"" + groupId + "\"" +
                                    "}";

                            RequestBody body = RequestBody.create(Environment.JSON, sendData);

                            Request request = new Request.Builder()
                                    .url(Environment.LARAVEL_SOL_SERVER + "/member")
                                    .post(body)
                                    .build();

                            Response response = client.newCall(request).execute();
                            return response.body().string();
                        } catch (IOException ie) {
                            ie.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        holder.joinButton.setEnabled(false);
                        Toast.makeText(listItem.getParent(), "참가신청 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }.execute();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView        textViewHead;   // 제목
        private TextView        textViewWriter; // 작성자
        private TextView        textViewContent;// 내용
        private LinearLayout    linearLayout;   // 클릭하면 이동할 레이아웃
        private Button          joinButton;     // 참가 버튼

        /**
         * 레이아웃과 연결
         * @param itemView  재사용할 레이아웃
         */
        public ViewHolder(View itemView) {
            super(itemView);

            textViewHead    = (TextView) itemView.findViewById(R.id.textViewHead);
            textViewWriter  = (TextView) itemView.findViewById(R.id.tvWriter);
            textViewContent = (TextView) itemView.findViewById(R.id.tvContent);
            linearLayout    = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            joinButton      = (Button) itemView.findViewById(R.id.joinButton);
        }
    }


}