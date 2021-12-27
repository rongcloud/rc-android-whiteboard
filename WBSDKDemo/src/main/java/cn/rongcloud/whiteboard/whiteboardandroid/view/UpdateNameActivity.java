package cn.rongcloud.whiteboard.whiteboardandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import cn.rongcloud.whiteboard.whiteboardandroid.R;
import cn.rongcloud.whiteboard.whiteboardandroid.model.UserInfo;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.LocalCacheClient;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.ToastUtils;

public class UpdateNameActivity extends Activity implements View.OnClickListener {


    public static final String EXTRA_KEY = "newName";

    private EditText mEtUpdateName;
    private TextView mTxSaveName;
    private String originalUserName;
    private UserInfo userInfo;
    private ImageView mReturnIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_name);
        initView();
    }

    private void initView() {
        mTxSaveName = (TextView) findViewById(R.id.tx_save_account_name);
        mEtUpdateName = (EditText) findViewById(R.id.et_update_name);
        mReturnIv = (ImageView) findViewById(R.id.return_iv);

        userInfo = LocalCacheClient.getInstance().getUserInfo(this);
        originalUserName = userInfo.getNickName();
        mEtUpdateName.setText(originalUserName);

        mEtUpdateName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String edit = mEtUpdateName.getText().toString();
                String str = stringFilter(edit);
                if (!edit.equals(str)) {
                    mEtUpdateName.setText(str);
                    //设置新的光标所在位置
                    mEtUpdateName.setSelection(str.length());

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mTxSaveName.setOnClickListener(this);
        mReturnIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_iv:
                returnPvious();
                break;
            case R.id.tx_save_account_name:
                String newName = mEtUpdateName.getText().toString();
                if (TextUtils.isEmpty(newName)) {
                    ToastUtils.showToast("昵称不能为空");
                    break;
                }
                userInfo.setNickName(newName);
                LocalCacheClient.getInstance().saveUser(UpdateNameActivity.this, new Gson().toJson(userInfo));
                ToastUtils.showToast("保存成功");
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        returnPvious();
    }

    private void returnPvious() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_KEY, mEtUpdateName.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    public String stringFilter(String str) throws PatternSyntaxException {
        // 只允许字母、数字和汉字其余的还可以随时添加比如下划线什么的，但是注意引文符号和中文符号区别
        String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";//正则表达式
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}