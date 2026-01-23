package com.blogservice.api.service;

import com.blogservice.api.domain.auth.LoginLog;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.repository.auth.LoginLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SnapshotService {

    private final LoginLogRepository loginLogRepository;

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