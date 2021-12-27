package cn.rongcloud.whiteboard.whiteboardandroid.network.request;

import java.io.Serializable;

/**
 * Created by yanke on 2021/8/2
 */
public class LoginRequestData implements Serializable {
    private String region;
    private String phone;
    private String code;

    public LoginRequestData(String region, String phone, String code) {
        this.region = region;
        this.phone = phone;
        this.code = code;
    }
}
