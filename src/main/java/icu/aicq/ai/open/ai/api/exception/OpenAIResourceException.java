package icu.aicq.ai.open.ai.api.exception;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author zhiqi
 * @date 2023-04-11
 */
public class OpenAIResourceException extends AicqException {

    private Integer code;

    private Response response;

    public OpenAIResourceException() {
    }

    public OpenAIResourceException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public OpenAIResourceException(String message) {
        super(message);
    }

    public OpenAIResourceException(Throwable throwable) {
        super(throwable);
    }

    public OpenAIResourceException(Integer code, Response response) {
        this.code = code;
        this.response = response;
    }

    public OpenAIResourceException(Integer code, Response response, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
        this.response = response;
    }

    public OpenAIResourceException(Integer code, Response response, String message) {
        super(message);
        this.code = code;
        this.response = response;
    }

    public OpenAIResourceException( Integer code, Response response, Throwable throwable) {
        super(throwable);
        this.code = code;
        this.response = response;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
