package pytaichuk.order_service.dto.customer.main_response;

import lombok.*;
import pytaichuk.order_service.dto.order.OrderResponse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private String name;
    private String surname;
    private String telephoneNumber;
    private OrderResponse order;
}
