package uz.binart.trackmanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "no entity with such name")
public class NotFoundException extends RuntimeException{

    public NotFoundException(){
        super();
    }

    public NotFoundException(String reason){
        super(reason);
    }

    public NotFoundException(String reason, Throwable cause){
        super(reason, cause);
    }

}
