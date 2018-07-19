package kr.ac.yjc.wdj.hikonnect.activities.group;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.ac.yjc.wdj.hikonnect.R;

/**
 * Created by LEE AREUM on 2018-06-21.
 */

public class MapFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.group_detail_plan, container, false);
        return myFragmentView;
    }
}
