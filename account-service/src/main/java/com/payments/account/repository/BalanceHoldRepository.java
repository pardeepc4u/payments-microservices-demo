package com.payments.account.repository;

import com.payments.account.domain.BalanceHold;
import com.payments.common.domain.HoldStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BalanceHoldRepository extends JpaRepository<BalanceHold, String> {
    List<BalanceHold> findByAccountIdAndStatus(String accountId, HoldStatus status);
    List<BalanceHold> findByPaymentId(String paymentId);
}
