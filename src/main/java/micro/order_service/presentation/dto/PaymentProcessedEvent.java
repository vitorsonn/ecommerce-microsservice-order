package micro.order_service.presentation.dto;

import micro.order_service.domain.OrderStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentProcessedEvent(UUID paymentId,
                                    UUID orderId,
                                    String customerId,
                                    BigDecimal amount,
                                    String status) {
}
