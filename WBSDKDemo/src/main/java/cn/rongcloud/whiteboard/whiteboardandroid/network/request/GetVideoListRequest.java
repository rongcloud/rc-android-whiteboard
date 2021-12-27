package cn.rongcloud.whiteboard.whiteboardandroid.network.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cn.rongcloud.whiteboard.whiteboardandroid.model.ItemVideoInfo;
import cn.rongcloud.whiteboard.whiteboardandroid.network.API;
import cn.rongcloud.whiteboard.whiteboardandroid.network.HubServerRequest;
import cn.rongcloud.whiteboard.whiteboardandroid.network.OKHttpRequest;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.GetVideoListResponseData;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.ResponseData;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by yanke on 2021/8/3
 */
public class GetVideoListRequest extends HubServerRequest<GetVideoListRequestData> {

    private GetVideoListRequestData requestData;

    public static GetVideoListRequest create() {
        return new GetVideoListRequest(API.HOST.HOST + API.PATH.VIDEO_LIST);
    }

    public GetVideoListRequest(String url) {
        super(url);
    }

    @Override
    protected ResponseData generateSuccessResponse(String result) {
        try {
            GetVideoListResponseData responseData = new GetVideoListResponseData();
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has("hubRecords")) {
                JSONArray hubRecordsJsonArray = jsonObject.optJSONArray("hubRecords");
                String hubRecordListJson = hubRecordsJsonArray == null ? null : hubRecordsJsonArray.toString();
                Type type = new TypeToken<List<ItemVideoInfo>>() {
                }.getType();
                List<ItemVideoInfo> videoInfoList = new Gson().fromJson(hubRecordListJson, type);
                responseData.setVideoInfoList(videoInfoList);
            }
            return responseData;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Request generateRequest() {
        return new okhttp3.Request.Builder()
                .url(appendUrl())
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .get()
                .build();
    }

    private String appendUrl() {
        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();

        if (requestData != null) {
            builder.addQueryParameter("offset", String.valueOf(requestData.offset));
            builder.addQueryParameter("rsTime", String.valueOf(requestData.rsTime));
            builder.addQueryParameter("reTime", String.valueOf(requestData.reTime));
        }
        return builder.build().toString();
    }


    @Override
    public OKHttpRequest params(GetVideoListRequestData data) {
        this.requestData = data;
        return this;
    }
}
