package micro.order_service.domain;

public enum OutboxStatus {
    PENDING,
    PROCESSED,
    FAILED
}
