package pytaichuk.computing_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "t_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    private String orderName;
    private LocalDateTime createdAt;
    private Long customerId;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderLineItems> orderLineItems;
}
