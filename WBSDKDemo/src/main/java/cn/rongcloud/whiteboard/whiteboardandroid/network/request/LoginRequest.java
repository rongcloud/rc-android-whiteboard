package cn.rongcloud.whiteboard.whiteboardandroid.network.request;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import cn.rongcloud.whiteboard.whiteboardandroid.model.UserInfo;
import cn.rongcloud.whiteboard.whiteboardandroid.network.API;
import cn.rongcloud.whiteboard.whiteboardandroid.network.DemoServerRequest;
import cn.rongcloud.whiteboard.whiteboardandroid.network.OKHttpRequest;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.LoginResponseData;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.ResponseData;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by yanke on 2021/7/31
 */
public class LoginRequest extends DemoServerRequest<LoginRequestData> {

    private LoginRequestData requestData;

    private LoginRequest(String url) {
        super(url);
    }

    @Override
    public OKHttpRequest params(LoginRequestData data) {
        this.requestData = data;
        return this;
    }

    public static LoginRequest create() {
        return new LoginRequest(API.HOST.HOST + API.PATH.LOGIN);
    }

    @Override
    protected ResponseData generateSuccessResponse(String result) {
        try {
            LoginResponseData responseData = new LoginResponseData();
            UserInfo userInfo = new Gson().fromJson(result, UserInfo.class);
            responseData.setUserInfo(userInfo);
            return responseData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Request generateRequest() {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), new Gson().toJson(requestData)))
                .build();
        return request;
    }
}
