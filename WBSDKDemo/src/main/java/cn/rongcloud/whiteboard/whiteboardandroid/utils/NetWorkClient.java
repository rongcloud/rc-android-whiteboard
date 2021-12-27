package cn.rongcloud.whiteboard.whiteboardandroid.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by yanke on 2021/8/23
 */
public class NetWorkClient {
    private static final NetWorkClient ourInstance = new NetWorkClient();

    public static NetWorkClient getInstance() {
        return ourInstance;
    }

    private NetWorkClient() {
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
