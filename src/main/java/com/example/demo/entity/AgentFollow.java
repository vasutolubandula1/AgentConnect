package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_follows",
       uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followed_id"}))
public class AgentFollow {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "follower_id", nullable = false)
    private Long followerId;
    @Column(name = "followed_id", nullable = false)
    private Long followedId;
    private LocalDateTime createdAt;

    public AgentFollow() {}
    @PrePersist public void onCreate() { createdAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public Long getFollowerId() { return followerId; }
    public void setFollowerId(Long followerId) { this.followerId = followerId; }
    public Long getFollowedId() { return followedId; }
    public void setFollowedId(Long followedId) { this.followedId = followedId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
