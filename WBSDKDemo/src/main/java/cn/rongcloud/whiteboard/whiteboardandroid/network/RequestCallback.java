package cn.rongcloud.whiteboard.whiteboardandroid.network;


import cn.rongcloud.whiteboard.whiteboardandroid.network.response.ResponseData;

/**
 * @author yanke
 */
public interface RequestCallback {

    void onResponse(ResponseData responseData);
}
