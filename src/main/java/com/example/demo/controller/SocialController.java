package com.example.demo.controller;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Notification;
import com.example.demo.service.SocialService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SocialController {
    private final SocialService socialService;
    public SocialController(SocialService socialService) { this.socialService = socialService; }
    @PostMapping("/agents/{followerId}/follow/{followedId}")
    public Map<String,Object> follow(@PathVariable Long followerId, @PathVariable Long followedId) {
        return socialService.follow(followerId, followedId);
    }
    @DeleteMapping("/agents/{followerId}/follow/{followedId}")
    public Map<String,Object> unfollow(@PathVariable Long followerId, @PathVariable Long followedId) {
        return socialService.unfollow(followerId, followedId);
    }
    @GetMapping("/agents/{followerId}/follow/{followedId}")
    public Map<String,Object> followStatus(@PathVariable Long followerId, @PathVariable Long followedId) {
        return socialService.followStatus(followerId, followedId);
    }
    @GetMapping("/agents/{agentId}/matches")
    public List<Map<String,Object>> matches(@PathVariable Long agentId) {
        return socialService.findMatches(agentId);
    }
    @PostMapping("/posts/{postId}/like")
    public Map<String,Object> like(@PathVariable Long postId, @RequestParam Long agentId) {
        return socialService.toggleLike(postId, agentId);
    }
    @GetMapping("/posts/{postId}/like")
    public Map<String,Object> likeStatus(@PathVariable Long postId, @RequestParam Long agentId) {
        return socialService.likeStatus(postId, agentId);
    }
    @GetMapping("/posts/{postId}/comments")
    public List<Comment> comments(@PathVariable Long postId) { return socialService.getComments(postId); }
    @PostMapping("/posts/{postId}/comments")
    public Comment addComment(@PathVariable Long postId, @RequestParam(required = false) Long agentId,
                              @RequestBody Map<String,String> body) {
        return socialService.addComment(postId, agentId, body.get("content"));
    }
    @PostMapping("/posts/{postId}/ai-comment")
    public Comment aiComment(@PathVariable Long postId, @RequestParam(required = false) Long agentId) {
        return socialService.generateAiComment(postId, agentId);
    }
    @GetMapping("/notifications")
    public List<Notification> notifications(@RequestParam Long agentId) {
        return socialService.getNotifications(agentId);
    }
    @PutMapping("/notifications/{id}/read")
    public Notification markRead(@PathVariable Long id) { return socialService.markNotificationRead(id); }
    @GetMapping("/dashboard")
    public Map<String,Object> dashboard(@RequestParam(required = false) Long agentId) {
        return socialService.dashboard(agentId);
    }
}
