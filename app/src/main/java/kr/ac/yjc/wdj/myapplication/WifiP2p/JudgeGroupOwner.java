package kr.ac.yjc.wdj.myapplication.WifiP2p;

import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;


// Whether this device is a group owner
public class JudgeGroupOwner {
    private boolean                 isGroupOwner;
    private InetAddress             groupOwnerAddress;
    private Socket                  groupOwnerSocket;
    private BufferedReader          messageReader;
    private PrintWriter             messageWriter;
    private String                  host;
    private int                     port;
    private int                     SOCKET_TIMEOUT;
    private static final String     TAG                 =   "CommunicateWithServer";

    public void CommunicateWithServer(WifiP2pInfo mInfo) {
        if (mInfo.isGroupOwner == true) {

            groupOwnerSocket    =   new Socket();
            host                =   mInfo.groupOwnerAddress.getHostAddress();
            port                =   8888;
            SOCKET_TIMEOUT      =   5000;

            try {
                groupOwnerSocket.bind(null);
                //groupOwnerSocket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                groupOwnerSocket.connect(new InetSocketAddress(host, port));

                messageReader       =   new BufferedReader(new InputStreamReader(groupOwnerSocket.getInputStream()));
                messageWriter       =   new PrintWriter(groupOwnerSocket.getOutputStream());


            } catch (Exception e) {
                Log.e(TAG, "Socket Connection Failed");
            } finally {
                if (groupOwnerSocket != null) {
                    try {
                        groupOwnerSocket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Log.d(TAG, "This device is client");
        }
    }
}
