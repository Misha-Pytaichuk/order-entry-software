package pytaichuk.order_service.dto.order;

import lombok.*;
import pytaichuk.order_service.dto.item.OrderLineItemsResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String orderNumber;
    private String orderName;
    private LocalDateTime createdAt;
    private List<OrderLineItemsResponseDto> orderLineItemsResponseDto;
}
