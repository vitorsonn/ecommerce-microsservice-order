package micro.order_service.infra.persistence;

import micro.order_service.domain.OutboxMessage;
import micro.order_service.domain.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxMessage, UUID> {
    List<OutboxMessage> findByStatus(OutboxStatus status);

}
