package uz.binart.trackmanagementsystem.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ResponseData<T> {
    private T data;
    private String errorMessage;
    private Long timestamp;

    public ResponseData(T data) {
        this.data = data;
        this.errorMessage = "";
        this.timestamp = System.currentTimeMillis();
    }

    public ResponseData(String successMessage) {
        this.errorMessage = "";
        this.data = (T) successMessage;
        this.timestamp = System.currentTimeMillis();
    }

    public ResponseData(T data, String errorMessage) {
        this.data = data;
        this.errorMessage = errorMessage;
        this.timestamp = System.currentTimeMillis();
    }

    public ResponseData() {
        this.errorMessage = "";
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ResponseEntity<ResponseData<T>> response(T data) {
        return ResponseEntity.ok(new ResponseData<>(data));
    }

    public static <T> ResponseEntity<ResponseData<T>> response(ResponseData<T> responseData, HttpStatus status) {
        return new ResponseEntity<>(responseData, status);
    }

    public static <T> ResponseEntity<ResponseData<T>> response(String errorMessage, HttpStatus httpStatus) {
        return new ResponseEntity<>(new ResponseData<>(null, errorMessage), httpStatus);
    }

    public static <T> ResponseEntity<ResponseData<T>> responseBadRequest(String errorMessage) {
        return new ResponseEntity<>(new ResponseData<>(null, errorMessage), HttpStatus.BAD_REQUEST);
    }

}
