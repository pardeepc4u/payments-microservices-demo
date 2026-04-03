package com.payments.payment.dto;

import com.payments.common.domain.PaymentStatus;
import com.payments.common.domain.PaymentType;
import com.payments.payment.domain.Payment;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
    String id,
    String fromAccountId,
    String toAccountId,
    BigDecimal amount,
    String currency,
    PaymentType paymentType,
    PaymentStatus status,
    String routingNumber,
    String accountNumber,
    String traceNumber,
    String description,
    Instant createdAt,
    Instant updatedAt,
    String correlationId
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getFromAccountId(),
            payment.getToAccountId(),
            payment.getAmount(),
            payment.getCurrency(),
            payment.getPaymentType(),
            payment.getStatus(),
            maskAccountNumber(payment.getRoutingNumber()),
            maskAccountNumber(payment.getAccountNumber()),
            payment.getTraceNumber(),
            payment.getDescription(),
            payment.getCreatedAt(),
            payment.getUpdatedAt(),
            payment.getCorrelationId()
        );
    }
    
    private static String maskAccountNumber(String number) {
        if (number == null || number.length() <= 4) {
            return "****";
        }
        return "****" + number.substring(number.length() - 4);
    }
}
