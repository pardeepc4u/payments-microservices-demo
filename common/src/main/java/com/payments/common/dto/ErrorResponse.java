package com.payments.common.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Standard error response DTO.
 */
public record ErrorResponse(
    String errorCode,
    String message,
    Map<String, String> details,
    Instant timestamp,
    String path
) {
    public ErrorResponse(String errorCode, String message, String path) {
        this(errorCode, message, Map.of(), Instant.now(), path);
    }
}
