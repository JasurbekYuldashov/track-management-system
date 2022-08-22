package uz.binart.trackmanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "entity with such id already exists")
public class UnitWithSuchNumberExistsAlreadyException extends RuntimeException{
    public UnitWithSuchNumberExistsAlreadyException(){
        super();
    }
    public UnitWithSuchNumberExistsAlreadyException(String reason){
        super(reason);
    }
    public UnitWithSuchNumberExistsAlreadyException(String reason, Throwable cause){
        super(reason, cause);
    }
}
