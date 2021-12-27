package cn.rongcloud.whiteboard.whiteboardandroid.utils;

/**
 * Created by yanke on 2021/8/23
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import cn.rongcloud.whiteboard.whiteboardandroid.WBApplication;

/**
 * 网络连接变化 监听帮助类
 * <p>
 * 说明：
 * 1、静态注册广播监听网络变化 的方式，{@link ConnectivityManager#CONNECTIVITY_ACTION}已有说明，
 * 7.0及以后 静态注册的接收器不会收到 CONNECTIVITY_ACTION，只能用动态注册。（这是官方对广播权限的限制）
 * 2、5.0后有新的api{@link ConnectivityManager.NetworkCallback} ,但是只能在app 存活时监听到。和动态注册效果类似，但有更多细节的回调。
 * <p>
 * 综合这两点，本类实现方案：7.0及以后使用新api，只能在app存活时接收到回调；7.0以前使用静态注册广播。
 */
public class NetWorkChangeListenerHelper {

    private static final String TAG = NetWorkChangeListenerHelper.class.getCanonicalName();

    /**
     * 网络不可用
     */
    private static final int NETWORK_STATE_UNAVAILABLE = -1;

    /**
     * 网络可用
     */
    private static final int NETWORK_STATE_AVAILABLE = 0;

//    /**
//     * 网络可用，且是移动数据
//     */
//    private static final int NETWORK_STATE_AVAILABLE_MOBILE = 1;
//
//    /**
//     * 网络可用，且是wifi
//     */
//    private static final int NETWORK_STATE_AVAILABLE_WIFI = 2;

    private static NetworkChangeListener mNetworkChangeListener;

    public boolean hasRegistNetworkCallback() {
        return mNetworkChangeListener != null;
    }

    public void registerNetworkCallback(NetworkChangeListener networkChangeListener) {
        if (hasRegistNetworkCallback()) {
            Log.d(TAG, "hasRegistNetworkCallback");
            return;
        }

        mNetworkChangeListener = networkChangeListener;

        //7.0及以后 使用这个新的api（7.0以前还是用静态注册广播）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityManager connectivityManager = (ConnectivityManager) WBApplication.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
            // 请注意这里会有一个版本适配bug，所以请在这里添加非空判断
            if (connectivityManager != null) {
                NetworkRequest request = new NetworkRequest.Builder().build();
                connectivityManager.registerNetworkCallback(request, new AkuNetworkCallback());
            }

        }
    }

    private void handleOnNetworkChange(int networkState) {
        if (mNetworkChangeListener == null) {
            return;
        }
        switch (networkState) {
            case NETWORK_STATE_UNAVAILABLE:
                mNetworkChangeListener.onNetworkChange(false);
                break;
            case NETWORK_STATE_AVAILABLE:
                mNetworkChangeListener.onNetworkChange(true);
                break;
//            case NETWORK_STATE_AVAILABLE_WIFI:
//                mNetworkChangeListener.onNetworkChange(true);
//                break;
            default:
                break;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public class AkuNetworkCallback extends ConnectivityManager.NetworkCallback {

        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            Log.d(TAG, "网络连接了");
            handleOnNetworkChange(NETWORK_STATE_AVAILABLE);
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Log.d(TAG, "网络断开了");
            handleOnNetworkChange(NETWORK_STATE_UNAVAILABLE);
        }

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            //网络变化时，这个方法会回调多次
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.d(TAG, "wifi网络已连接");
//                    handleOnNetworkChange(NETWORK_STATE_AVAILABLE_WIFI);

                } else {
                    Log.d(TAG, "移动网络已连接");
//                    handleOnNetworkChange(NETWORK_STATE_AVAILABLE_MOBILE);
                }
            }
        }

    }

    public static class NetworkChangeBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //7.0以下用静态广播
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return;
            }
            if (intent == null) {
                return;
            }

            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                return;
            }

            if (mNetworkChangeListener == null) {
                return;
            }

            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            mNetworkChangeListener.onNetworkChange(!noConnectivity);
        }
    }

    /**
     * NetworkChangeListener
     */
    public interface NetworkChangeListener {
        void onNetworkChange(boolean isNetworkAvailable);
    }
}

