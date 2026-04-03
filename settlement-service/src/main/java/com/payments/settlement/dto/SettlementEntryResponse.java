package com.payments.settlement.dto;

import com.payments.settlement.model.SettlementEntryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementEntryResponse {

    private Long id;
    private String paymentId;
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private SettlementEntryStatus status;
    private String correlationId;
    private String reason;
    private Instant createdAt;
    private Instant settledAt;
}
