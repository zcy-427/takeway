package com.example.takeway.firstspringboot.interceptor;

import com.example.takeway.firstspringboot.utils.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("Authorization");
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("保安：站住！你没有 VIP 手环（Token），禁止入内！");
        }

        try {
            // 如果手环是伪造的、或者过期了，这里会自动抛出异常
            Long userId = JwtUtil.getUserIdFromToken(token);

            // 4. 验证通过！保安极其恭敬地放行，并把客人的 ID 挂在胸前（存入 request），方便后续大厨使用
            request.setAttribute("currentUserId", userId);
            System.out.println("====== 保安放行！尊贵的 VIP 客人 " + userId + " 进入了系统 ======");

            return true; // true 代表放行，请求继续走向 Controller

        } catch (Exception e) {
            throw new RuntimeException("保安：手环造假或已过期！给我打出去！");
        }
    }
}
