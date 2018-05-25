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
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.ac.yjc.wdj.hikonnect.apis.HttpRequest.HttpRequestConnection;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author  Jungyu Choi
 * @author  bs Kwon<rnjs9957@gmail.com>
 * @since   2018-04-11
 */
public class  Locationmemo extends Activity {

    int             memo_num;           // Location memo's number.
    String          memoTitleStr;       // Location memo's Title.
    String          memoContentsStr;    // Location memo's Contents.
    String          memoWriterStr;      // Location memo's Writer.
    String          memoNicknameStr;    // Writer's nickname.
    String          memoCreatedTimeStr; // Location memo's created time.

    String          result;             // Http response contents.

    TextView        txtViewCreatedTime;
    TextView        txtViewContents;
    TextView        txtViewTitle;
    TextView        txtViewWriter;
    ImageView       imgViewPicture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);     // Disable Title bar.
        setContentView(R.layout.location_memo);

        // Get location memo number from parents activity.
        Intent intent = getIntent();
        memo_num = intent.getIntExtra("location_no", 0);

        // Initialize UI.
        txtViewCreatedTime      = (TextView)    findViewById(R.id.txtMemoCreatedTime);
        txtViewTitle            = (TextView)    findViewById(R.id.txtLocationMemoTitle);
        txtViewWriter           = (TextView)    findViewById(R.id.txtLocationMemoWriter);
        txtViewContents         = (TextView)    findViewById(R.id.txtLocationMemoContent);
        imgViewPicture          = (ImageView)   findViewById(R.id.imgViewLocationMemoPic);

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
                            .add("location_no", String.valueOf(memo_num))
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
                        memoTitleStr        = jsonObject.getString("title");
                        memoContentsStr     = jsonObject.getString("content");
                        memoWriterStr       = jsonObject.getString("writer");
                        memoNicknameStr     = jsonObject.getString("nickname");

                        String createdTime  = jsonObject.getString("created_at");
                        Date date           = new SimpleDateFormat("yy-mm-dd").parse(createdTime);
                        memoCreatedTimeStr  = new SimpleDateFormat("yyyy년 mm월 dd일 E요일").format(date);
                    }

                    httpUrl = HttpUrl
                            .parse(Environments.NODE_HIKONNECT_IP + "/images/LocationMemo/" + memo_num + "_" + memoWriterStr + ".jpg")
                            .newBuilder()
                            .build();

                    req = new Request.Builder().url(httpUrl).build();

                    Response res = client.newCall(req).execute();

                    InputStream is = res.body().byteStream();

                    final Bitmap bitmap = BitmapFactory.decodeStream(is);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtViewCreatedTime.setText(memoCreatedTimeStr.toString());
                            txtViewTitle.setText(memoTitleStr);
                            txtViewContents.setText(memoContentsStr);
                            txtViewWriter.setText(memoNicknameStr);
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