package kr.ac.yjc.wdj.myapplication.models;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import kr.ac.yjc.wdj.myapplication.HttpRequestConnection;

/**
 * Created by Kwon on 3/26/2018.
 */

public class HikingPlan {

    public HttpRequestConnection httpReqConn = null;

    public HikingPlan() {
        this.httpReqConn = new HttpRequestConnection();
    }

    public static class NetworkTask extends AsyncTask<Void, Void, String> {
        private final String TAG = "NetworkTask";

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {
            this.url    = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            HttpRequestConnection httpRequestConnection = new HttpRequestConnection();
            result = httpRequestConnection.request(url, values);

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d(TAG, s);
        }
    }
}
