package kr.ac.yjc.wdj.myapplication.WifiP2p;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Callback;

/**
 * Created by LEE AREUM on 2018-04-14.
 */

public class HttpConnection {
    OkHttpClient client = new OkHttpClient();
    private static HttpConnection instance = new HttpConnection();
    public static HttpConnection getInstance() {
        return instance;
    }

    HttpUrl httpUrl = new HttpUrl.Builder()
            .scheme("http")
            .host("10.0.2.2")
            .port(8000)
            .addPathSegment("api/HttpRequest")
            .build();

    public void requestWebServer(String uuid, String hiking_group, Callback callback) {
        RequestBody body = new FormBody.Builder()
                           .add("uuid", uuid)
                           .add("hiking_group", hiking_group)
                           .build();

        Request request = new Request.Builder()
                          .url(httpUrl)
                          .post(body)
                          .build();

        client.newCall(request).enqueue(callback);
    }
}
