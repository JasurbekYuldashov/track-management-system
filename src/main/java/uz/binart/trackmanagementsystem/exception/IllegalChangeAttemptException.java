package uz.binart.trackmanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "trying to parse existing unit like new?")
public class IllegalChangeAttemptException extends RuntimeException{
    public IllegalChangeAttemptException(){
        super();
    }
    public IllegalChangeAttemptException(String reason){
        super(reason);
    }
    public IllegalChangeAttemptException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
