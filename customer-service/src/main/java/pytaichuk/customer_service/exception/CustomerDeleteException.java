package pytaichuk.customer_service.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDeleteException extends RuntimeException{
    public CustomerDeleteException(String message){
        super(message);
    }
}
