package kr.ac.yjc.wdj.hikonnect.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;


import kr.ac.yjc.wdj.hikonnect.R;

/**
 * Created by 강성은 on 2018-05-23.
 */

public class LoadingDialog extends Dialog {

    private Context         context;
    private RelativeLayout  layout;     // 로딩 전체 레이아웃

    /**
     * 생성자 오버라이딩
     * @param context
     */
    public LoadingDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);

        // 레이아웃 초기화
        layout = (RelativeLayout) findViewById(R.id.progressLayout);
    }
}
