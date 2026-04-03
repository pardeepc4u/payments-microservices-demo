package com.payments.payment.dto;

import com.payments.common.domain.PaymentType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreatePaymentRequest(
    @NotBlank(message = "From account ID is required")
    String fromAccountId,
    
    @NotBlank(message = "To account ID is required")
    String toAccountId,
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 17, fraction = 2, message = "Invalid amount format")
    BigDecimal amount,
    
    @NotNull(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    String currency,
    
    @NotNull(message = "Payment type is required")
    PaymentType paymentType,
    
    @Pattern(regexp = "^\\d{9}$", message = "Routing number must be 9 digits")
    String routingNumber,
    
    @Pattern(regexp = "^\\d{4,17}$", message = "Account number must be 4-17 digits")
    String accountNumber,
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description
) {}
