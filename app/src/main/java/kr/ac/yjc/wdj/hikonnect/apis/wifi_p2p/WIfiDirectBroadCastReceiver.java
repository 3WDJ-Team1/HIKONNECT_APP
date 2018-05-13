package kr.ac.yjc.wdj.hikonnect.apis.wifi_p2p;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.widget.Toast;

/**
 * @author  Beomsu Kwon
 * @since   2018-04-19
 */
public class WIfiDirectBroadCastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;

    private Channel mChannel;

    private Activity mActivity;

    private WifiP2pInfo netInfo;

    public WIfiDirectBroadCastReceiver(WifiP2pManager mManager, Channel mChannel, Activity mActivity) {
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.mActivity = mActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Toast.makeText(context, action, Toast.LENGTH_SHORT).show();
    }
}
