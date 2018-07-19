package kr.ac.yjc.wdj.hikonnect.activities.group;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * The class used when check network connection status
 * @author  Areum Lee (leear5799@gmail.com)
 * @since   2018-06-15
 */

public class NetworkHelper {
    private static final String TAG = "NetworkHelper";

    public static boolean isConnectedToInternet(Context context) {
        try {
            if (context != null) {
                ConnectivityManager connectivityManager
                        = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }
}
