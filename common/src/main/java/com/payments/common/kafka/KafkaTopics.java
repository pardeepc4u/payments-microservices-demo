package com.payments.common.kafka;

/**
 * Kafka topic names for the payments microservices.
 */
public final class KafkaTopics {
    
    private KafkaTopics() {
        // Utility class
    }
    
    // Payment topics
    public static final String PAYMENT_INITIATED = "payment.initiated";
    public static final String PAYMENT_VALIDATED = "payment.validated";
    public static final String PAYMENT_PROCESSING = "payment.processing";
    public static final String PAYMENT_COMPLETED = "payment.completed";
    public static final String PAYMENT_FAILED = "payment.failed";
    
    // Account topics
    public static final String ACCOUNT_UPDATED = "account.updated";
    public static final String ACCOUNT_HOLD_CREATED = "account.hold.created";
    public static final String ACCOUNT_HOLD_RELEASED = "account.hold.released";
    
    // Notification topics
    public static final String NOTIFICATION_SENT = "notification.sent";
    
    // Audit topics
    public static final String AUDIT_LOGGED = "audit.logged";
}
