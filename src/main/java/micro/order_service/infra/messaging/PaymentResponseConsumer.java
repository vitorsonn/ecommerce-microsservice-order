package micro.order_service.infra.messaging;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import micro.order_service.application.OrderService;
import micro.order_service.infra.config.RabbitMQConfig;
import micro.order_service.presentation.dto.PaymentProcessedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentResponseConsumer {

    private final OrderService orderService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ORDER_PAYMENT_RESPONSE)
    public void consumePaymentResponse(PaymentProcessedEvent event){
        log.info("Resposta de pagamenot recebida para o pedido: {}", event.orderId());
        orderService.updateOrderStatus(event.orderId(), event.status());

    }
}
