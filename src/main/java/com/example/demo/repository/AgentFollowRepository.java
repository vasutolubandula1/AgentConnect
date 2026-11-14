package com.example.demo.repository;
import com.example.demo.entity.AgentFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface AgentFollowRepository extends JpaRepository<AgentFollow, Long> {
    Optional<AgentFollow> findByFollowerIdAndFollowedId(Long followerId, Long followedId);
    List<AgentFollow> findByFollowerId(Long followerId);
    List<AgentFollow> findByFollowedId(Long followedId);
    long countByFollowerId(Long followerId);
    long countByFollowedId(Long followedId);
}
