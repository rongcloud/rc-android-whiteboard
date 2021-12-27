package cn.rongcloud.whiteboard.whiteboardandroid.network;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.rongcloud.whiteboard.whiteboardandroid.network.response.ErrorResponseData;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.NetworkResponseData;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.RequestErrorCodeMap;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.ResponseData;
import okhttp3.Request;

/**
 * Created by yanke on 2021/8/3
 */
public abstract class DemoServerRequest<T> extends OKHttpRequest<T> {
    public DemoServerRequest(String url) {
        super(url);
    }

    @Override
    protected ResponseData constructErrorResponse(String result) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) {
            return new NetworkResponseData();
        } else {
            if (jsonObject.has("code")) {
                int code = jsonObject.optInt("code");
                String message = RequestErrorCodeMap.ErrorCode.valueOf(code).msg;
                if (TextUtils.isEmpty(message)) {
                    if (jsonObject.has("message")) {
                        message = jsonObject.optString("message");
                    } else {
                        message = "未知错误";
                    }
                }
                return new ErrorResponseData(code, message);
            } else {
                return new ErrorResponseData(-1, "未知错误");
            }
        }
    }

    @Override
    protected final ResponseData constructSuccessResponse(String result) {
        ResponseData responseData = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            responseData = new ResponseData();
            if (jsonObject.has("result")) {
                ResponseData newResponseData = generateSuccessResponse(jsonObject.optString("result"));
                if (newResponseData != null) {
                    responseData = newResponseData;
                }
            }
            if (jsonObject.has("code")) {
                responseData.code = jsonObject.optInt("code");
            }
            if (jsonObject.has("message")) {
                responseData.errorMessage = jsonObject.optString("message");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return responseData == null ? new ResponseData() : responseData;
    }

    @Override
    protected final Request constructRequest() {
        return generateRequest();
    }

    protected abstract ResponseData generateSuccessResponse(String result);

    protected abstract Request generateRequest();

}
