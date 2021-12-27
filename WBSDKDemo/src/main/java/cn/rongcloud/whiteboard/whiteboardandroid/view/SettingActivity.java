package cn.rongcloud.whiteboard.whiteboardandroid.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import cn.rongcloud.whiteboard.whiteboardandroid.R;
import cn.rongcloud.whiteboard.whiteboardandroid.model.UserInfo;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.LocalCacheClient;
import cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.StatusBarUtil;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mSettingReturnIv;
    private TextView mTxSettingAccount;
    private TextView mTxSettingName;
    private Button mBtnLogout;
    private static final int REQUEST_CODE = 100;

    public static void start(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        mSettingReturnIv = (ImageView) findViewById(R.id.setting_return_iv);
        mTxSettingAccount = (TextView) findViewById(R.id.tx_setting_account);
        mTxSettingName = (TextView) findViewById(R.id.tx_setting_name);
        mBtnLogout = (Button) findViewById(R.id.btn_logout);

        String userCacheInfo = LocalCacheClient.getInstance().getUser(SettingActivity.this);
        String phone = LocalCacheClient.getInstance().getPhone(SettingActivity.this);
        UserInfo userInfo = new Gson().fromJson(userCacheInfo, UserInfo.class);
        mTxSettingAccount.setText(phone);
        mTxSettingName.setText(userInfo.getNickName());
        mTxSettingName.setOnClickListener(this);
        mSettingReturnIv.setOnClickListener(this);
        mBtnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                new AlertDialog.Builder(this)
                        .setMessage("确定退出登录吗?")
                        .setCancelable(true)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LocalCacheClient.getInstance().clearUserCache(SettingActivity.this);
                                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).create().show();
                break;
            case R.id.setting_return_iv:
                finish();
                break;
            case R.id.tx_setting_name:
                Intent intent = new Intent(SettingActivity.this, UpdateNameActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null && !TextUtils.isEmpty(data.getStringExtra(UpdateNameActivity.EXTRA_KEY))) {
            String newName = data.getStringExtra(UpdateNameActivity.EXTRA_KEY);
            mTxSettingName.setText(newName);
        }
    }
}