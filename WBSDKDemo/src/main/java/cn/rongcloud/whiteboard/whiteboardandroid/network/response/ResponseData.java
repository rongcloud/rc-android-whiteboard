package cn.rongcloud.whiteboard.whiteboardandroid.network.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by andy on 2018/6/6.
 */

public class ResponseData implements Serializable {

    /**
     * 如果请求失败，可能返回错误信息，对应的错误信息提示
     */
    @SerializedName(value = "msg", alternate = {"desc", "message"})
    public String errorMessage;

    /**
     * 返回的http的code
     */
    public int httpCode;

    /**
     * 返回的数据结构类型
     */
    @SerializedName("result")
    public Object result;

    /**
     * 虽然返回的httpcode是200，但是有可能有其他的错误提示
     */
    @SerializedName("code")
    public int code;
}
