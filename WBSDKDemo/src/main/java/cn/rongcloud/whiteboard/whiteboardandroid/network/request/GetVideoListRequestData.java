package cn.rongcloud.whiteboard.whiteboardandroid.network.request;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by yanke on 2021/8/3
 */
public class GetVideoListRequestData implements Serializable {
    /**
     * 白板ID
     */
    public String hubId;
    /**
     * 用户ID
     */
    public String userId;
    /**
     * 录制开始时间
     */
    public long rsTime;
    /**
     * 录制结束时间
     */
    public long reTime;
    /**
     * 页数
     */
    public int offset;
    /**
     * 每页条数(每页最多200)
     */
    public int limit;
}
