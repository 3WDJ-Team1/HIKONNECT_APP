package kr.ac.yjc.wdj.hikonnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.groupDetail.TabsActivity;
import kr.ac.yjc.wdj.hikonnect.activities.group_list.GroupListItem;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author  Sungeun Kang (kasueu0814@gmail.com), Areum Lee (leear5799@gmail.com)
 * @since   2018-05-26
 */
public class JoinedGroupListAdapter extends RecyclerView.Adapter<JoinedGroupListAdapter.JoinedGroupHolder> {

    private ArrayList<GroupListItem>    listItems;  // 그룹 리스트
    private SharedPreferences           preferences;

    // Provide a suitable constructor (depends on the kind of dataset)
    public JoinedGroupListAdapter(ArrayList<GroupListItem> listItems, SharedPreferences prefreneces) {
        this.listItems      = listItems;
        this.preferences    = prefreneces;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public JoinedGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.joined_group_item_list, parent, false);
        return new JoinedGroupHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final JoinedGroupHolder holder, final int position) {
        final GroupListItem listItem = listItems.get(position);

        // 값 설정
        holder.groupTitle.setText(listItem.getHead());
        holder.groupWriter.setText(listItem.getWriter());
        holder.groupDescrption.setText(listItem.getContent());

        // 리스너 장착
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 부모 액티비티의 Context 객체 받아오기
                final Context parent = listItem.getParent();

                        Intent intent = new Intent(parent, TabsActivity.class);

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // 그룹 아이디 값 보내기
                        intent.putExtra("groupId", listItem.getGroupId());
                        // 그룹 이름 보내기
                        intent.putExtra("groupName", listItem.getHead());
                        // 참여 상태 보내기
                        intent.putExtra("status", "\"member\"");

                        // 액티비티 전환
                        parent.startActivity(intent);

            }
        });

        // 그룹 탈퇴
        holder.btnExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Integer, String>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        Toast.makeText(listItem.getParent(), "그룹 탈퇴중...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected String doInBackground(Void... params) {

                        try {

                            OkHttpClient client = new OkHttpClient();

                            RequestBody body = new FormBody.Builder()
                                    .add("userid", preferences.getString("user_id", ""))
                                    .add("uuid", listItem.getGroupId())
                                    .build();

                            Request request = new Request.Builder()
                                    .url(Environments.LARAVEL_HIKONNECT_IP + "/api/out_group")
                                    .post(body)
                                    .build();

                            Response response = client.newCall(request).execute();

                            return response.body().string();

                        } catch (IOException ie) {
                            Log.e("ExitGroup", "IOException was occurred while exiting group!!!!\n" + ie);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);

                        if (s == "false") {
                            Toast.makeText(listItem.getParent(), "그룹 탈퇴 실패했습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(listItem.getParent(), "그룹 탈퇴 완료하였습니다.", Toast.LENGTH_SHORT).show();
                        }
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


    /**
     * 참여하고 있는 그룹 데이터와 레이아웃을 연결할 뷰홀더
     */
    public class JoinedGroupHolder extends RecyclerView.ViewHolder {

        private TextView        groupTitle;         // 제목
        private TextView        groupWriter;        // 작성자
        private TextView        groupDescrption;    // 설명
        private RelativeLayout  relativeLayout;     // 클릭하면 이동할 레이아웃
        private Button          btnExitGroup;       // 탈퇴 버튼

        /**
         * 레이아웃과 연결
         * @param itemView  재사용할 레이아웃
         */
        private JoinedGroupHolder(View itemView) {
            super(itemView);

            groupTitle      = (TextView)        itemView.findViewById(R.id.groupTitle);
            groupWriter     = (TextView)        itemView.findViewById(R.id.groupWriter);
            groupDescrption = (TextView)        itemView.findViewById(R.id.groupDescription);
            relativeLayout  = (RelativeLayout)  itemView.findViewById(R.id.joinedGroupLayout);
            btnExitGroup    = (Button)     itemView.findViewById(R.id.btnExitGroup);

        }
    }

}