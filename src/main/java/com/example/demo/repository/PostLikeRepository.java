package com.example.demo.repository;
import com.example.demo.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostIdAndAgentId(Long postId, Long agentId);
    List<PostLike> findByPostId(Long postId);
    long countByPostId(Long postId);
}
