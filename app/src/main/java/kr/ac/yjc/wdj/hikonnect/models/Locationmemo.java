package kr.ac.yjc.wdj.hikonnect.models;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;


import kr.ac.yjc.wdj.hikonnect.R;


/**
 * @author  Jungyu Choi
 * @since   2018-04-11
 */
public class Locationmemo extends Activity {

    TextView title;
    TextView content;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_memo);

        title = findViewById(R.id.gettitle);
        content = findViewById(R.id.getcontent);

        Intent intent = getIntent();
        title.setText(intent.getStringExtra("title"));
        content.setText(intent.getStringExtra("content")+intent.getStringExtra("position"));


    }
}
