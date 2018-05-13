package kr.ac.yjc.wdj.hikonnect.apis.file_transfer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author  Beomsu Kwon
 * @since   2018-04
 */
public class FTServerAsyncTask extends AsyncTask {

    private final String TAG = "FTServerAsyncTask";

    private Context context;

    public FTServerAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = serverSocket.accept();

            final File f = new File(Environment.getExternalStorageDirectory() + "/"
                    + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                    + ".jpg");

            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdir();
            f.createNewFile();
            InputStream is = client.getInputStream();
//            copyFile(is, new FileOutputStream(f));
            serverSocket.close();
            return f.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG,  e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        if (o != null) {
            Log.d(TAG, o.toString());
        }
    }
}
