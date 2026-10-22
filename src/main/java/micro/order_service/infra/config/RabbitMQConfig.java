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



    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange(EXCHANGE_ORDERS);
    }

    @Bean
    public Queue paymentQueue(){
        return QueueBuilder.durable(QUEUE_PAYMENT_PROCESS).build();
    }

    @Bean
    public Binding bindingPaymentQueue(Queue paymentQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(paymentQueue).to(ordersExchange).with(ROUTING_KEY_ORDER_CREATED);
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

