package kr.ac.yjc.wdj.hikonnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import kr.ac.yjc.wdj.hikonnect.APIs.PermissionManager;

public class WifiP2pTestingActivity extends AppCompatActivity{

    final private String TAG = "WIfiP2pTestingActivity";

    private TextView textView;

    private WifiP2pManager wifiP2pManager;

    private Channel wifiP2pChannel;

    private PermissionManager pManager;

    private IntentFilter wifiIntentFilter;

    private BroadcastReceiver wReceiver;

    private WifiP2pDeviceList deviceList;

    private class FileTransfer extends AsyncTask<Void, Void, String> {

        private ServerSocket servSocket;

        private Socket socket;

        public FileTransfer() {
            try{
                servSocket = new ServerSocket(8888);

                socket = new Socket();

            } catch (IOException e) {
                Log.e("FileTransfer", e.getStackTrace().toString());
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            return null;
        }
    }

    private class WifiDirectBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d(TAG, action);

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // 와이파이의 상태가 변경되었을 때.

                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // 와이파이 p2p가 켜져 있을 때
                } else {
                    // 와이파이 p2p가 꺼져 있을 때
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // 네트워크 내부 디바이스들의 상태가 변경되었을 때.

                if (wifiP2pManager != null) {
                    wifiP2pManager.requestPeers(wifiP2pChannel, new WifiP2pManager.PeerListListener() {
                        @Override
                        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {

                            Log.d(TAG, "PeersAvailable!");

                            // 연결 가능한 디바이스들의 목록
                            deviceList = wifiP2pDeviceList;

                            Collection<WifiP2pDevice> devices = deviceList.getDeviceList();

                            Log.d(TAG, "Devices: " + devices);

                            textView.setText(devices.toString());
                        }
                    });
                    Log.d(TAG, "P2P peers list changed!");
                }

            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                //

            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                //
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_p2p_testing);

        textView = findViewById(R.id.textView);

        pManager = new PermissionManager(this);

        pManager.requestPermissions();

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "FCM token: " + token);

        wifiP2pManager      = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        wifiP2pChannel      = wifiP2pManager.initialize(this, getMainLooper(), null);

        // 와이파이 다이렉트를 위한 IntentFilter 초기화
        wifiIntentFilter   = new IntentFilter();
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        // Firebase 푸시 메시지
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();
    }

    public void onClickedDiscoverPeerBtn(View view) {
        wifiP2pManager.discoverPeers(wifiP2pChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(WifiP2pTestingActivity.this, "Success discover peer", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Success discover peer");

            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(WifiP2pTestingActivity.this, "Failure discover peer: " + i, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Failure discover peer: " + i);
            }
        });
    }

    public void onClickedConnectPeerBtn(View view) {

        WifiP2pConfig config = new WifiP2pConfig();

        if (deviceList == null) {
            Log.e(TAG, "deviceList is null");
            return;
        }

        ArrayList<WifiP2pDevice> devices = new ArrayList<>(deviceList.getDeviceList());

        config.deviceAddress    = devices.get(0).deviceAddress;
        config.wps.setup        = WpsInfo.PBC;

        wifiP2pManager.connect(wifiP2pChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Connected!!");
            }

            @Override
            public void onFailure(int i) {
                Log.d(TAG, "Connect Failed");
            }
        });
    }

    public void onClickedWipeTextViewBtn(View view) {
        textView.setText("");
    }

    public void onClickedSendDataBtn(View view) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        wReceiver = new WifiDirectBroadcastReceiver();

        registerReceiver(wReceiver, wifiIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 와이파이 다이렉트 Broadcast Receiver 등록 해제.
        unregisterReceiver(wReceiver);
    }
}
