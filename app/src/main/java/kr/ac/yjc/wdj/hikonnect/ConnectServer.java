package kr.ac.yjc.wdj.hikonnect;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author  Jungyu Choi
 * @since   2018-05-08
 */
public class ConnectServer {
    OkHttpClient client = new OkHttpClient();

    public void requestGet(String url, String searchKey){

        //URL에 포함할 Query문 작성 Name&Value
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addEncodedQueryParameter("searchKey", searchKey);
        String requestUrl = urlBuilder.build().toString();

        //Query문이 들어간 URL을 토대로 Request 생성
        Request request = new Request.Builder().url(requestUrl).build();

        //만들어진 Request를 서버로 요청할 Client 생성
        //Callback을 통해 비동기 방식으로 통신을 하여 서버로부터 받은 응답을 어떻게 처리 할 지 정의함
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("error", "Connect Server Error is " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("aaaa", "Response Body is " + response.body().string());
            }
        });
    }

}
