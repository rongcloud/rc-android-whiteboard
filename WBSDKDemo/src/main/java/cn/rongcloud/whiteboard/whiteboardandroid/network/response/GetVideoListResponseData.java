package cn.rongcloud.whiteboard.whiteboardandroid.network.response;

import java.util.List;

import cn.rongcloud.whiteboard.whiteboardandroid.model.ItemVideoInfo;

/**
 * Created by yanke on 2021/8/2
 */
public class GetVideoListResponseData extends ResponseData {

    private List<ItemVideoInfo> videoInfoList;

    public List<ItemVideoInfo> getVideoInfoList() {
        return videoInfoList;
    }

    public void setVideoInfoList(List<ItemVideoInfo> videoInfoList) {
        this.videoInfoList = videoInfoList;
    }
}
