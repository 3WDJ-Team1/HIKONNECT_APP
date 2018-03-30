package kr.ac.yjc.wdj.myapplication.APIs.WifiP2p;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.util.Log;

public class WifiP2pController {
    private static String TAG = "WifiP2pController";

    private WifiP2pManager mManager;

    public WifiP2pController(WifiP2pManager manager) {

        this.mManager = manager;

    }

    public void discoverPeers(Channel channel) {
        mManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Success discover peers");
            }

            @Override
            public void onFailure(int i) {
                Log.d(TAG, "Failure discover peers : " + i);
            }
        });
    }

    public void connectPeer(Channel channel, WifiP2pDevice device) {

        WifiP2pConfig config = new WifiP2pConfig();

        config.deviceAddress = device.deviceAddress;

        mManager.connect(channel, config, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Peers Connection Success");
            }

            @Override
            public void onFailure(int i) {
                Log.d(TAG, "Peers Connection Failure" + i);
            }
        });
    }
}
