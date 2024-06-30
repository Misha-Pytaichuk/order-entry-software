package pytaichuk.order_service.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateRequest {
    private Integer year;
    private Integer month;
    private Integer day;
}
