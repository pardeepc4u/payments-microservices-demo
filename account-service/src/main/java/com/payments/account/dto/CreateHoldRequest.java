package com.payments.account.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateHoldRequest(
    @NotBlank(message = "Payment ID is required")
    String paymentId,
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    BigDecimal amount
) {}
