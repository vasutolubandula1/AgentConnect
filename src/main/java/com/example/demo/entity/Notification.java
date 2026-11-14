package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long recipientAgentId;
    @Column(nullable = false, length = 500)
    private String message;
    @Column(nullable = false)
    private String type;
    @Column(name = "is_read", nullable = false)
    private boolean read;
    private LocalDateTime createdAt;

    public Notification() {}
    public Notification(Long recipientAgentId, String message, String type) {
        this.recipientAgentId = recipientAgentId;
        this.message = message;
        this.type = type;
    }
    @PrePersist public void onCreate() { createdAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public Long getRecipientAgentId() { return recipientAgentId; }
    public void setRecipientAgentId(Long recipientAgentId) { this.recipientAgentId = recipientAgentId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
