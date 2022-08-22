package uz.binart.trackmanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.EXPECTATION_FAILED, reason = "no such type of units")
public class NoSuchUnitTypeException extends RuntimeException{
    public NoSuchUnitTypeException(){
        super();
    }
    public NoSuchUnitTypeException(String reason){
        super(reason);
    }

    public NoSuchUnitTypeException(String reason, Throwable cause){
        super(reason, cause);
    }


}
