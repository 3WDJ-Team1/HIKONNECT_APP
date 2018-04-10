package kr.ac.yjc.wdj.hikonnect.APIs.WifiP2p;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

import java.util.ArrayList;

public class WifiP2pBroadCastReceiver extends BroadcastReceiver {

    static protected String TAG = "WifiP2pBroadCastReceiver";

    protected WifiP2pManager mManager;

    protected WifiP2pController mController;

    protected Channel mChannel;

    protected Activity mActivity;

    protected ArrayList<WifiP2pDevice> devices;

    public WifiP2pBroadCastReceiver(WifiP2pManager manager, Channel channel, Activity activity) {
        super();

        this.mManager = manager;
        this.mController = new WifiP2pController(mManager);
        this.mChannel = channel;
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // 와이파이의 상태가 변경되었을 때.

            Log.d(TAG, "onReceive: WIFI_P2P_STATE_CHANGED");

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // 와이파이 p2p가 켜져 있을 때
                Log.d(TAG, "onReceive: WIFI_P2P_STATE_ENABLED");
            } else {
                // 와이파이 p2p가 꺼져 있을 때
                Log.d(TAG, "onReceive: WIFI_P2P_STATE_DISABLED");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // 네트워크 내부 디바이스들의 상태가 변경되었을 때.
            Log.d(TAG, "onReceive: WIFI_P2P_PEERS_CHANGED");

            if (mManager != null) {
                mManager.requestPeers(mChannel, new PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {

                        // 연결 가능한 디바이스들의 목록
                        devices = new ArrayList<>(wifiP2pDeviceList.getDeviceList());
                        Log.d(TAG, "Devices: " + devices);
                    }
                });
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            //
            Log.d(TAG, "onReceive: WIFI_P2P_CONNECTION_CHANGED");

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            //
            Log.d(TAG, "onReceive: WIFI_P2P_THIS_DEVICE_CHANGED");
        }
    }
}
