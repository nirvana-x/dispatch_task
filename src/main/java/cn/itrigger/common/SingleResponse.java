package cn.itrigger.common;

/**
 * @author
 * @Description ${TODO}
 * @Date 2019/5/17 18:55
 **/
public class SingleResponse<T> extends Response<T> {
    private T data;

    public SingleResponse(int httpCode, String httpMsg) {
        super(httpCode, httpMsg);
    }

    public SingleResponse(int httpCode, String httpMsg, T data) {
        super(httpCode, httpMsg);
        this.data = data;
    }

    public T getData() {
        return this.data;
    }

    public SingleResponse<T> setData(T data) {
        this.data = data;
        return this;
    }
}
