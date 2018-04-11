package kr.ac.yjc.wdj.myapplication.group_list;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import kr.ac.yjc.wdj.myapplication.R;

public class edit_view extends Fragment {
    EditText editText0;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_view, container, false);
        editText0 = (EditText)view.findViewById(R.id.editText0);
        return view;
    }
}
