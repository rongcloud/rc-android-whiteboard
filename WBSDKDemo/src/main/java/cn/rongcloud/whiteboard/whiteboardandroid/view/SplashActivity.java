package cn.rongcloud.whiteboard.whiteboardandroid.view;

import android.os.Bundle;
import android.text.TextUtils;

import cn.rongcloud.whiteboard.whiteboardandroid.utils.LocalCacheClient;

public class SplashActivity extends BaseFullScreenActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userCacheJson = LocalCacheClient.getInstance().getUser(SplashActivity.this);
        if (TextUtils.isEmpty(userCacheJson)) {
            LoginActivity.start(SplashActivity.this);
        } else {
            MainActivity.start(SplashActivity.this);
        }
        finish();
    }
}