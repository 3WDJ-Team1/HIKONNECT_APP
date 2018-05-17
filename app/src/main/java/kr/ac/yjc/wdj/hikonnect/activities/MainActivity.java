package kr.ac.yjc.wdj.hikonnect.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.groups.GroupActivity;
import kr.ac.yjc.wdj.hikonnect.activities.myPage.UserGroupActivity;
import kr.ac.yjc.wdj.hikonnect.activities.myPage.UserProfileActivity;
import kr.ac.yjc.wdj.hikonnect.activities.myPage.UserRecordActivity;
import kr.ac.yjc.wdj.hikonnect.activities.session.SessionManager;

/**
 * @file        kr.ac.yjc.wdj.hikonnect.activities.MainActivity.java
 * @author      Areum Lee (leear5799@gmail.com)
 * @since       2018-04-24
 * @brief       The Activity used app's main page
 */

public class MainActivity extends AppCompatActivity
            implements NavigationView.OnNavigationItemSelectedListener{
    ActionBarDrawerToggle   toggle;
    NavigationView          navigationView;
    SessionManager          session;
    LinearLayout            linearLayout;
    DrawerLayout            drawer;
    TextView                txtView;
    Toolbar                 toolbar;
    String                  user;
    String                  id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> userId = session.getUserId();
        id = userId.get(SessionManager.KEY_ID);

        /*txtView = (TextView) findViewById(R.id.main_textview);
        txtView.setText(id);*/

        txtView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.main_textview);

        txtView.setText(id);

        //changeTxtName(id);
    }

   /* private void changeTxtName(String id) {
        txtView = (TextView) findViewById(R.id.main_textview);
        txtView.setText(id);
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.groups) {
            startActivity(new Intent(this, GroupActivity.class));
        } else if (id == R.id.my_groups) {
            startActivity(new Intent(this, UserGroupActivity.class));
        } else if (id == R.id.my_records) {
            startActivity(new Intent(this, UserRecordActivity.class));
        } else if (id == R.id.my_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
        } else if (id == R.id.log_out) {
            session.logOutUser();
            //startActivity(new Intent(this, PreActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}

