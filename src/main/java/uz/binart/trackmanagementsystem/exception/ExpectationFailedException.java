package uz.binart.trackmanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class ExpectationFailedException extends RuntimeException{

    public ExpectationFailedException(){
        super();
    }

    public ExpectationFailedException(String message){
        super(message);
    }

    public ExpectationFailedException(String message, Throwable cause){
        super(message, cause);
    }

}
