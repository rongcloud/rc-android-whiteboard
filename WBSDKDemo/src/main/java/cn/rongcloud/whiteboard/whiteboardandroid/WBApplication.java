package cn.rongcloud.whiteboard.whiteboardandroid;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.rongcloud.whiteboard.sdk.net.SSLUtils;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.NetWorkClient;
import cn.rongcloud.whiteboard.whiteboardandroid.view.SplashActivity;

/**
 * Created by yanke on 2021/7/31
 */
public class WBApplication extends Application {

    private static WBApplication appInstance;
    private static String currentProcessName;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static WBApplication getApplication() {
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;

        SSLUtils.setSSLContext(getSSLContext());
        SSLUtils.setHostnameVerifier(SSLUtils.getHostVerifier());

        if (!getApplicationInfo().packageName.equals(getCurrentProcessName(getApplicationContext()))) {
            return;
        }


        observeAppInBackground();
    }

    /**
     * 监听应用是否转为后台
     */
    private void observeAppInBackground() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                //解决：切后台关闭应用某项权限,进程被杀,重启应用因SDK还未绑定IPC进程,
                // 导致当前恢复页面调用SDK API接口返回IPC_DISCONNECT,页面白屏或黑屏现象
                if (savedInstanceState != null) {
                    Intent intent = new Intent(activity, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    /**
     * @param context 上下文
     * @return 当前进程名
     */
    public static String getCurrentProcessName(Context context) {
        if (context == null) {
            return currentProcessName;
        }
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName;
        }

        //1)通过 Application 的 API 获取当前进程名
        currentProcessName = getCurrentProcessNameByApplication();
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName;
        }

        //2)通过反射 ActivityThread 获取当前进程名
        currentProcessName = getCurrentProcessNameByActivityThread();
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName;
        }

        //3)通过 ActivityManager 获取当前进程名
        currentProcessName = getCurrentProcessNameByActivityManager(context);
        return currentProcessName;
    }

    /**
     * 通过 Application 新的 API 获取进程名，无需反射，无需 IPC，效率最高。
     */
    private static String getCurrentProcessNameByApplication() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName();
        }
        return null;
    }

    /**
     * 通过反射 ActivityThread 获取进程名，避免了 ipc
     */
    private static String getCurrentProcessNameByActivityThread() {
        String processName = null;
        try {
            final Method declaredMethod = Class.forName("android.app.ActivityThread", false, Application.class.getClassLoader())
                    .getDeclaredMethod("currentProcessName", new Class[0]);
            declaredMethod.setAccessible(true);
            final Object invoke = declaredMethod.invoke(null, new Object[0]);
            if (invoke instanceof String) {
                processName = (String) invoke;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return processName;
    }


    /**
     * 通过 ActivityManager 获取进程名，需要 IPC 通信
     */
    private static String getCurrentProcessNameByActivityManager(Context context) {
        if (context == null) {
            return null;
        }
        int pid = Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> runningAppList = am.getRunningAppProcesses();
            if (runningAppList != null) {
                for (ActivityManager.RunningAppProcessInfo processInfo : runningAppList) {
                    if (processInfo.pid == pid) {
                        return processInfo.processName;
                    }
                }
            }
        }
        return null;
    }

    public SSLContext getSSLContext() {
        SSLContext sslContext = null;
        try {
            //使用X509TrustManager代替CertificateTrustManager跳过证书验证。
            TrustManager tm[] = {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            Log.d("checkClientTrusted", "authType:" + authType);
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            Log.d("checkServerTrusted", "authType:" + authType);
                            try {
                                chain[0].checkValidity();
                            } catch (Exception e) {
                                Log.e("checkServerTrusted", "Exception", e);
                            }
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tm, null);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
        return sslContext;
    }

    public HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

}
