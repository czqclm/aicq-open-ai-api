package icu.aicq.ai.open.ai.api.exception;

/**
 * @author zhiqi
 * @since 2023-04-11
 */
public class OpenAINoRouteToHostException extends AicqException {

    public OpenAINoRouteToHostException() {
    }

    public OpenAINoRouteToHostException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public OpenAINoRouteToHostException(String message) {
        super(message);
    }

    public OpenAINoRouteToHostException(Throwable throwable) {
        super(throwable);
    }
}
