package uz.binart.trackmanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
public class FieldLimitException extends RuntimeException{

    public FieldLimitException() {
        super();
    }

    public FieldLimitException(String message) {
        super(message);
    }

    public FieldLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
