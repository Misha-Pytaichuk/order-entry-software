package pytaichuk.order_service.dto.order;

import lombok.*;
import pytaichuk.order_service.dto.item.OrderLineItemsRequestDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Long customerId;
    private List<OrderLineItemsRequestDto> orderLineItemsRequestDto;
}
