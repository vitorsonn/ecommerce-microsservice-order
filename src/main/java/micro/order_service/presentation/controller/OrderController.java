package micro.order_service.presentation.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import micro.order_service.application.OrderService;
import micro.order_service.domain.Order;
import micro.order_service.presentation.dto.CreateOrderRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        Order createdOrder = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(createdOrder);
    }


}
