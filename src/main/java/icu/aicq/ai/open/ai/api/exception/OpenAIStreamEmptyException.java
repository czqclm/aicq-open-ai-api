package icu.aicq.ai.open.ai.api.exception;

/**
 * @author zhiqi
 * @since 2023-04-11
 */
public class OpenAIStreamEmptyException extends AicqException {

    public OpenAIStreamEmptyException() {
    }

    public OpenAIStreamEmptyException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public OpenAIStreamEmptyException(String message) {
        super(message);
    }

    public OpenAIStreamEmptyException(Throwable throwable) {
        super(throwable);
    }
}
