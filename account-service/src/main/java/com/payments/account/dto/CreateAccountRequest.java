package com.payments.account.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateAccountRequest(
    @NotBlank(message = "Account holder name is required")
    String accountHolderName,
    
    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.00", message = "Initial balance cannot be negative")
    BigDecimal initialBalance,
    
    String routingNumber
) {}
