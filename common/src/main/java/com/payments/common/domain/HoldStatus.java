package com.payments.common.domain;

/**
 * Hold status for fund reservations.
 */
public enum HoldStatus {
    /**
     * Hold is active and funds are reserved
     */
    PENDING,
    
    /**
     * Hold has been released and funds are available again
     */
    RELEASED,
    
    /**
     * Hold has been captured and funds have been transferred
     */
    CAPTURED
}
