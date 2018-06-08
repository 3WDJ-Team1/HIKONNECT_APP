package kr.ac.yjc.wdj.hikonnect.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.MapItem;
import kr.ac.yjc.wdj.hikonnect.Member;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.MapsActivityTemp;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static kr.ac.yjc.wdj.hikonnect.activities.MapsActivityTemp.REQUEST_INTERVAL_TIME;
import static kr.ac.yjc.wdj.hikonnect.activities.MapsActivityTemp.TAG;

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private GoogleMap gMap;
    private FragmentActivity fActivity;

    boolean isInfoWindowShown = false;

    // 그룹 맴버 상태 표시
    private TextView tvOtherUserNickname;    // 유저의 닉네임 TextView.
    private TextView tvOtherUserSpeed;       // 현재 속도 TextView (값 -> km/h 기준).
    private TextView tvOtherUserDistance;    // 총 이동 거리 TextView (값 -> km 기준).
    private TextView tvOtherUserArriveWhen;  // 예상 도착 시간 TextView (값 -> 시간 기준).
    private TextView tvOtherUserRank;        // 등수 TextView (값 -> 등산 거리 기준).

    public MyInfoWindowAdapter(Context context, GoogleMap gMap, FragmentActivity fActivity) {
        this.context = context;
        this.gMap = gMap;
        this.fActivity = fActivity;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (marker.getTag() == null) return null;

        View view = ((Activity) context)
                .getLayoutInflater()
                .inflate(R.layout.layout_other_user_data_box, null);

        tvOtherUserNickname = view.findViewById(R.id.otherUserNickname);
        tvOtherUserSpeed = view.findViewById(R.id.otherUserSpeed);
        tvOtherUserDistance = view.findViewById(R.id.otherUserDistance);
        tvOtherUserArriveWhen = view.findViewById(R.id.otherUserArriveWhen);
        tvOtherUserRank = view.findViewById(R.id.otherUserRank);

        if (marker.getTag() instanceof Member) {
            String nickname = ((Member) marker.getTag()).nickname;
            String velocity = String.format("%.2f", ((Member) marker.getTag()).avgSpeed);
            String distance = String.format("%.2f", ((Member) marker.getTag()).hikedDistance);
            // String arrvieWhen   = ((Member) marker.getTag()).nickname;
            String rank = String.valueOf(((Member) marker.getTag()).rank);

            tvOtherUserNickname.setText(nickname);
            tvOtherUserSpeed.setText(velocity);
            tvOtherUserDistance.setText(distance);
            // tvOtherUserArriveWhen.setText(nickname);
            tvOtherUserRank.setText(rank);
        }

        requestOtherUserHikingInfo((MapItem) marker.getTag(), marker);

        return view;
    }

    private void requestOtherUserHikingInfo(final MapItem mapItem, final Marker marker) {

            new Thread(new Runnable() {
                String nickname;        // 맴버의 닉네임.
                double distance;        // 맴버의 등산 거리.
                double velocity;        // 맴버의 평균 속도.
                int rank;               // 맴버의 등수.

                @Override
                public void run() {

                    try {
                        // 타겟 URL 설정.
                        HttpUrl reqUrl = HttpUrl
                                .parse(Environments.LARAVEL_HIKONNECT_IP + "/api/getMemberDetail")
                                .newBuilder()
                                .build();

                        // 클릭 된 맴버의 ID값.
                        String member_no = String.valueOf(((Member) mapItem).member_no);

                        // Request Body에 From Data 입력.
                        RequestBody reqBody = new FormBody.Builder()
                                .add("member_no", member_no)    // 맴버의 ID값.
                                .build();

                        // Request 객체 생성.
                        Request req = new Request.Builder()
                                .url(reqUrl)
                                .post(reqBody)
                                .build();

                        // 서버의 Response.
                        Response response = new OkHttpClient().newCall(req).execute();

                        JSONParser parser = new JSONParser();

                        JSONObject result = (JSONObject) ((JSONArray) parser.parse(response.body().string())).get(0);

                        TextView txtViewDistance = fActivity.findViewById(R.id.distance);
                        double hikedDistance = Double.valueOf((String) txtViewDistance.getText());

                        nickname = String.valueOf(result.get("nickname"));                          // 맴버의 닉네임.
                        distance = Double.valueOf(result.get("distance").toString());               // 맴버가 걸어온 거리.
                        distance = Math.abs(hikedDistance - distance);                              // 소수점 2자리에서 반올림.
                        velocity = Double.valueOf(result.get("velocity").toString());               // 맴버의 평균 속도.
                        velocity = Math.abs(velocity);                                              // 소수점 2자리에서 반올림.
                        rank = Integer.valueOf(result.get("rank").toString());                      // 맴버의 등수.

                        Thread.sleep(REQUEST_INTERVAL_TIME);

                    } catch (Exception e) {
                        Log.e(TAG, "onClusterItemClick: ", e);
                    }

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                isInfoWindowShown = marker.isInfoWindowShown();
                                LatLng markerPos = ((MapItem) marker.getTag()).getPosition();

                                if (marker.getTag() instanceof Member) {
                                    ((Member) marker.getTag()).nickname = nickname;
                                    ((Member) marker.getTag()).hikedDistance = distance;
                                    ((Member) marker.getTag()).avgSpeed = velocity;
                                    ((Member) marker.getTag()).rank = rank;
                                }

                                // 데이터를 UI에 적용.
                                tvOtherUserNickname.setText(nickname);
                                tvOtherUserSpeed.setText(String.valueOf(velocity));
                                tvOtherUserDistance.setText(String.valueOf(distance));
                                tvOtherUserRank.setText(String.valueOf(rank));

                                // 다른 유저 정보 CardView가 보여질 때 지속적으로 값을 갱신.
                                if (marker.isInfoWindowShown()) {
                                    marker.showInfoWindow();
                                }
                            } catch (NullPointerException npe) {
                                Log.e(TAG, "run: ", npe);
                            }
                        }
                    });
                }
            }).start();
    }
}
