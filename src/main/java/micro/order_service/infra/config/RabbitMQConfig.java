package micro.order_service.infra.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_PAYMENT_PROCESS = "payment.process.queue";
    public static final String EXCHANGE_ORDERS = "orders.exchange";
    public static final String ROUTING_KEY_ORDER_CREATED = "order.created";
    public static final String QUEUE_ORDER_PAYMENT_RESPONSE = "order.payment-response.queue";
    public static final String EXCHANGE_PAYMENTS = "payments.exchange";
    public static final String ROUTING_KEY_PAYMENT_PROCESSED = "payment.processed";





    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange(EXCHANGE_ORDERS);
    }

    @Bean
    public TopicExchange paymentsExchange() {
        return new TopicExchange(EXCHANGE_PAYMENTS);
    }

    @Bean
    public Queue orderPaymentResponseQueue() {
        return QueueBuilder.durable(QUEUE_ORDER_PAYMENT_RESPONSE).build();
    }


    @Bean
    public Binding bindingOrderPaymentResponse(Queue orderPaymentResponseQueue, TopicExchange paymentsExchange) {
        return BindingBuilder.bind(orderPaymentResponseQueue)
                .to(paymentsExchange)
                .with(ROUTING_KEY_PAYMENT_PROCESSED);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public org.springframework.boot.ApplicationRunner initializeRabbit(RabbitAdmin rabbitAdmin) {
        return args -> rabbitAdmin.initialize();
    }
}

