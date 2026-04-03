package com.payments.settlement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.common.domain.PaymentCompletedEvent;
import com.payments.common.kafka.KafkaTopics;
import com.payments.settlement.dto.SettlementBatchResponse;
import com.payments.settlement.dto.SettlementEntryResponse;
import com.payments.settlement.model.SettlementBatch;
import com.payments.settlement.model.SettlementEntry;
import com.payments.settlement.model.SettlementEntryStatus;
import com.payments.settlement.model.SettlementStatus;
import com.payments.settlement.repository.SettlementBatchRepository;
import com.payments.settlement.repository.SettlementEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementBatchRepository batchRepository;
    private final SettlementEntryRepository entryRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.PAYMENT_COMPLETED, groupId = "settlement-service-group")
    public void handlePaymentCompleted(String message) {
        try {
            PaymentCompletedEvent event = objectMapper.readValue(message, PaymentCompletedEvent.class);
            log.info("Received completed payment for settlement: paymentId={}, amount={}",
                    event.getPaymentId(), event.getAmount());

            SettlementBatch batch = getOrCreateCurrentBatch();
            
            SettlementEntry entry = SettlementEntry.builder()
                    .batch(batch)
                    .paymentId(event.getPaymentId())
                    .accountId(event.getFromAccountId())
                    .amount(event.getAmount())
                    .currency("USD")
                    .status(SettlementEntryStatus.PENDING)
                    .correlationId(event.getCorrelationId())
                    .createdAt(Instant.now())
                    .build();

            entryRepository.save(entry);

            batch.setTotalPayments(batch.getTotalPayments() + 1);
            if (batch.getTotalAmount() == null) {
                batch.setTotalAmount(BigDecimal.ZERO);
            }
            batch.setTotalAmount(batch.getTotalAmount().add(event.getAmount()));
            batchRepository.save(batch);

            log.info("Added payment {} to settlement batch {}", event.getPaymentId(), batch.getId());
        } catch (Exception e) {
            log.error("Error processing payment for settlement: {}", e.getMessage(), e);
        }
    }

    private SettlementBatch getOrCreateCurrentBatch() {
        return batchRepository.findFirstByStatusOrderByCreatedAtDesc(SettlementStatus.PENDING)
                .orElseGet(() -> {
                    SettlementBatch batch = SettlementBatch.builder()
                            .batchDate(Instant.now())
                            .status(SettlementStatus.PENDING)
                            .totalPayments(0)
                            .totalAmount(BigDecimal.ZERO)
                            .createdAt(Instant.now())
                            .build();
                    return batchRepository.save(batch);
                });
    }

    @Transactional
    public SettlementBatch processSettlement(Long batchId) {
        SettlementBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found: " + batchId));

        if (batch.getStatus() != SettlementStatus.PENDING) {
            throw new RuntimeException("Batch is not in PENDING status");
        }

        batch.setStatus(SettlementStatus.PROCESSING);
        batch.setSettlementReference("SETT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        batchRepository.save(batch);

        List<SettlementEntry> entries = entryRepository.findByBatchId(batchId);
        
        for (SettlementEntry entry : entries) {
            try {
                entry.setStatus(SettlementEntryStatus.SETTLED);
                entry.setSettledAt(Instant.now());
                entryRepository.save(entry);
            } catch (Exception e) {
                log.error("Failed to settle entry {}: {}", entry.getId(), e.getMessage());
                entry.setStatus(SettlementEntryStatus.FAILED);
                entry.setReason(e.getMessage());
                entryRepository.save(entry);
            }
        }

        batch.setStatus(SettlementStatus.COMPLETED);
        batch.setCompletedAt(Instant.now());
        batch.setBankReference("BANK-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        batchRepository.save(batch);

        log.info("Settlement batch {} completed: reference={}", batchId, batch.getSettlementReference());
        return batch;
    }

    public SettlementBatchResponse getBatch(Long batchId) {
        SettlementBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found: " + batchId));
        return mapBatchToResponse(batch);
    }

    public List<SettlementEntryResponse> getBatchEntries(Long batchId) {
        return entryRepository.findByBatchId(batchId).stream()
                .map(this::mapEntryToResponse)
                .collect(Collectors.toList());
    }

    private SettlementBatchResponse mapBatchToResponse(SettlementBatch batch) {
        return SettlementBatchResponse.builder()
                .id(batch.getId())
                .batchDate(batch.getBatchDate())
                .totalPayments(batch.getTotalPayments())
                .totalAmount(batch.getTotalAmount())
                .status(batch.getStatus())
                .settlementReference(batch.getSettlementReference())
                .bankReference(batch.getBankReference())
                .createdAt(batch.getCreatedAt())
                .completedAt(batch.getCompletedAt())
                .build();
    }

    private SettlementEntryResponse mapEntryToResponse(SettlementEntry entry) {
        return SettlementEntryResponse.builder()
                .id(entry.getId())
                .paymentId(entry.getPaymentId())
                .accountId(entry.getAccountId())
                .amount(entry.getAmount())
                .currency(entry.getCurrency())
                .status(entry.getStatus())
                .correlationId(entry.getCorrelationId())
                .reason(entry.getReason())
                .createdAt(entry.getCreatedAt())
                .settledAt(entry.getSettledAt())
                .build();
    }
}
