package micro.order_service.application;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import micro.order_service.domain.OutboxMessage;
import micro.order_service.domain.OutboxStatus;
import micro.order_service.infra.config.RabbitMQConfig;
import micro.order_service.infra.persistence.OutboxRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxWorker {
    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;


    @Scheduled(fixedDelay = 5000) // 5 segs
    @Transactional
    public void processOutboxMessages() {
        List<OutboxMessage> pendingMessages = outboxRepository.findByStatus(OutboxStatus.PENDING);

        if (pendingMessages.isEmpty()) {
            log.info("No pending outbox messages to process.");
            return;
        }


        log.info("Processing {} pending outbox messages.", pendingMessages.size());


        for (OutboxMessage message : pendingMessages) {
            try {
                Message amqpMessage = MessageBuilder
                        .withBody(message.getPayload().getBytes(StandardCharsets.UTF_8))
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .build();

                rabbitTemplate.send(
                        RabbitMQConfig.EXCHANGE_ORDERS,
                        RabbitMQConfig.ROUTING_KEY_ORDER_CREATED,
                        amqpMessage
                );

                message.setStatus(OutboxStatus.PROCESSED);
                outboxRepository.save(message);

                log.info("Mensagem    Outbox ID {} enviada com sucesso.", message.getId());
            } catch (Exception e) {
                log.error("Erro ao enviar mensagem Outbox ID {}: {}", message.getId(), e.getMessage());
                message.setRetryCount(message.getRetryCount() + 1);

                if (message.getRetryCount() >= 3) {
                    message.setStatus(OutboxStatus.FAILED);
                    log.warn("Mensagem Outbox ID {} atingiu o limite de tentativas. Status alterado para FAILED.", message.getId());
                }

                outboxRepository.save(message); //salva mesmo assim com o status pending e o retry_count incrementado
            }
        }


    }
}
