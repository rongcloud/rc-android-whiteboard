package cn.rongcloud.whiteboard.whiteboardandroid.network.request;


import java.io.Serializable;

/**
 * Created by yanke on 2021/8/2
 *
 * @author yanke
 */
public class VerifyCodeRequestData implements Serializable {
    public String region;
    public String phone;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public VerifyCodeRequestData(String region, String phone) {
        this.region = region;
        this.phone = phone;
    }
}
