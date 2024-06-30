package pytaichuk.computing_service.repository;

import pytaichuk.computing_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT SUM(oli.quantity * oli.price) FROM Order o " +
            "JOIN o.orderLineItems oli " +
            "WHERE o.createdAt >= :dateFrom AND o.createdAt <= :dateTo")
    Double getPrice(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);
}
