package kr.ac.yjc.wdj.hikonnect;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.gun0912.tedpermission.PermissionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

import kr.ac.yjc.wdj.hikonnect.apis.HttpRequest.HttpRequestConnection;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author  Jungyu Choi
 * @since   2018-04-11
 */
public class  Locationmemo extends Activity {

    int location_num;
    String titlestring, contentstring,path,writer,result;
    Handler handler;
    TextView txtViewContents, txtViewTitle, txtViewWriter;
    Bitmap bitmap;
    ContentValues contentValues;
    HttpRequestConnection hrc;
    ImageView imgViewPicture;
    PermissionListener permissionlistener = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.location_memo);

        Intent intent = getIntent();
        location_num = intent.getIntExtra("location_no", 0);

        txtViewTitle    = (TextView)    findViewById(R.id.txtLocationMemoTitle);
        txtViewWriter   = (TextView)    findViewById(R.id.txtLocationMemoWriter);
        txtViewContents = (TextView)    findViewById(R.id.txtLocationMemoContent);
        imgViewPicture  = (ImageView)   findViewById(R.id.imgViewLocationMemoPic);

        contentValues = new ContentValues();
        hrc = new HttpRequestConnection();

        contentValues.put("location_no", location_num);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    OkHttpClient client = new OkHttpClient();

                    HttpUrl httpUrl = HttpUrl
                            .parse(Environments.LARAVEL_HIKONNECT_IP + "/api/getLocationMemoDetail")
                            .newBuilder()
                            .build();

                    RequestBody reqBody = new FormBody.Builder()
                            .add("location_no", String.valueOf(location_num))
                            .build();

                    Request req = new Request.Builder()
                            .url(httpUrl)
                            .post(reqBody)
                            .build();

                    Response response = client
                            .newCall(req)
                            .execute();

                    result = response.body().string();

                    Log.d("HIKONNECT", "run: response: " + result);

                    JSONArray jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        titlestring = jsonObject.getString("title");
                        contentstring = jsonObject.getString("content");
                        writer = jsonObject.getString("writer");
                    }

                    httpUrl = HttpUrl
                            .parse(Environments.NODE_HIKONNECT_IP + "/images/LocationMemo/" + location_num + "_" + writer + ".jpg")
                            .newBuilder()
                            .build();

                    req = new Request.Builder().url(httpUrl).build();

                    Response res = client.newCall(req).execute();

                    InputStream is = res.body().byteStream();

                    final Bitmap bitmap = BitmapFactory.decodeStream(is);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtViewTitle.setText(titlestring);
                            txtViewContents.setText(contentstring);
                            txtViewWriter.setText(writer);
                            imgViewPicture.setImageBitmap(bitmap);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void mOnClose2(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
}