package uz.binart.trackmanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.EXPECTATION_FAILED, reason = "no such ownership type")
public class NoSuchOwnershipTypeException extends RuntimeException{

    public NoSuchOwnershipTypeException(){
        super();
    }

    public NoSuchOwnershipTypeException(String reason){
        super(reason);
    }

    public NoSuchOwnershipTypeException(String reason, Throwable cause){
        super(reason, cause);
    }

}
