package icu.aicq.ai.open.ai.api.exception;

/**
 * @author zhiqi
 * @since 2023-04-11
 */
public class OpenAIStreamClosedUnexpectedlyException extends AicqException {

    public OpenAIStreamClosedUnexpectedlyException() {
    }

    public OpenAIStreamClosedUnexpectedlyException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public OpenAIStreamClosedUnexpectedlyException(String message) {
        super(message);
    }

    public OpenAIStreamClosedUnexpectedlyException(Throwable throwable) {
        super(throwable);
    }
}
