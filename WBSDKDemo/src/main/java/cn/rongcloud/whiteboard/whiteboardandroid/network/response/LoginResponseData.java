package cn.rongcloud.whiteboard.whiteboardandroid.network.response;


import cn.rongcloud.whiteboard.whiteboardandroid.model.UserInfo;

/**
 * Created by yanke on 2021/8/2
 */
public class LoginResponseData extends ResponseData {
    private UserInfo userInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
