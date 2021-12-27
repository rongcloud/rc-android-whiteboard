package cn.rongcloud.whiteboard.whiteboardandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import cn.rongcloud.whiteboard.whiteboardandroid.network.response.ResponseData;

/**
 * Created by yanke on 2021/7/31
 */
public class UserInfo extends ResponseData implements Parcelable {

    @SerializedName("nickName")
    private String nickName;
    @SerializedName("id")
    private String userId;
    @SerializedName("token")
    private String token;

    protected UserInfo(Parcel in) {
        nickName = in.readString();
        userId = in.readString();
        token = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nickName);
        dest.writeString(userId);
        dest.writeString(token);
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "nickName='" + nickName + '\'' +
                ", userId='" + userId + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
