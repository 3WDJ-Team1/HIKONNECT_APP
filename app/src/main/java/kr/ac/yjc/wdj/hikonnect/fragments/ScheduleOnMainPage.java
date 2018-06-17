package kr.ac.yjc.wdj.hikonnect.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.MapsActivityTemp;
import kr.ac.yjc.wdj.hikonnect.beans.MainActivitySchedule;

import static kr.ac.yjc.wdj.hikonnect.Environments.APP_TAG;

public class ScheduleOnMainPage extends Fragment {

    MainActivitySchedule data;

    public ScheduleOnMainPage() {

    }

    public void setData(MainActivitySchedule data) {
        this.data = data;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (data != null) {
            try {
                RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.item_schedule_main, container, false);

                TextView titleTextView      = layout.findViewById(R.id.nowScheduleTitle);
                TextView groupNameTextView  = layout.findViewById(R.id.nowScheduleGroupName);
                TextView leaderTextView     = layout.findViewById(R.id.nowScheduleLeader);
                TextView destMntTextView    = layout.findViewById(R.id.nowScheduleDestination);
                TextView startDateTextView  = layout.findViewById(R.id.nowScheduleDate);

                Button showOnMap            = layout.findViewById(R.id.btnStartHiking);

                titleTextView.setText(data.getTitle());
                groupNameTextView.setText(data.getGroupName());
                leaderTextView.setText(data.getLeader());
                destMntTextView.setText(data.getMntName());
                @SuppressLint("SimpleDateFormat")
                String startDate = new SimpleDateFormat("yyyy년 MM월 dd일").format(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(data.getStartDate()));
                startDateTextView.setText(startDate);

                showOnMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setClickable(false);

                        Intent intent = new Intent(getContext(), MapsActivityTemp.class);
                        intent.putExtra("schedule_no", data.getScheduleNum());
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

                return layout;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {

        }
        return null;
    }
}
