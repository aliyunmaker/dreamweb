package cc.landingzone.dreamweb.common.response;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author dongmeng.ldm
 * @date 2020/11/4
 */
@Data
public class ResponseBaseResult<T> implements Serializable {

    public static final String SUCCESS_CODE = "200";

    /**
     * 当api返回的值为非200，并且没有code属性的时候就会显示这个错误信息。
     */
    public static final String SERVER_ERROR_CODE = "SERVER_ERROR_CODE";

    public static final String DEFAULT_ERROR_CODE = "500";

    private static final long serialVersionUID = 3257204060864767332L;



    /**
     * 请求ID
     */
    private String requestId;

    String code = SUCCESS_CODE;

    String message;

    Integer httpStatusCode = 200;

    T data;

    ResponseBaseResult(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseBaseResult() {
    }


    public String getRequestId() {
        if (this.requestId == null) {
            this.requestId = UUID.randomUUID().toString();
        }
        return this.requestId;
    }

    /**
     * 方便方法判定是否成功的需求。
     *
     * @return
     */
    public boolean isSuccess() {
        return this.code.equals(SUCCESS_CODE);
    }

    public static <T> ResponseBaseResult<T> createSuccessResponse(T data) {
        ResponseBaseResult<T> responseBaseResult = new ResponseBaseResult<>();
        responseBaseResult.setData(data);
        return responseBaseResult;
    }

    public static ResponseBaseResult createErrorResponse(String code, String message) {
        return new ResponseBaseResult(code, message);
    }

    public static ResponseBaseResult createErrorResponse(String message) {
        return new ResponseBaseResult(DEFAULT_ERROR_CODE, message);
    }

}