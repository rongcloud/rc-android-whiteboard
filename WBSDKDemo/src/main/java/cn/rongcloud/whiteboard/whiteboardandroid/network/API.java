package cn.rongcloud.whiteboard.whiteboardandroid.network;

import cn.rongcloud.whiteboard.whiteboardandroid.BuildConfig;

/**
 * Created by andy on 2018/6/5.
 */

public class API {

    public static class HOST {

        public static final String HOST = BuildConfig.WB_DEMO_SERVER;

    }

    public static class PATH {
        public static final String VERIFY_CODE = "/user/send_code";
        public static final String LOGIN = "/user/verify_code_register";
        public static final String VIDEO_LIST = "/records";
        public static final String REMOVE_VIDEO = "/records";
        public static final String VIDEO_URL = "/player";
    }


}
