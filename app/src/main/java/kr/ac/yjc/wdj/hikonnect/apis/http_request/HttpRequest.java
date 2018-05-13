//package kr.ac.yjc.wdj.hikonnect.apis.http_request;
//
//import android.content.AbstractThreadedSyncAdapter;
//import android.os.AsyncTask;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.HashMap;
//
//import okhttp3.HttpUrl;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
///**
// * Created by Kwon on 4/14/2018.
// */
//
//public class HttpRequest {
//
//    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//    private OkHttpClient client = new OkHttpClient();
//
//    private Request req;
//
//    private Response res;
//
//    public String get(URL argUrl, HashMap<String, String> params){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Request req = new Request.Builder()
//                            .url(addUrlParam(params, argUrl))
//                            .get()
//                            .build();
//
//                    Response res = client.newCall(req).execute();
//
//                    res.body().string();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).run();
//
//        return ;
//    }
//
//    public String post() {
//
//    }
//
//    public String patch() {
//
//    }
//
//    public String delete() {
//
//    }
//
//    public HttpUrl addUrlParam(HashMap<String, String> params, URL argUrl) throws NullPointerException{
//
//        HttpUrl.Builder reqUrl = HttpUrl.parse(argUrl.toString()).newBuilder();
//
//        for(HashMap.Entry<String, String> entry : params.entrySet()) {
//            String key      = entry.getKey();
//            String value    = entry.getValue();
//
//            reqUrl.addQueryParameter(key, value);
//        }
//
//
//        return reqUrl.build();
//    }
//
//    public RequestBody createReqBody(HashMap<String, String> params) throws JSONException {
//        JSONObject json = new JSONObject();
//
//        for(HashMap.Entry<String, String> entry : params.entrySet()) {
//            String key      = entry.getKey();
//            String value    = entry.getValue();
//
//            json.put(key, value);
//        }
//
//        return RequestBody.create(JSON, json.toString());
//    }
//}
