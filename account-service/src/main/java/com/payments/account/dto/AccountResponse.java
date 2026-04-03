package com.payments.account.dto;

import com.payments.account.domain.Account;
import com.payments.common.domain.AccountStatus;
import java.math.BigDecimal;

public record AccountResponse(
    String id,
    String accountNumber,
    String accountHolderName,
    BigDecimal availableBalance,
    BigDecimal currentBalance,
    BigDecimal heldAmount,
    String currency,
    AccountStatus status
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getAccountNumber(),
            account.getAccountHolderName(),
            account.getAvailableBalance(),
            account.getCurrentBalance(),
            account.getHeldAmount(),
            account.getCurrency(),
            account.getStatus()
        );
    }
}
