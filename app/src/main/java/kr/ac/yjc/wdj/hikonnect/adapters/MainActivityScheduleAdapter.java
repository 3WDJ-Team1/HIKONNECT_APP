package kr.ac.yjc.wdj.hikonnect.adapters;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.beans.GroupSchedule;
import kr.ac.yjc.wdj.hikonnect.beans.MainActivitySchedule;
import kr.ac.yjc.wdj.hikonnect.fragments.ScheduleOnMainPage;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static kr.ac.yjc.wdj.hikonnect.Environments.APP_TAG;

public class MainActivityScheduleAdapter extends FragmentPagerAdapter {
    // 세션 유지
    private SharedPreferences pref;

    private ArrayList<MainActivitySchedule> schedules = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    public MainActivityScheduleAdapter(FragmentManager fm, SharedPreferences pref) {
        super(fm);

        this.pref = pref;

        new GetScheduleTask()
                .execute(pref.getString("user_id", "null"));
    }

    @Override
    public Fragment getItem(int i) {
        ScheduleOnMainPage page = new ScheduleOnMainPage();
        page.setData(schedules.get(i));
        return page;
    }

    @Override
    public int getCount() {
        return schedules.size();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetScheduleTask extends AsyncTask<String, MainActivitySchedule, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String userID = strings[0];

                Log.d(APP_TAG, "Get Schedules: user ID: " + userID);

                HttpUrl httpUrl = HttpUrl
                        .parse(Environments.LARAVEL_HIKONNECT_IP + "/api/getNowSchedule")
                        .newBuilder()
                        .build();

                RequestBody requestBody = new FormBody
                        .Builder()
                        .add("user_id", userID)
                        .build();

                Request req = new Request.Builder()
                        .url(httpUrl)
                        .post(requestBody)
                        .build();

                OkHttpClient client = new OkHttpClient();

                Response res = client.newCall(req).execute();

                return res.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                super.onPostExecute(s);

                Log.d(APP_TAG, "onPostExecute: " + s);

                JSONParser parser = new JSONParser();

                JSONArray jsonArray = (JSONArray) parser.parse(s);

                MainActivitySchedule schedule;

                for (Object o : jsonArray) {
                    JSONObject _schedule = (JSONObject) o;

                    String mntName      = (String) _schedule.get("mnt_name");
                    String title        = (String) _schedule.get("title");
                    String groupName    = (String) _schedule.get("group_name");
                    String scheduleNum  = String.valueOf(_schedule.get("schedule_no"));
                    String startDate    = (String) _schedule.get("start_date");
                    String leader       = (String) _schedule.get("leader");

                    schedule = new MainActivitySchedule();
                    schedule.setMntName(mntName);
                    schedule.setTitle(title);
                    schedule.setGroupName(groupName);
                    schedule.setScheduleNum(scheduleNum);
                    schedule.setStartDate(startDate);
                    schedule.setLeader(leader);

                    schedules.add(schedule);
                }

                notifyDataSetChanged();
                Log.d(APP_TAG, "END: " + schedules);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
