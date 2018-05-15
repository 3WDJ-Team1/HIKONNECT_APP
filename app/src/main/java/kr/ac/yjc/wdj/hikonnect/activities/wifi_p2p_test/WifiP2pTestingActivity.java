package kr.ac.yjc.wdj.hikonnect.activities.wifi_p2p_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;

import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.apis.PermissionManager;
import kr.ac.yjc.wdj.hikonnect.apis.file_transfer.FTClientAsyncTask;
import kr.ac.yjc.wdj.hikonnect.apis.file_transfer.FTServerAsyncTask;

public class WifiP2pTestingActivity extends AppCompatActivity
        implements WifiP2pManager.ActionListener{

    final private String TAG = "WIfiP2pTestingActivity";

    private TextView textView;

    private RecyclerView deviceListView;

    private WifiP2pManager wifiP2pManager;

    private Channel wifiP2pChannel;

    private PermissionManager pManager;

    private IntentFilter wifiIntentFilter;

    private BroadcastReceiver wReceiver;

    private WifiP2pDeviceList deviceList;

    @Override
    public void onSuccess() {
        Log.d(TAG, "Action Success");
    }

    @Override
    public void onFailure(int i) {
        Log.e(TAG, "Action Failure Error Code: " + i);
    }

    private class WifiDirectBroadcastReceiver extends BroadcastReceiver
            implements
            WifiP2pManager.PeerListListener,
            WifiP2pManager.ConnectionInfoListener,
            WifiP2pManager.GroupInfoListener{

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
                    wifiP2pManager.requestPeers(wifiP2pChannel, this);
                    wifiP2pManager.requestGroupInfo(wifiP2pChannel, this);
                    Log.d(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION");
                }

            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                if (wifiP2pManager == null) {
                    return;
                }

                NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                
                if (networkInfo.isConnected()) {
                    Toast.makeText(context, "Connected!", Toast.LENGTH_SHORT).show();

                    wifiP2pManager.requestConnectionInfo(wifiP2pChannel, this);
                } else {
                    Toast.makeText(context, "Connection Failed!", Toast.LENGTH_SHORT).show();
                }
                

            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                //
            }
        }

        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {

            // 연결 가능한 디바이스들의 목록
            deviceList = wifiP2pDeviceList;

            Collection<WifiP2pDevice> devices = deviceList.getDeviceList();

            String deviceStatus = "";

            for (WifiP2pDevice device : devices) {
                deviceStatus += "Device Name    : " + device.deviceName + "\n";
                deviceStatus += "Device address : " + device.deviceAddress + "\n";
                deviceStatus += "Device status  : " + device.status + "\n";
                deviceStatus += "-----------\n";
            }

            textView.setText(deviceStatus);
        }

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            InetAddress targetIP = null;

            if(!wifiP2pInfo.isGroupOwner) {
                targetIP = wifiP2pInfo.groupOwnerAddress;

                Log.d(TAG, "Group owner's IP: " + targetIP);

                Button connectBtn = (Button) findViewById(R.id.connectPeerBtn);

                connectBtn.setBackgroundColor(Color.CYAN);
            }
        }

        @Override
        public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
            if(wifiP2pGroup == null) {
                return;
            }

            ArrayList clients = new ArrayList<>(wifiP2pGroup.getClientList());

            Log.d("GroupInfo", wifiP2pGroup.toString());
            Log.d("Clients", clients.toString());
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

        textView.setText("...");

        wifiP2pManager.discoverPeers(wifiP2pChannel, this);
    }

    public void onClickedConnectPeerBtn(View view) {

        try {
            WifiP2pConfig config = new WifiP2pConfig();

            if (deviceList == null) {
                Log.e(TAG, "deviceList is null");
                return;
            }

            ArrayList<WifiP2pDevice> devices = new ArrayList<>(deviceList.getDeviceList());

            config.deviceAddress = devices.get(0).deviceAddress;

            config.wps.setup = WpsInfo.PBC;

            wifiP2pManager.connect(wifiP2pChannel, config, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickedSendDataBtn(View view) {
        if (deviceList == null) {
            Log.e(TAG, "device list is null");
        }

        ArrayList<WifiP2pDevice> devices = new ArrayList<>(deviceList.getDeviceList());
        WifiP2pDevice device =  devices.get(0);

        FTServerAsyncTask serverTask = new FTServerAsyncTask(this);
        FTClientAsyncTask clientTask = new FTClientAsyncTask(this, device.deviceAddress, 8888, 0);

        try {
            serverTask.execute();

            clientTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "send start");
    }

    public void onClickedWipeTextViewBtn(View view) {
        textView.setText("");
    }

    public void onClickedDisconnectBtn(View view) {
        wifiP2pManager.cancelConnect(wifiP2pChannel, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        wReceiver = new WifiP2pTestingActivity.WifiDirectBroadcastReceiver();

        registerReceiver(wReceiver, wifiIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 와이파이 다이렉트 Broadcast Receiver 등록 해제.
        unregisterReceiver(wReceiver);
    }
}
