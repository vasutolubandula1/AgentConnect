package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SocialService {
    private final AgentRepository agents;
    private final PostRepository posts;
    private final AgentFollowRepository follows;
    private final PostLikeRepository likes;
    private final NotificationRepository notifications;
    private final CommentRepository comments;

    public SocialService(AgentRepository agents, PostRepository posts,
            AgentFollowRepository follows, PostLikeRepository likes,
            NotificationRepository notifications, CommentRepository comments) {
        this.agents = agents; this.posts = posts; this.follows = follows;
        this.likes = likes; this.notifications = notifications; this.comments = comments;
    }

    @Transactional
    public Map<String, Object> follow(Long followerId, Long followedId) {
        Agent follower = agents.findById(followerId).orElseThrow(() -> new IllegalArgumentException("Follower agent not found"));
        Agent followed = agents.findById(followedId).orElseThrow(() -> new IllegalArgumentException("Agent to follow not found"));
        if (followerId.equals(followedId)) throw new IllegalArgumentException("An agent cannot follow itself");
        if (follows.findByFollowerIdAndFollowedId(followerId, followedId).isEmpty()) {
            AgentFollow follow = new AgentFollow();
            follow.setFollowerId(followerId);
            follow.setFollowedId(followedId);
            follows.save(follow);
            notifications.save(new Notification(followedId, follower.getName() + " started following you.", "FOLLOW"));
        }
        return followStatus(followerId, followedId);
    }

    @Transactional
    public Map<String, Object> unfollow(Long followerId, Long followedId) {
        follows.findByFollowerIdAndFollowedId(followerId, followedId).ifPresent(follows::delete);
        return followStatus(followerId, followedId);
    }

    public Map<String, Object> followStatus(Long followerId, Long followedId) {
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("following", follows.findByFollowerIdAndFollowedId(followerId, followedId).isPresent());
        result.put("followers", follows.countByFollowedId(followedId));
        result.put("followingCount", follows.countByFollowerId(followerId));
        return result;
    }

    @Transactional
    public Map<String, Object> toggleLike(Long postId, Long agentId) {
        Post post = posts.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        Agent agent = agents.findById(agentId).orElseThrow(() -> new IllegalArgumentException("Agent not found"));
        boolean liked;
        var existing = likes.findByPostIdAndAgentId(postId, agentId);
        if (existing.isPresent()) {
            likes.delete(existing.get()); liked = false;
        } else {
            PostLike like = new PostLike();
            like.setPostId(postId);
            like.setAgentId(agentId);
            likes.save(like);
            liked = true;
            // Notify the post author when the liker is not the author.
            agents.findAll().stream().filter(a -> a.getName() != null && a.getName().equalsIgnoreCase(post.getAgentName()))
                .findFirst().filter(a -> !a.getId().equals(agentId))
                .ifPresent(a -> notifications.save(new Notification(a.getId(), agent.getName() + " liked your post.", "LIKE")));
        }
        return likeStatus(postId, agentId);
    }

    public Map<String, Object> likeStatus(Long postId, Long agentId) {
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("liked", likes.findByPostIdAndAgentId(postId, agentId).isPresent());
        result.put("likes", likes.countByPostId(postId));
        return result;
    }

    public List<Notification> getNotifications(Long agentId) {
        if (!agents.existsById(agentId)) throw new IllegalArgumentException("Agent not found");
        return notifications.findByRecipientAgentIdOrderByCreatedAtDesc(agentId);
    }

    @Transactional
    public Notification markNotificationRead(Long id) {
        Notification n = notifications.findById(id).orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        n.setRead(true);
        return notifications.save(n);
    }

    public List<Comment> getComments(Long postId) {
        if (!posts.existsById(postId)) throw new IllegalArgumentException("Post not found");
        return comments.findByPostIdOrderByCreatedAtAsc(postId);
    }

    @Transactional
    public Comment addComment(Long postId, Long agentId, String content) {
        if (!posts.existsById(postId)) throw new IllegalArgumentException("Post not found");
        if (agentId != null && !agents.existsById(agentId)) throw new IllegalArgumentException("Agent not found");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("Comment cannot be empty");
        return comments.save(new Comment(postId, agentId, content.trim(), false));
    }

    @Transactional
    public Comment generateAiComment(Long postId, Long agentId) {
        Post post = posts.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        Agent agent = agentId == null ? null : agents.findById(agentId).orElseThrow(() -> new IllegalArgumentException("Agent not found"));
        String content = post.getContent() == null ? "" : post.getContent().trim();
        String response;
        String lower = content.toLowerCase(Locale.ROOT);
        if (lower.contains("help") || lower.contains("problem") || lower.contains("issue"))
            response = "Interesting challenge. A useful next step is to break it into smaller tasks, test one solution at a time, and share what works.";
        else if (lower.contains("learn") || lower.contains("study") || lower.contains("project"))
            response = "Great initiative! Try documenting your progress and sharing one practical takeaway so other agents can learn from it too.";
        else if (lower.contains("?"))
            response = "Good question! I would compare a couple of possible approaches, check the evidence, and choose the one that best fits the goal.";
        else
            response = "Thanks for sharing this update! The idea is interesting. Keep experimenting, measure the results, and share your next improvement.";
        if (agent != null) response = "AI assistant for " + agent.getName() + ": " + response;
        return comments.save(new Comment(postId, agentId, response, true));
    }

    public List<Map<String,Object>> findMatches(Long agentId) {
        Agent source = agents.findById(agentId).orElseThrow(() -> new IllegalArgumentException("Agent not found"));
        Set<String> interests = tokens((source.getName() == null ? "" : source.getName()) + " " +
                (source.getAgentType() == null ? "" : source.getAgentType()) + " " +
                (source.getDescription() == null ? "" : source.getDescription()));
        List<Map<String,Object>> result = new ArrayList<>();
        for (Agent candidate : agents.findAll()) {
            if (candidate.getId().equals(agentId)) continue;
            Set<String> candidateTokens = tokens((candidate.getName() == null ? "" : candidate.getName()) + " " +
                    (candidate.getAgentType() == null ? "" : candidate.getAgentType()) + " " +
                    (candidate.getDescription() == null ? "" : candidate.getDescription()));
            Set<String> common = new HashSet<>(interests); common.retainAll(candidateTokens);
            double score = interests.isEmpty() ? 0 : (100.0 * common.size() / interests.size());
            Map<String,Object> item = new LinkedHashMap<>();
            item.put("agent", candidate); item.put("matchScore", Math.round(score));
            item.put("sharedKeywords", common);
            result.add(item);
        }
        result.sort((a,b) -> Double.compare(((Number)b.get("matchScore")).doubleValue(), ((Number)a.get("matchScore")).doubleValue()));
        return result;
    }

    public Map<String,Object> dashboard(Long agentId) {
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("totalAgents", agents.count());
        result.put("totalPosts", posts.count());
        result.put("following", agentId == null ? 0 : follows.countByFollowerId(agentId));
        result.put("followers", agentId == null ? 0 : follows.countByFollowedId(agentId));
        result.put("unreadNotifications", agentId == null ? 0 : notifications.countByRecipientAgentIdAndReadFalse(agentId));
        result.put("totalLikes", likes.count());
        return result;
    }

    private Set<String> tokens(String value) {
        return Pattern.compile("[^a-z0-9]+").splitAsStream(value.toLowerCase(Locale.ROOT))
                .filter(s -> s.length() > 2)
                .filter(s -> !Set.of("the","and","for","with","from","this","that","are","you","your","agent").contains(s))
                .collect(Collectors.toSet());
    }
}
