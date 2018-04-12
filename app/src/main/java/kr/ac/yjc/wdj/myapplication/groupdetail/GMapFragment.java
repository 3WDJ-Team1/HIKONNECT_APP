package kr.ac.yjc.wdj.myapplication.groupdetail;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.ac.yjc.wdj.myapplication.R;

/**
 * class to get MapFragmentView (which one is used in group detail page)
 * @author Sungeun Kang (kasueu0814@gmail.com)
 * @since  2018-04-07
 */
public class GMapFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.group_detail_plan, container, false);
        return myFragmentView;
    }
}
