package kr.ac.yjc.wdj.hikonnect.apis.file_transfer;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class FTClientAsyncTask extends AsyncTask {

    private final String TAG = "FTClientAsyncTask";

    private Context context;

    private String host;

    private int port;

    private int len;

    private Socket socket;

    private byte buf[];

    public FTClientAsyncTask(Context context, String host, int port, int len) {
        this.context    = context;
        this.host       = host;
        this.port       = port;
        this.len        = len;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            socket = new Socket();
            buf = new byte[1024];

            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), 500);

            OutputStream os     = socket.getOutputStream();
            ContentResolver cr  = context.getContentResolver();
            InputStream is      = null;
            is = cr.openInputStream(Uri.parse("path/picture.jpg"));
            while ((len = is.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
            os.close();
            is.close();
        } catch(FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch(IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }
        return objects;
    }
}
