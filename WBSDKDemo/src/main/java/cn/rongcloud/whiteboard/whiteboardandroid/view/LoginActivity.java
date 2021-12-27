package cn.rongcloud.whiteboard.whiteboardandroid.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import cn.rongcloud.whiteboard.whiteboardandroid.R;
import cn.rongcloud.whiteboard.whiteboardandroid.model.UserInfo;
import cn.rongcloud.whiteboard.whiteboardandroid.network.request.VerifyCodeRequestData;
import cn.rongcloud.whiteboard.whiteboardandroid.network.RequestCallback;
import cn.rongcloud.whiteboard.whiteboardandroid.network.request.LoginRequest;
import cn.rongcloud.whiteboard.whiteboardandroid.network.request.VerifyCodeRequest;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.LoginResponseData;
import cn.rongcloud.whiteboard.whiteboardandroid.network.request.LoginRequestData;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.ResponseData;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.LocalCacheClient;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.NetWorkClient;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.ToastUtils;

public class LoginActivity extends BaseFullScreenActivity implements View.OnClickListener {

    private static final long INTERVAL_DURATION = 1000;
    private static final long TOTAL_DURATION = 60 * INTERVAL_DURATION;
    private static final long PHONE_NUM_LIMIT = 11;

    private EditText mEtLoginPhone;
    private EditText mEtLoginVerifyCode;
    private Button mSendCodeBtn;
    private Button mBtnLogin;
    private CheckBox mLoginRegistrationTermsCheckBox;
    private TextView mLoginRegistrationTermsText;
    private boolean isTicking = false;

    private CountDownTimer countDownTimer = new CountDownTimer(TOTAL_DURATION, INTERVAL_DURATION) {

        @Override
        public void onTick(long millisUntilFinished) {
            isTicking = true;
            int count = Math.round(millisUntilFinished / INTERVAL_DURATION);
            mSendCodeBtn.setText(count + "");
        }

        @Override
        public void onFinish() {
            isTicking = false;
            mSendCodeBtn.setText(getText(R.string.wb_login_send_code_text));
            setSendCodeBtnEnabled(true);
        }
    };

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        mEtLoginPhone = (EditText) findViewById(R.id.et_login_phone);
        mEtLoginVerifyCode = (EditText) findViewById(R.id.et_login_verify_code);
        mSendCodeBtn = (Button) findViewById(R.id.et_login_send_verify_code);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mLoginRegistrationTermsCheckBox = (CheckBox) findViewById(R.id.login_registration_terms_check_box);
        mLoginRegistrationTermsText = (TextView) findViewById(R.id.login_registration_terms_text);

        String lastUserPhone = LocalCacheClient.getInstance().getPhone(this);
        if (!TextUtils.isEmpty(lastUserPhone)) {
            mEtLoginPhone.setText(lastUserPhone);
            setSendCodeBtnEnabled(true);
        } else {
            setSendCodeBtnEnabled(false);
        }

