package kr.ac.yjc.wdj.hikonnect.activities.user;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.PreActivity;
import kr.ac.yjc.wdj.hikonnect.apis.http_request.HttpRequestConnection;

/**
 * @file        kr.ac.yjc.wdj.hikonnect.activities.MainActivity.java
 * @author      Areum Lee (leear5799@gmail.com)
 * @since       2018-04-26
 * @brief       The Activity used when register new user
 */

public class UserRegisterActivity extends AppCompatActivity {
        EditText id, pw, nickname, phoneNum;
        Spinner gender, age_group;
        CheckBox gendersc, phonesc, agesc, groupsc;
        Button logup, cancle;
        ContentValues contentValues = new ContentValues();
        Intent intent;
        Toolbar myToolbar;
        DrawerLayout dlDrawer;
        ActionBarDrawerToggle dtToggle;
        RelativeLayout rLayout;
        HttpRequestConnection hrc = new HttpRequestConnection();
        String result;
        Handler handler;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.user_registeration);

            myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            setSupportActionBar(myToolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(true);

            dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
            dlDrawer.addDrawerListener(dtToggle);

            // Spinner
            Spinner gdSpinner = (Spinner)findViewById(R.id.gender_spinner);
            ArrayAdapter gdAdapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
            gdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            gdSpinner.setAdapter(gdAdapter);

            Spinner ageSpinner = (Spinner)findViewById(R.id.age_group_spinner);
            ArrayAdapter ageAdapter = ArrayAdapter.createFromResource(this, R.array.age_group, android.R.layout.simple_spinner_item);
            ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ageSpinner.setAdapter(ageAdapter);


            //loginProgressBar = findViewById(R.id.login_progress_bar);

            id              = findViewById(R.id.id_editText);
            pw              = findViewById(R.id.pw_editText);
            nickname        = findViewById(R.id.nickname_editText);
            phoneNum        = findViewById(R.id.phoneNum_editText);

            gender          = findViewById(R.id.gender_spinner);
            age_group       = findViewById(R.id.age_group_spinner);

            logup           = findViewById(R.id.signUpBtn);
            cancle          = findViewById(R.id.cancelBtn);

            /*gendersc        = findViewById(R.id.gender_ck);
            phonesc         = findViewById(R.id.phone_ck);
            agesc           = findViewById(R.id.age_ck);
            groupsc         = findViewById(R.id.private_ck);*/

            logup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        contentValues.put("idv", id.getText().toString());
                        contentValues.put("pwv", pw.getText().toString());
                        contentValues.put("nn", nickname.getText().toString());
                        contentValues.put("gender", gender.getSelectedItem().toString());
                        contentValues.put("age", age_group.getSelectedItem().toString());
                        contentValues.put("phone", phoneNum.getText().toString());
                        contentValues.put("phonesc", true);
                        contentValues.put("gendersc",true);
                        contentValues.put("agesc", true);
                        contentValues.put("groupsc", "all");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                result = hrc.request(Environments.LARAVEL_HIKONNECT_IP + "/api/user", contentValues);
                                Message msg = handler.obtainMessage();
                                handler.sendMessage(msg);
                            }
                        }).start();
                        handler = new Handler() {
                            public void handleMessage(Message msg) {
                                if (msg.what == 0) {
                                    Intent intent = new Intent(UserRegisterActivity.this,PreActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    //
                                }
                            }
                        };
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
/*
            // 화면 터치 시, 키보드 자판 없어짐
            rLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imManager.hideSoftInputFromWindow(logup.getWindowToken(), 0);
                }
            });

            cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(UserRegisterActivity.this, MainActivity.class));
                }
            });*/
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        protected void onPostCreate(@Nullable Bundle savedInstanceState) {
            super.onPostCreate(savedInstanceState);

            dtToggle.syncState();
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);

            dtToggle.onConfigurationChanged(newConfig);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (dtToggle.onOptionsItemSelected(item)) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    }