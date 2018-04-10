package kr.ac.yjc.wdj.myapplication.notification;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import kr.ac.yjc.wdj.myapplication.R;

public class NotificationService extends AppCompatActivity {


    NotificationCompat.Builder notification;
    private static final int uniqueID = 45612;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_service);

        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

    }

    public  void bucksButtonClicked(View view)  {
        // Build the notification
//        notification.setSmallIcon(dr)
    }
}
