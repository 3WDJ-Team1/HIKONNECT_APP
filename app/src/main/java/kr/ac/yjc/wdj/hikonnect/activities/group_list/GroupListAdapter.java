package kr.ac.yjc.wdj.hikonnect.activities.group_list;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.groupDetail.TabsActivity;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author  Jiyoon Lee, Sungeun Kang (kasueu0814@gmail.com)
 *          , Areum Lee (leear5799@gamil.com)
 * @since   2018-04-10
 */
public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private List<GroupListItem> listItems;
    private String              status;

    private SharedPreferences   pref;

    // Provide a suitable constructor (depends on the kind of dataset)
    public GroupListAdapter(List<GroupListItem> listItems, SharedPreferences pref) {
        this.listItems  = listItems;
        this.pref       = pref;
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
        final GroupListItem listItem = listItems.get(position);

        // 값 설정
        holder.textViewHead.setText(listItem.getHead());
        holder.textViewWriter.setText(listItem.getWriter());
        holder.textViewContent.setText(listItem.getContent());

        // 리스너 장착
        holder.moveToGroupDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 부모 액티비티의 Context 객체 받아오기
                final Context parent = listItem.getParent();

                // 그룹 참가 여부 ( 불참, 참가자, 주최자 ) 확인
                new AsyncTask<String, Integer, String>() {

                    @Override
                    protected String doInBackground(String... params) {
                        // http request (post)
                        try {
                            OkHttpClient client = new OkHttpClient();

                            RequestBody body = new FormBody.Builder()
                                    .add("userid", params[1])
                                    .add("uuid", params[0])
                                    .build();

                            Request request = new Request.Builder()
                                    .url(Environments.LARAVEL_HIKONNECT_IP + "/api/checkMember")
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
                        Log.d("status", s);

                        // status 초기화
                        status = s;

                        Intent intent = new Intent(parent, TabsActivity.class);

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // 그룹 아이디 값 보내기
                        intent.putExtra("groupId", listItem.getGroupId());
                        // 그룹 이름 보내기
                        intent.putExtra("groupName", listItem.getHead());
                        // 참여 상태 보내기
                        intent.putExtra("status", status);

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        // 액티비티 전환
                        parent.startActivity(intent);

                    }
                }.execute(listItem.getGroupId(), pref.getString("user_id", ""));
            }
        });

        /*// 참가 버튼 누르면 참가신청
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
                                    "\"userid\":\"" + pref.getString("user_id", "") + "\"," +
                                    "\"uuid\":\""   + groupId                       + "\""  +
                                    "}";

                            RequestBody body = RequestBody.create(Environments.JSON, sendData);

                            Request request = new Request.Builder()
                                    .url(Environments.LARAVEL_SOL_SERVER + "/member")
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
                        holder.joinButton.setClickable(false);
                        Toast.makeText(listItem.getParent(), "참가신청 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }.execute();
            }
        });*/

        if (position % 2 == 1) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(listItem.getParent(), R.color.grey_100));
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(listItem.getParent(), R.color.white));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView        cardView;               // CardView
        private TextView        textViewHead;           // 제목
        private TextView        textViewWriter;         // 작성자
        private TextView        textViewContent;        // 내용
        private Button          moveToGroupDetailBtn;   // 클릭하면 TabsActivity로 이동할 버튼
        private LinearLayout    linearLayout;           // 클릭하면 이동할 레이아웃

        /**
         * 레이아웃과 연결
         * @param itemView  재사용할 레이아웃
         */
        public ViewHolder(View itemView) {
            super(itemView);

            cardView                = (CardView)        itemView.findViewById(R.id.cardView);
            textViewHead            = (TextView)        itemView.findViewById(R.id.textViewHead);
            textViewWriter          = (TextView)        itemView.findViewById(R.id.tvWriter);
            textViewContent         = (TextView)        itemView.findViewById(R.id.tvContent);
            moveToGroupDetailBtn    = (Button)          itemView.findViewById(R.id.MoveToGroupDetailBtn);
            linearLayout            = (LinearLayout)    itemView.findViewById(R.id.linearLayout);
        }
    }

}