package com.payments.settlement.dto;

import com.payments.settlement.model.SettlementStatus;
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
public class SettlementBatchResponse {

    private Long id;
    private Instant batchDate;
    private Integer totalPayments;
    private BigDecimal totalAmount;
    private SettlementStatus status;
    private String settlementReference;
    private String bankReference;
    private Instant createdAt;
    private Instant completedAt;
}
