package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long postId;
    private Long agentId;
    @Column(nullable = false, length = 1200)
    private String content;
    @Column(nullable = false)
    private boolean aiGenerated;
    private LocalDateTime createdAt;

    public Comment() {}
    public Comment(Long postId, Long agentId, String content, boolean aiGenerated) {
        this.postId = postId; this.agentId = agentId;
        this.content = content; this.aiGenerated = aiGenerated;
    }
    @PrePersist public void onCreate() { createdAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public Long getPostId() { return postId; }
    public Long getAgentId() { return agentId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isAiGenerated() { return aiGenerated; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
