package kr.ac.yjc.wdj.hikonnect.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import kr.ac.yjc.wdj.hikonnect.R;

public class HomeActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
    }
}
