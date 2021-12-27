package cn.rongcloud.whiteboard.whiteboardandroid.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import cn.rongcloud.whiteboard.sdk.model.Role;
import cn.rongcloud.whiteboard.whiteboardandroid.R;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getCanonicalName();
    private ImageView mMainSettingIv;
    private ImageView mMainRecordIv;
    private RelativeLayout mAdminRoleLayout;
    private RelativeLayout mViewerRoleLayout;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "onCreate");

        initView();
    }

    private void initView() {
        mMainSettingIv = (ImageView) findViewById(R.id.main_setting_iv);
        mMainRecordIv = (ImageView) findViewById(R.id.main_video_iv);
        mAdminRoleLayout = (RelativeLayout) findViewById(R.id.main_white_board_admin_role_rl);
        mViewerRoleLayout = (RelativeLayout) findViewById(R.id.main_white_board_viewer_role_rl);

        mMainSettingIv.setOnClickListener(this);
        mMainRecordIv.setOnClickListener(this);
        mAdminRoleLayout.setOnClickListener(this);
        mViewerRoleLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_setting_iv:
                SettingActivity.start(MainActivity.this);
                break;
            case R.id.main_video_iv:
                VideoListActivity.start(MainActivity.this);
                break;
            case R.id.main_white_board_admin_role_rl:
                JoinRoomActivity.start(MainActivity.this, Role.PRESENTER);
                break;
            case R.id.main_white_board_viewer_role_rl:
                JoinRoomActivity.start(MainActivity.this, Role.VIEWER);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
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