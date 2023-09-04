package icu.aicq.ai.open.ai.api.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhiqi
 * @since 2023-03-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AicqException extends RuntimeException {
    private String message;

    private Throwable throwable;

    public AicqException() {

    }

    public AicqException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public AicqException(String message) {
        super(message);
    }

    public AicqException(Throwable throwable) {
        super(throwable);
    }
}