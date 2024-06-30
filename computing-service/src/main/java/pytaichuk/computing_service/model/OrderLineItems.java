package pytaichuk.computing_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "t_order_line_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String skuCode;
    private String itemName;
    private BigDecimal price;
    private Integer quantity;
}
