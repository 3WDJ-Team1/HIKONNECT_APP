package kr.ac.yjc.wdj.hikonnect.activities.myPage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.session.SessionManager;

/**
 * Created by LEE AREUM on 2018-05-17.
 */

public class UserProfileActivity extends AppCompatActivity{
    private SessionManager  session;
    private String          id;
    private ImageView       userImg;
    private String          SERVER_ADDRESS = "http://172.26.2.88:3000";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> userId = session.getUserId();
        id = userId.get(SessionManager.KEY_ID);

        userImg = (ImageView) findViewById(R.id.user_profile_img);

        new DownloadImage(id).execute();
    }

    public class DownloadImage extends AsyncTask<Void, Void, Bitmap> {
        String userid;

        public DownloadImage(String userid) {
            this.userid = userid;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            String url = SERVER_ADDRESS + "/images/UserProfile/" + userid + ".jpg";

            try {
                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000 * 30);

                return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                userImg.setImageBitmap(bitmap);
            }
        }
    }

}
