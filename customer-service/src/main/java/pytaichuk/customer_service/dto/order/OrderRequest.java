package pytaichuk.customer_service.dto.order;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pytaichuk.customer_service.dto.item.OrderLineItemsRequestDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @Pattern(regexp = "0\\d{9}")
    private String telephoneNumber;
    private List<OrderLineItemsRequestDto> orderLineItemsRequestDto;
}
