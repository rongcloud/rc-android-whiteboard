package cn.rongcloud.whiteboard.whiteboardandroid.network.response;

public class NetworkResponseData extends ResponseData {

    public NetworkResponseData() {
        errorMessage = "网络错误，请检查您的网络重试";
        httpCode = 500;
        code = 500;
    }
}
