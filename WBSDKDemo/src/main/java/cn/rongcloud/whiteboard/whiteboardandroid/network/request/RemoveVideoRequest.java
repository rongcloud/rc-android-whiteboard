package cn.rongcloud.whiteboard.whiteboardandroid.network.request;

import cn.rongcloud.whiteboard.whiteboardandroid.network.API;
import cn.rongcloud.whiteboard.whiteboardandroid.network.HubServerRequest;
import cn.rongcloud.whiteboard.whiteboardandroid.network.OKHttpRequest;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.ResponseData;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by yanke on 2021/8/4
 */
public class RemoveVideoRequest extends HubServerRequest<RemoveVideoRequestData> {

    private RemoveVideoRequestData requestData;

    public RemoveVideoRequest(String url) {
        super(url);
    }

    public static RemoveVideoRequest create() {
        return new RemoveVideoRequest(API.HOST.HOST + API.PATH.REMOVE_VIDEO);
    }

    @Override
    public OKHttpRequest params(RemoveVideoRequestData data) {
        requestData = data;
        return this;
    }

    @Override
    protected ResponseData generateSuccessResponse(String result) {
        return null;
    }

    @Override
    protected Request generateRequest() {
        FormBody formBody = new FormBody.Builder().addEncoded("recordIds", requestData.id).build();
        return new okhttp3.Request.Builder()
                .url(String.format(url, requestData.id))
                .addHeader("Content-Type", "x-www-form-urlencoded;charset=utf-8")
                .delete(formBody)
                .build();
    }

    private String appendUrl() {
        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();

        if (requestData != null) {
            builder.addQueryParameter("id", String.valueOf(requestData.id));
        }
        return builder.build().toString();
    }
}
