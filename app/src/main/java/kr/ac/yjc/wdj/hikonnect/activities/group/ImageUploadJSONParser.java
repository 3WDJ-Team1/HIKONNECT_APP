package kr.ac.yjc.wdj.hikonnect.activities.group;

import android.util.Log;

import org.json.JSONObject;

import java.io.File;

import kr.ac.yjc.wdj.hikonnect.Environments;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by LEE AREUM on 2018-06-15.
 */

public class ImageUploadJSONParser {

    public static JSONObject uploadImage(String sourceImgFile) {
        try {
            File sourceFile                 = new File(sourceImgFile);

            final MediaType MEDIA_TYPE_PNG  = MediaType.parse("image/*");
            String filename                 = sourceImgFile.substring(sourceImgFile.lastIndexOf("/") + 1);

            OkHttpClient client = new OkHttpClient();

            // okhttp3
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("", filename, RequestBody.create(MEDIA_TYPE_PNG, sourceFile))
                    .build();

            Request request = new Request.Builder()
                    .url(Environments.NODE_HIKONNECT_IP + "/image/announce")
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            String res = response.body().string();

            Log.d("ImageUpload", res);

            return new JSONObject(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
