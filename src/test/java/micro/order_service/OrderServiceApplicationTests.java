package micro.order_service;

import micro.order_service.application.OrderService;
import micro.order_service.application.OutboxWorker;
import micro.order_service.domain.Order;
import micro.order_service.domain.OutboxMessage;
import micro.order_service.domain.OutboxStatus;
import micro.order_service.infra.persistence.OutboxRepository;
import micro.order_service.presentation.dto.CreateOrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceApplicationTests {

	@Autowired
	private OrderService orderService;

	@Autowired
	private OutboxWorker outboxWorker;

	@Autowired
	private OutboxRepository outboxRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldCreateOrderAndProcessOutboxMessage() {
		CreateOrderRequest request = new CreateOrderRequest("customer-123", new BigDecimal("150.00"));
		Order order = orderService.createOrder(request);

		assertNotNull(order);
		assertNotNull(order.getId());

		outboxWorker.processOutboxMessages();

		List<OutboxMessage> pending = outboxRepository.findByStatus(OutboxStatus.PENDING);
		assertTrue(pending.isEmpty());
	}

}
