package cn.rongcloud.whiteboard.whiteboardandroid.network;


import cn.rongcloud.whiteboard.whiteboardandroid.network.response.ResponseData;

/**
 * Created by andy on 2018/6/6.
 */

public interface Request {

    /**
     * 同步请求，返回的数据
     *
     * @return
     */
    ResponseData syncRequest();

    /**
     * 异步请求时的callback
     *
     * @param callback
     */
    void request(RequestCallback callback);
}
