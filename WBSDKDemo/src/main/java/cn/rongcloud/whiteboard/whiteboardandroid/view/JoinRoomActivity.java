package cn.rongcloud.whiteboard.whiteboardandroid.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import cn.rongcloud.whiteboard.sdk.model.Role;
import cn.rongcloud.whiteboard.whiteboardandroid.R;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.NetWorkClient;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.ToastUtils;

public class JoinRoomActivity extends BaseActivity {

    private static final String TAG = JoinRoomActivity.class.getCanonicalName();
    private ImageView mReturnJoinRoomIv;
    private EditText mEtRoomNum;
    private Button mBtnJoinRoom;
    private static Role mRole;
    private TextView roleTip;
    private int ROOM_ID_MAX_LENGTH = 6;

    public static void start(Context context, Role role) {
        mRole = role;
        Intent intent = new Intent(context, JoinRoomActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);
        initView();
        Log.e(TAG, "onCreate");
    }

    private void initView() {
        roleTip = (TextView) findViewById(R.id.tx_role);
        mReturnJoinRoomIv = (ImageView) findViewById(R.id.return_join_room_iv);
        mEtRoomNum = (EditText) findViewById(R.id.et_room_num);
        mBtnJoinRoom = (Button) findViewById(R.id.btn_join_room);
        mBtnJoinRoom.setEnabled(false);

        String title = "";
        switch (mRole) {
            case PRESENTER:
                title = "演示者";
                break;
            case VIEWER:
                title = "观看者";
                break;
            default:
                break;
        }
        roleTip.setText(String.format(getString(R.string.role_tip_test), title));

        mEtRoomNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == ROOM_ID_MAX_LENGTH) {
                    mBtnJoinRoom.setEnabled(true);
                } else {
                    mBtnJoinRoom.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBtnJoinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetWorkClient.getInstance().isNetworkConnected(JoinRoomActivity.this)) {
                    ToastUtils.showToast("请检查网络");
                    return;
                }
                RoomActivity.start(JoinRoomActivity.this, mEtRoomNum.getText().toString(), mRole);
            }
        });
        mReturnJoinRoomIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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