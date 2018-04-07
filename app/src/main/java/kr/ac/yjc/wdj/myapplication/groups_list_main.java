package kr.ac.yjc.wdj.myapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class groups_list_main extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    LinearLayout    list;
    Spinner         spinner;
    DatePicker      datePicker;
    Button          button;
    TextView        textView;
    int flag=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list_main);

        datePicker =    (DatePicker) findViewById(R.id.Datepicker);
        spinner =       (Spinner) findViewById(R.id.planets_spinner);
        list =          (LinearLayout) findViewById(R.id.list);
        button =        (Button) findViewById(R.id.set);
        textView =      (TextView) findViewById(R.id.textView);

        FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        text_view fragment=new text_view();
        fragmentTransaction.add(R.id.container, fragment);
        fragmentTransaction.commit();


        ////////////////////////////////////////////////////스피너////////////////////////////////////////////////////
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.groups_list_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // spinner에 setOnItemSelecedListener의 객체 new AdapterBiew.OnItemSeletedLiener()를 만들어
        spinner.setOnItemSelectedListener(this);





        ////////////////////////////////////////////////////그룹 리스트////////////////////////////////////////////////////
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(R.layout.groups_list, list, true);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // parent.getItemAtPosition(pos).toString()
        if (pos == 2) {
            datePicker.setVisibility(datePicker.VISIBLE);
            Fragment fragment;
            switch (flag)   {
                default:
                case 0: {
                    fragment=new edit_view();
                    flag=1;
                    break;
                }
                case 1: {
                    fragment=new text_view();
                    flag=0;
                    break;
                }
            }
            FragmentManager fragmentManager=getFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    // textView.setText(datePicker.getDayOfMonth() + "." + datePicker.getMonth() + "." + datePicker.getYear());
                    Toast.makeText(groups_list_main.this, datePicker.getDayOfMonth() + "" + datePicker.getMonth() + "" + datePicker.getYear(), Toast.LENGTH_LONG).show();
                    datePicker.setVisibility(datePicker.INVISIBLE);
                }
            });
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}