        String content = getString(R.string.login_agree_registration_terms_text);
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String url = "file:///android_asset/agreement_zh.html";
                WebActivity.start(LoginActivity.this, url);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                //设置文本的颜色
                ds.setColor(getResources().getColor(R.color.main_theme_color));
                //超链接形式的下划线，false 表示不显示下划线，true表示显示下划线
                ds.setUnderlineText(false);
            }
        }, 2, content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //设置点击事件，加上这句话才有效果
        mLoginRegistrationTermsText.setMovementMethod(LinkMovementMethod.getInstance());
        //设置点击后的颜色为透明（有默认背景）
        mLoginRegistrationTermsText.setHighlightColor(getResources().getColor(R.color.transparent));
        mLoginRegistrationTermsText.setText(spannableString);


        mEtLoginPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    setSendCodeBtnEnabled(true);
                } else {
                    setSendCodeBtnEnabled(false);
                }

                changeBtnLoginUI(s.length(), mEtLoginVerifyCode.getText().length());
            }
        });

        mEtLoginVerifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                changeBtnLoginUI(mEtLoginPhone.length(), s.length());
            }
        });

        mSendCodeBtn.setOnClickListener(this);

        mBtnLogin.setOnClickListener(this);
    }

    private void changeBtnLoginUI(int phoneLength, int codeLength) {
        if (phoneLength == PHONE_NUM_LIMIT && codeLength > 0) {
            mBtnLogin.setEnabled(true);
        } else {
            mBtnLogin.setEnabled(false);
        }
    }

    private void setSendCodeBtnEnabled(boolean isEnabled) {
        if (isTicking) {
            return;
        }
        if (isEnabled) {
            mSendCodeBtn.setTextColor(getResources().getColor(R.color.white));
        } else {
            mSendCodeBtn.setTextColor(getResources().getColor(R.color.color_8C000000));
        }
        mSendCodeBtn.setEnabled(isEnabled);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_login_send_verify_code:
                if (!NetWorkClient.getInstance().isNetworkConnected(this)) {
                    ToastUtils.showToast("请检查网络");
                    break;
                }

                if (!checkPhone()) {
                    break;
                }

                countDownTimer.start();
                setSendCodeBtnEnabled(false);
                requestVerifyCode();
                break;
            case R.id.btn_login:
                if (!checkPhone()) {
                    break;
                }

                if (!mLoginRegistrationTermsCheckBox.isChecked()) {
                    ToastUtils.showToast(getString(R.string.agree_read_regist_term_text));
                    break;
                }

                if (TextUtils.isEmpty(mEtLoginVerifyCode.getText())) {
                    ToastUtils.showToast(getString(R.string.tip_param_verify_code_empty_error));
                    break;
                }

                requestLogin();
                break;
            default:
                break;
        }
    }

    private void requestVerifyCode() {
        VerifyCodeRequestData data = new VerifyCodeRequestData("86", mEtLoginPhone.getText().toString());

        VerifyCodeRequest.create().params(data).request(new RequestCallback() {
            @Override
            public void onResponse(ResponseData responseData) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseData.code == 200) {
                            ToastUtils.showToast("发送验证码成功");
                        } else {
                            if (responseData.code == 5000) {
                                ToastUtils.showToast("验证码发送频繁,请稍后再试");
                            } else {
                                ToastUtils.showToast("发送验证码失败");
                            }
                        }
                    }
                });
            }
        });
    }

    private void requestLogin() {
        String phone = mEtLoginPhone.getText().toString();
        LoginRequestData loginRequestData = new LoginRequestData("86", phone, mEtLoginVerifyCode.getText().toString());
        LoginRequest.create().params(loginRequestData).request(new RequestCallback() {
            @Override
            public void onResponse(ResponseData responseData) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseData.code == 200) {
                            LoginResponseData data = (LoginResponseData) responseData;
                            ToastUtils.showToast(getString(R.string.login_success_toast));
                            UserInfo userInfo = data.getUserInfo();
                            LocalCacheClient.getInstance().saveUser(LoginActivity.this, new Gson().toJson(userInfo));
                            LocalCacheClient.getInstance().savePhone(LoginActivity.this, phone);
                            MainActivity.start(LoginActivity.this);
                            finish();
                        } else {
                            if (responseData.code == 1000) {
                                ToastUtils.showToast(getString(R.string.tip_param_verify_code_error));
                            } else if (responseData.code == 2000) {
                                ToastUtils.showToast(getString(R.string.tip_param_verify_code_expired_error));
                            } else {
                                ToastUtils.showToast(TextUtils.isEmpty(responseData.errorMessage) ? "登录失败" : responseData.errorMessage);
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * 检查输入的手机号
     *
     * @return
     */
    private boolean checkPhone() {
        if (TextUtils.isEmpty(mEtLoginPhone.getText())) {
            ToastUtils.showToast(getString(R.string.tip_param_input_phone));
            return false;
        }

        String phoneNum = mEtLoginPhone.getText().toString();
        if (phoneNum.length() != PHONE_NUM_LIMIT) {
            ToastUtils.showToast(getString(R.string.tip_valid_param_input_number));
            return false;
        }

        return true;
    }
}