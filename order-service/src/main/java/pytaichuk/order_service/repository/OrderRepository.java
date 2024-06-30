package pytaichuk.order_service.repository;

import pytaichuk.order_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findOrderByOrderNumber(String orderNumber);
    List<Order> findOrdersByCustomerId(Long customerId);
    List<Order> findOrdersByCreatedAt(LocalDateTime createdAt);
}
