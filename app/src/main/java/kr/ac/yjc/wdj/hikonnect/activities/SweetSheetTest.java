package kr.ac.yjc.wdj.hikonnect.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.SweetSheet;

import kr.ac.yjc.wdj.hikonnect.R;

public class SweetSheetTest extends Activity {

    RelativeLayout  parentLayout;
    SweetSheet      mSweetSheet;

    TextView        currentSpeedValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sweet_sheet_test);

        parentLayout    = findViewById(R.id.parent_relative_layout);
        mSweetSheet     = new SweetSheet(parentLayout);

        CustomDelegate customDelegate = new CustomDelegate(true, CustomDelegate.AnimationType.DuangLayoutAnimation);

        View view = LayoutInflater.from(this).inflate(R.layout.map_hiking_info, null, false);

        currentSpeedValue = view.findViewById(R.id.current_speed_value);

        customDelegate.setCustomView(view);

        mSweetSheet.setDelegate(customDelegate);

        findViewById(R.id.handle_sweet_sheet).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSweetSheet.show();
            }
        });
    }
}
