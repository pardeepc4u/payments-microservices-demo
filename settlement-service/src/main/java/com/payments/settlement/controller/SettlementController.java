package com.payments.settlement.controller;

import com.payments.settlement.dto.SettlementBatchResponse;
import com.payments.settlement.dto.SettlementEntryResponse;
import com.payments.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @PostMapping("/batches/{batchId}/process")
    public ResponseEntity<SettlementBatchResponse> processSettlement(@PathVariable Long batchId) {
        settlementService.processSettlement(batchId);
        SettlementBatchResponse response = settlementService.getBatch(batchId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/batches/{batchId}")
    public ResponseEntity<SettlementBatchResponse> getBatch(@PathVariable Long batchId) {
        SettlementBatchResponse response = settlementService.getBatch(batchId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/batches/{batchId}/entries")
    public ResponseEntity<List<SettlementEntryResponse>> getBatchEntries(@PathVariable Long batchId) {
        List<SettlementEntryResponse> entries = settlementService.getBatchEntries(batchId);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "settlement-service"));
    }
}
