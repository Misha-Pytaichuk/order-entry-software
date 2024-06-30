package pytaichuk.customer_service.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindException extends RuntimeException {
    public FindException(String message) {
        super(message);
    }
}
