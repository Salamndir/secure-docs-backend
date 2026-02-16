package com.salem.backend.logging;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AccessLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // 1. وقت البداية
        long startTime = System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 2. استخراج البيانات الأساسية
        String method = request.getMethod();
        String requestUri = request.getRequestURI();
        String userAgent = request.getHeader("User-Agent");
        
        // [تعديل هام] جلب IP الحقيقي من خلف Nginx
        String ipAddress = getClientIp(request);

        // 3. استخراج هوية المستخدم (بدون تعطيل الطلب لو فشل)
        String username = "Anonymous";
        try {
            username = extractToken(request);
        } catch (Exception e) {
            log.warn("Could not extract token username: " + e.getMessage());
        }

        try {
            // 4. تنفيذ الطلب (اترك السبرينج يكمل شغله)
            chain.doFilter(request, response);
        } finally {
            // 5. التسجيل بعد الانتهاء (عشان نعرف الـ Status Code ومدة التنفيذ)
            long duration = System.currentTimeMillis() - startTime;
            int statusCode = response.getStatus();

            String logMessage = String.format(
                "[AUDIT] Time: %s | IP: %s | User: %s | Method: %s | URL: %s | Status: %d | Agent: %s | Duration: %dms",
                timestamp, ipAddress, username, method, requestUri, statusCode, userAgent, duration
            );

            // سجل الأخطاء باللون الأحمر (Error) والنجاح بالأخضر (Info)
            if (statusCode >= 400) {
                log.error(logMessage); // يظهر بالأحمر في السيرفر
            } else {
                log.info(logMessage);
            }
        }
    }

    /**
     * دالة ذكية لجلب الـ IP الحقيقي حتى لو خلف Nginx أو Cloudflare
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // أحياناً يكون الهيدر يحتوي عدة عناوين (Client, Proxy1, Proxy2) -> نأخذ الأول
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // نستخدم مكتبة Nimbus الموجودة أصلاً مع Spring Security
                SignedJWT signedJWT = SignedJWT.parse(token);
                String preferredUsername = (String) signedJWT.getJWTClaimsSet().getClaim("preferred_username");
                return preferredUsername != null ? preferredUsername : "Unknown-User";
            } catch (ParseException e) {
                return "Invalid-Token";
            }
        }
        return "Anonymous";
    }

    @Override
    public void destroy() {}
}