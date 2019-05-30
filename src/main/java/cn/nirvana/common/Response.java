package cn.nirvana.common;

import java.io.Serializable;

/**
 * @author
 * @Description ${TODO}
 * @Date 2019/5/17 18:56
 **/
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 815861485460502164L;
    private int code;
    private String message;

    Response(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static enum ResponseCode {
        HTTP_OK(200, "操作成功"),
        HTTP_NO_CONTENT(204, "操作成功"),
        HTTP_SERVER_ERROR(500, "服务异常");

        private int code;
        private String message;

        private ResponseCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return this.code;
        }

        public String getMessage() {
            return this.message;
        }
    }
}
