package kr.ac.yjc.wdj.hikonnect.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.klinker.android.sliding.SlidingActivity;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClusterDetailActivity extends SlidingActivity {
    public static final int CLUSTER_CLICKED = 1;

    private static final String TAG = "HIKONNECT";

    private ArrayList<Integer>                      memberIDs;
    private ArrayList<Integer>                      lMemoIDs;
    private ArrayList<ClusterDetailRecyclerBean>    clusterBeans = new ArrayList<>();

    private RecyclerView            recyclerView;

    @Override
    public void init(Bundle bundle) {
        getArgsFromParent();

        setPrimaryColors(
                getResources().getColor(R.color.mapColorPrimary),
                getResources().getColor(R.color.mapColorPrimaryDark)
        );

        disableHeader();
    }

    @SuppressLint("StaticFieldLeak")
    private void getArgsFromParent() {
        Intent fromParent = getIntent();

        switch (fromParent.getIntExtra("key", 0)) {
            case CLUSTER_CLICKED:
                memberIDs   = fromParent.getIntegerArrayListExtra("memberIDs");
                lMemoIDs    = fromParent.getIntegerArrayListExtra("lMemoIDs");

                setContent(R.layout.cluster_detail_activity);

                recyclerView = findViewById(R.id.cluster_items);
                recyclerView.setAdapter(new ClusterDetailRecyclerViewAdapter(R.layout.cluster_detail_list_item, clusterBeans, this));
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

                final OkHttpClient client = new OkHttpClient();

                for (Integer memberID : memberIDs) {
                    Members member = new Members();
                    member.setNo(memberID);

                    new AsyncTask<Integer, Integer, String>() {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();

                        }

                        @Override
                        protected String doInBackground(Integer... integers) {
                            try {

                                int _memberID = integers[0];

                                HttpUrl httpUrl = HttpUrl
                                        .parse(Environments.LARAVEL_HIKONNECT_IP + "/api/getMemberDetail")
                                        .newBuilder()
                                        .build();

                                RequestBody requestBody = new FormBody
                                        .Builder()
                                        .add("member_no", String.valueOf(_memberID))
                                        .build();

                                Request request = new Request.Builder()
                                        .url(httpUrl)
                                        .post(requestBody)
                                        .build();

                                Response response = client.newCall(request).execute();

                                return response.body().toString();

                            } catch (IOException e) {
                                Log.e(TAG, "HTTP error: ", e);
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(String string) {
                            super.onPostExecute(string);

                            Log.d(TAG, "HTTP response: " + string);
                        }
                    }.execute(memberID);

                    clusterBeans.add(member);
                }

                for (Integer lMemoID : lMemoIDs) {
                    new AsyncTask<Integer, Integer, String>() {

                        @Override
                        protected String doInBackground(Integer... integers) {
                            try {

                                int locationMemoID = integers[0];

                                HttpUrl httpUrl = HttpUrl
                                        .parse(Environments.LARAVEL_HIKONNECT_IP + "/api/getLocationMemoDetail")
                                        .newBuilder()
                                        .build();

                                RequestBody requestBody = new FormBody
                                        .Builder()
                                        .add("location_no", String.valueOf(locationMemoID))
                                        .build();

                                Request request = new Request.Builder()
                                        .url(httpUrl)
                                        .post(requestBody)
                                        .build();

                                Response response = client.newCall(request).execute();

                                return response.body().string();

                            } catch (IOException | NullPointerException e) {
                                Log.e(TAG, "HTTP error: ", e);
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            try {

                                super.onPostExecute(s);

                                JSONObject jsonObject = (JSONObject) ((JSONArray) new JSONParser().parse(s)).get(0);

                                Log.d(TAG, "array: " + jsonObject);

                                LocationMemo locationMemo = new LocationMemo();
                                locationMemo.setName(jsonObject.get("title").toString());

                                clusterBeans.add(locationMemo);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute(lMemoID);
                }

                break;
        }
    }
}

abstract class ClusterDetailRecyclerBean {
    private int         no;
    private Drawable    image;
    private String      name;

    public void setImage(Drawable image) {
        this.image = image;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setNo(int no) {
        this.no = no;
    }

    public Drawable     getImage() {
        return image;
    }
    public String       getName() {
        return name;
    }
    public int getNo() {
        return no;
    }
}

class LocationMemo extends ClusterDetailRecyclerBean {

}

class Members extends ClusterDetailRecyclerBean {

}

class ClusterDetailRecyclerViewAdapter extends RecyclerView.Adapter<ClusterDetailRecyclerViewAdapter.ViewHolder> {

    ArrayList<ClusterDetailRecyclerBean>    items;
    int                                     layout;
    Activity parent;

    ClusterDetailRecyclerViewAdapter(int layout, ArrayList<ClusterDetailRecyclerBean> items, Activity parent) {
        this.items      = items;
        this.layout     = layout;
        this.parent     = parent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(parent).inflate(layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        try {

            ClusterDetailRecyclerBean item = items.get(i);

            viewHolder.itemWrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                }
            });
            viewHolder.itemTextView.setText(item.getName());

            if (item instanceof Members) {
                viewHolder.itemImageView.setImageDrawable(item.getImage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout    itemWrapper;
        private ImageView           itemImageView;
        private TextView            itemTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            itemWrapper     = itemView.findViewById(R.id.item_wrapper);
            itemImageView   = itemView.findViewById(R.id.item_image);
            itemTextView    = itemView.findViewById(R.id.item_name);
        }
    }
}