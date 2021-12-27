package cn.rongcloud.whiteboard.whiteboardandroid.network.request;

import android.util.Log;

import com.google.gson.Gson;

import cn.rongcloud.whiteboard.whiteboardandroid.network.API;
import cn.rongcloud.whiteboard.whiteboardandroid.network.DemoServerRequest;
import cn.rongcloud.whiteboard.whiteboardandroid.network.OKHttpRequest;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.ResponseData;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by yanke on 2021/8/2
 */
public class VerifyCodeRequest extends DemoServerRequest<VerifyCodeRequestData> {

    private static final String TAG = VerifyCodeRequest.class.getSimpleName();
    private VerifyCodeRequestData verifyCodeRequestData;

    public VerifyCodeRequest(String url) {
        super(url);
    }

    public static VerifyCodeRequest create() {
        return new VerifyCodeRequest(API.HOST.HOST + API.PATH.VERIFY_CODE);
    }

    @Override
    protected ResponseData generateSuccessResponse(String result) {
        return null;
    }

    @Override
    protected Request generateRequest() {
        Log.d(TAG, "url=" + url);
        Log.d(TAG, "verify code bean phone=" + verifyCodeRequestData.getPhone() + "  Region=" + verifyCodeRequestData.getRegion());
        String json = new Gson().toJson(verifyCodeRequestData);
        Log.d(TAG, "verify code param json=" + json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json))
                .build();
        return request;
    }

    @Override
    public OKHttpRequest params(VerifyCodeRequestData data) {
        verifyCodeRequestData = data;
        return this;
    }
}
