package pytaichuk.customer_service.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineItemsResponseDto {
    private Long id;
    private String skuCode;
    private String itemName;
    private BigDecimal price;
    private Integer quantity;
}
