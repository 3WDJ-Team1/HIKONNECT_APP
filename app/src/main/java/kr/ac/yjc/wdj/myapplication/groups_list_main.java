package kr.ac.yjc.wdj.myapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class groups_list_main extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    LinearLayout        list;
    LinearLayout        container;
    Spinner             spinner;
    DatePicker          datePicker;
    Button              button;
    int cusor = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list_main);

        datePicker      = (DatePicker) findViewById(R.id.Datepicker);
        spinner         = (Spinner) findViewById(R.id.planets_spinner);
        list            = (LinearLayout) findViewById(R.id.list);
        container       = (LinearLayout) findViewById(R.id.container);
        button          = (Button) findViewById(R.id.set);

        FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        edit_view fragment = new edit_view();
        fragmentTransaction.add(R.id.container, fragment).commit();

        ////////////////////////////////////////////////////스피너////////////////////////////////////////////////////
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.groups_list_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter.notifyDataSetChanged();
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);


        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cusor == 0)  {
                    calendar(1);
                }
            }
        });


        ////////////////////////////////////////////////////그룹 리스트////////////////////////////////////////////////////
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(R.layout.groups_list, list, true);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if (pos == 2)   calendar(1);
        else            calendar(0);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(groups_list_main.this, "ddd", Toast.LENGTH_SHORT).show();
    }

    public void calendar(int i)  {
        Fragment fragment;
        switch (i)   {
            default:
            case 0: {
                fragment=new edit_view();
                cusor = 1;
                break;
            }
            case 1: {
                datePicker.setVisibility(datePicker.VISIBLE);
                fragment=new text_view();
                cusor = 0;
                break;
            }
        }
        FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).commit();
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // textView.setText(datePicker.getDayOfMonth() + "." + datePicker.getMonth() + "." + datePicker.getYear());
                text_view.textView0.setText(datePicker.getDayOfMonth() + "." + datePicker.getMonth() + "." + datePicker.getYear());
                datePicker.setVisibility(datePicker.INVISIBLE);
            }
        });
    }
}

