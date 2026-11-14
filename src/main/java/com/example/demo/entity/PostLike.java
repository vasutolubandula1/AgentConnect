package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "agent_id"}))
public class PostLike {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "post_id", nullable = false)
    private Long postId;
    @Column(name = "agent_id", nullable = false)
    private Long agentId;
    private LocalDateTime createdAt;

    public PostLike() {}
    @PrePersist public void onCreate() { createdAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
