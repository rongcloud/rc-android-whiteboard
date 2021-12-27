package cn.rongcloud.whiteboard.whiteboardandroid.network.response;

public interface RequestErrorCodeMap {

    enum ErrorCode {
        /**
         * 未知错误
         */
        UNKNOWN(-1, "");

        public int code;
        public String msg;

        /**
         * 构造函数。
         *
         * @param code 错误代码。
         * @param msg  错误消息。
         */
        ErrorCode(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public static ErrorCode valueOf(int code) {
            for (ErrorCode c : ErrorCode.values()) {
                if (code == c.getCode()) {
                    return c;
                }
            }

            ErrorCode c = UNKNOWN;
            c.code = code;
            c.msg = code + "";

            return c;
        }

        /**
         * 获取错误代码值。
         *
         * @return 错误代码值。
         */
        public int getCode() {
            return this.code;
        }

        /**
         * 获取错误消息。
         *
         * @return 错误消息。
         */
        public String getMessage() {
            return this.msg;
        }
    }

}
