package micro.order_service.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderRequest(@NotNull String customerId,
                                 @NotNull @Positive BigDecimal totalAmount) {
}
