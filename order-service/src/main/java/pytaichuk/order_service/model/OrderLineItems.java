package pytaichuk.order_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

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
    @NotBlank(message = "Поле не може бути пустим")
    private String skuCode;
    @NotBlank(message = "Поле не може бути пустим")
    private String itemName;
    @Min(value = 0, message = "Ціна не може бути меньше або дорівнювати нулю")
    private BigDecimal price;
    @Min(value = 0, message = "Кількість не може бути меньше або дорівнювати нулю")
    private Integer quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLineItems that = (OrderLineItems) o;
        return Objects.equals(id, that.id) && Objects.equals(skuCode, that.skuCode) && Objects.equals(itemName, that.itemName) && Objects.equals(price, that.price) && Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, skuCode, itemName, price, quantity);
    }
}
