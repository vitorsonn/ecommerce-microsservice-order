package micro.order_service.application;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import micro.order_service.domain.Order;
import micro.order_service.domain.OutboxMessage;
import micro.order_service.infra.persistence.OrderRepository;
import micro.order_service.infra.persistence.OutboxRepository;
import micro.order_service.presentation.dto.CreateOrderRequest;
import micro.order_service.presentation.dto.OrderCreatedEvent;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;


    @Transactional
    public Order createOrder(CreateOrderRequest request) {

        Order order = Order.builder()
                .customerId(request.customerId())
                .totalAmount(request.totalAmount())
                .build();

        Order savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getCustomerId(),
                savedOrder.getTotalAmount()
        );

        try{

            String payload = objectMapper.writeValueAsString(event);

            OutboxMessage outboxMessage = OutboxMessage.builder()
                    .aggregateType("Order")
                    .aggregateId(savedOrder.getId().toString())
                    .eventType("OrderCreatedEvent")
                    .payload(payload)
                    .build();

            outboxRepository.save(outboxMessage);
        } catch (JacksonException e) {
            throw new RuntimeException("Erro ao serializar evento do Outbox", e);
        }

        return savedOrder;
    }

}
