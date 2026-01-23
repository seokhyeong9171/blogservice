package com.blogservice.api.service;

import com.blogservice.api.domain.auth.LoginLog;
import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.comment.CommentSnapshot;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.PostSnapshot;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.repository.auth.LoginLogRepository;
import com.blogservice.api.repository.comment.CommentSnapshotRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.repository.post.PostSnapshotRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SnapshotService {

    private final LoginLogRepository loginLogRepository;
    private final PostSnapshotRepository postSnapshotRepository;
    private final CommentSnapshotRepository commentSnapshotRepository;

    public void logLogin(HttpServletRequest servletRequest, User user) {
        String ipAddress = getClientIp(servletRequest);
        String userAgent = servletRequest.getHeader("User-Agent");

        LoginLog loginLog = LoginLog.builder()
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        loginLogRepository.save(loginLog);
    }

    public void postSnapshot(Post savedPost) {
        PostSnapshot snapshot = PostSnapshot.fromEntity(savedPost);
        postSnapshotRepository.save(snapshot);
    }

    public void commentSnapshot(Comment savedComment) {
        CommentSnapshot snapshot = CommentSnapshot.fromEntity(savedComment);
        commentSnapshotRepository.save(snapshot);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if ("unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if ("unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if ("unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}