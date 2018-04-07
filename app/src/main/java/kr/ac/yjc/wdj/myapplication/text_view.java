package kr.ac.yjc.wdj.myapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class text_view extends Fragment {
    static TextView textView0;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.text_view, container, false);
        textView0 = (TextView)view.findViewById(R.id.textView0);
        return view;
    }
}


