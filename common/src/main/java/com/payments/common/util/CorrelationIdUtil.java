package com.payments.common.util;

import org.slf4j.MDC;

/**
 * Utility class for managing correlation IDs across services.
 * Correlation IDs are used for distributed tracing.
 */
public final class CorrelationIdUtil {
    
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String MDC_KEY = "correlationId";
    
    private CorrelationIdUtil() {
        // Utility class
    }
    
    /**
     * Generate a new correlation ID.
     */
    public static String generate() {
        return java.util.UUID.randomUUID().toString();
    }
    
    /**
     * Set correlation ID in MDC for logging.
     */
    public static void setCorrelationId(String correlationId) {
        MDC.put(MDC_KEY, correlationId);
    }
    
    /**
     * Get current correlation ID from MDC.
     */
    public static String getCorrelationId() {
        return MDC.get(MDC_KEY);
    }
    
    /**
     * Clear correlation ID from MDC.
     */
    public static void clear() {
        MDC.remove(MDC_KEY);
    }
    
    /**
     * Execute a runnable with a correlation ID.
     */
    public static void withCorrelationId(String correlationId, Runnable runnable) {
        String previous = getCorrelationId();
        try {
            setCorrelationId(correlationId);
            runnable.run();
        } finally {
            if (previous != null) {
                setCorrelationId(previous);
            } else {
                clear();
            }
        }
    }
}
