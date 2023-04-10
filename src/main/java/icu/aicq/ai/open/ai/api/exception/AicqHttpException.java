package icu.aicq.ai.open.ai.api.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhiqi
 * @date 2023-03-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AicqHttpException extends RuntimeException {

    private Integer code;
    private Object response;

    private String msg;

    private Throwable throwable;

    public AicqHttpException(Integer code, Object response, String msg, Throwable throwable) {
        this.code = code;
        this.response = response;
        this.msg = msg;
        this.throwable = throwable;
    }

    public AicqHttpException(Integer code, Object response, String msg) {
        this.code = code;
        this.response = response;
        this.msg = msg;
    }
}