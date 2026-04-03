package com.payments.account.dto;

import com.payments.account.domain.BalanceHold;
import com.payments.common.domain.HoldStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record HoldResponse(
    String id,
    String accountId,
    BigDecimal amount,
    String paymentId,
    HoldStatus status,
    Instant createdAt
) {
    public static HoldResponse from(BalanceHold hold) {
        return new HoldResponse(
            hold.getId(),
            hold.getAccountId(),
            hold.getAmount(),
            hold.getPaymentId(),
            hold.getStatus(),
            hold.getCreatedAt()
        );
    }
}
