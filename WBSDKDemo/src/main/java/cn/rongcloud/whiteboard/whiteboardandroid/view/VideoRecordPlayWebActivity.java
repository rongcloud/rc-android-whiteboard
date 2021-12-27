package cn.rongcloud.whiteboard.whiteboardandroid.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import cn.rongcloud.whiteboard.sdk.IRCWBCallback;
import cn.rongcloud.whiteboard.sdk.RCWhiteBoard;
import cn.rongcloud.whiteboard.sdk.widget.WBWebView;
import cn.rongcloud.whiteboard.sdk.dsbridge.DWebView;
import cn.rongcloud.whiteboard.sdk.model.IRCWBEnum;
import cn.rongcloud.whiteboard.sdk.model.PermissionType;
import cn.rongcloud.whiteboard.sdk.model.PlayConfig;
import cn.rongcloud.whiteboard.whiteboardandroid.BuildConfig;
import cn.rongcloud.whiteboard.whiteboardandroid.R;
import cn.rongcloud.whiteboard.whiteboardandroid.WBApplication;
import cn.rongcloud.whiteboard.whiteboardandroid.model.UserInfo;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.LocalCacheClient;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.ToastUtils;

public class VideoRecordPlayWebActivity extends BaseFullScreenActivity {

    private static final String TAG = VideoRecordPlayWebActivity.class.getCanonicalName();
    private WBWebView wbWebView;
    private ProgressBar progressBar;
    static String paramUrl;
    static String playHubId;
    private RCWhiteBoard client;

    public static void start(Context context, String url, String hubId) {
        paramUrl = url;
        playHubId = hubId;
        Intent intent = new Intent(context, VideoRecordPlayWebActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record_play_web);
        initView();
        requestPlayUrl();
        initListener();
    }

    private void initView() {
        wbWebView = (WBWebView) findViewById(R.id.video_record_play_web_view);
        progressBar = (ProgressBar) findViewById(R.id.video_record_play_progress_bar);
        DWebView.setWebContentsDebuggingEnabled(true);
        wbWebView.setWebChromeClient(new ProgressWebChromeClient());
    }

    private void requestPlayUrl() {
        client = new RCWhiteBoard();
        client.init(this, wbWebView);

        UserInfo userInfo = LocalCacheClient.getInstance().getUserInfo(WBApplication.getApplication());
        if (userInfo == null) {
            return;
        }

        PlayConfig playConfig = new PlayConfig(BuildConfig.APP_KEY, userInfo.getToken(), paramUrl, userInfo.getNickName(), userInfo.getUserId(), playHubId);

        showLoadingDialog();
        client.playVideo(playConfig, new IRCWBCallback.GetVideoPlayUrlCallback() {
            @Override
            public void onSuccess() {
                dismissLoadingDialog();
            }

            @Override
            public void onError(IRCWBEnum.ErrorCode errorCode) {
                dismissLoadingDialog();
                ToastUtils.showToast("获取白板录像播放地址失败");
            }
        });
    }


    class ProgressWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    private void initListener() {
        client.setPermissionChangeListener(new IRCWBCallback.PermissionChangeListener() {
            @Override
            public void onPermissionChange(PermissionType type) {
            }
        });

        client.setRoomStatusListener(new IRCWBCallback.RoomStatusListener() {
            @Override
            public void onRoomQuit() {
                finish();
            }

            @Override
            public void onRoomException(int code, String message) {
                ToastUtils.showToast(message);
                finish();
            }

            @Override
            public void onCaptureEnd(Bitmap bitmap) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wbWebView != null) {
            wbWebView.destroy();
        }
        Log.e(TAG, "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.e(TAG, "onSaveInstanceState");
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(TAG, "onRestoreInstanceState");
    }
}