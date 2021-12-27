package cn.rongcloud.whiteboard.whiteboardandroid.model;

import java.io.Serializable;

/**
 * Created by yanke on 2021/8/4
 */
public class ItemVideoInfo implements Serializable {
    private int appId;
    private String hubId;
    private String id;
    private long recordedTime;
    private String sessionId;
    private String url;
    private String userId;
    private String duration;

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getHubId() {
        return hubId;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getRecordedTime() {
        return recordedTime;
    }

    public void setRecordedTime(long recordedTime) {
        this.recordedTime = recordedTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
