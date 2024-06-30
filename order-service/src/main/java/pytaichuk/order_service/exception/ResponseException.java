package pytaichuk.order_service.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ResponseException {
    private String message;
    private LocalDateTime localDateTime;

    public ResponseException(String message, LocalDateTime localDateTime) {
        this.message = message;
        this.localDateTime = localDateTime;
    }
}
