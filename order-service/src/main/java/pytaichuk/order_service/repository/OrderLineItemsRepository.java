package pytaichuk.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pytaichuk.order_service.model.OrderLineItems;

public interface OrderLineItemsRepository extends JpaRepository<OrderLineItems, Long> {
}
