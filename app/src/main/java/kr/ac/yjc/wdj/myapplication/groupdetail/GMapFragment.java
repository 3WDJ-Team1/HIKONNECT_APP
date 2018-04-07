package kr.ac.yjc.wdj.myapplication.groupdetail;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.ac.yjc.wdj.myapplication.R;

/**
 * Created by 강성은 on 2018-04-07.
 */

public class GMapFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.group_detail_plan, container, false);
        return myFragmentView;
    }
}
