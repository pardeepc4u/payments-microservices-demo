package com.payments.common.domain;

/**
 * Base entity with common fields for all domain objects.
 */
public abstract class BaseEntity {
    
    protected String id;
    protected java.time.Instant createdAt;
    protected java.time.Instant updatedAt;
    
    public BaseEntity() {
        this.createdAt = java.time.Instant.now();
        this.updatedAt = java.time.Instant.now();
    }
    
    public BaseEntity(String id) {
        this();
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public java.time.Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(java.time.Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public java.time.Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(java.time.Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void markUpdated() {
        this.updatedAt = java.time.Instant.now();
    }
}
