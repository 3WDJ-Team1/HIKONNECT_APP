package kr.ac.yjc.wdj.hikonnect.apis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import kr.ac.yjc.wdj.hikonnect.Environments;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadProfileIMG extends AsyncTask<Integer, Integer, Bitmap> {
    static final String TAG = "ProfilePNG";

    OkHttpClient okHttpClient = new OkHttpClient();

    static final int BIT_MAP_SIZE = 15;

    Bitmap bitmap;


    public Bitmap getUserProfileImg(final String userID) {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpUrl.Builder urlBuilder = HttpUrl
                                .parse(/*Environments.NODE_HIKONNECT_IP*/Environments.NODE_SOL_SERVER + "/images/UserProfile/" + userID + ".jpg")
                                .newBuilder();

                        String reqUrl = urlBuilder.build().toString();

                        Request req = new Request.Builder()
                                .url(reqUrl)
                                .build();

                        okHttpClient.newCall(req).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                bitmap = null;
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {

                                    InputStream is = response.body().byteStream();

                                    Bitmap originBitmap = BitmapFactory.decodeStream(is);

                                    bitmap = Bitmap.createScaledBitmap(originBitmap, BIT_MAP_SIZE, BIT_MAP_SIZE, true);
                                } catch (Exception e) {
                                    Log.e(TAG, "onResponse: ", e);
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "run: ", e);
                    }
                }
            });
            thread.start();
            thread.join();

        } catch (Exception e) {
            Log.e(TAG, "getUserProfileImg: ", e);
        }
        return null;
    }

    @Override
    protected Bitmap doInBackground(Integer... integers) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
