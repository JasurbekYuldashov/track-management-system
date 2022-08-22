package uz.binart.trackmanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "wrong structure of an object")
public class WrongEntityStructureException extends RuntimeException{
    public WrongEntityStructureException(){
        super();
    }

    public WrongEntityStructureException(String reason){
        super(reason);
    }

    public WrongEntityStructureException(String reason, Throwable cause){
        super(reason, cause);
    }

}
