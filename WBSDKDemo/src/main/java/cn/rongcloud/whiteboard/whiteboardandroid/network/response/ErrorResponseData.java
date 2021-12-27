package cn.rongcloud.whiteboard.whiteboardandroid.network.response;

public class ErrorResponseData extends ResponseData {

    public ErrorResponseData(int c, String m) {
        this.code = c;
        this.errorMessage = m;
        this.httpCode = 400;
    }
}
