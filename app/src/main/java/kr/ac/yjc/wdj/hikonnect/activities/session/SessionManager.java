package kr.ac.yjc.wdj.hikonnect.activities.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import kr.ac.yjc.wdj.hikonnect.activities.PreActivity;
import kr.ac.yjc.wdj.hikonnect.activities.user.LoginActivity;

/**
 * Created by LEE AREUM on 2018-05-07.
 */

public class SessionManager {
    SharedPreferences           pref;
    Context                     context;
    SharedPreferences.Editor    editor;

    int PRIVATE_MODE =0;

    private static final String PREF_NAME   = "AndroidHivePref";
    private static final String IS_LOGIN    = "IgLoggedIn";
    public static final String KEY_ID       = "id";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    // Create login session
    public void createLogInSession(String id) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.commit();
    }

    // Check user login status
    public void checkLogin() {
        if (!this.isLoggedIn()) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    // Get stored session data
    public HashMap<String, String> getUserId() {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_ID, pref.getString(KEY_ID, null));

        return user;
    }

    // Clear session details
    public void logOutUser() {
        editor.clear();
        editor.commit();

        Intent intent = new Intent(context, PreActivity.class);
        context.startActivity(intent);
    }

    // Quick check for login
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}

