package com.durianchain.interceptors;

import com.durianchain.common.result.Result;
import com.durianchain.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

/**
 * Interceptor to validate JWT tokens in incoming requests.
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // Allow preflight OPTIONS requests through
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Get the Authorization header
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendJsonResponse(response,
                    Result.error()
                            .code(HttpStatus.UNAUTHORIZED.value())
                            .message("Missing or invalid Authorization header.")
            );
            return false;
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix

        try {
            Claims claims = JwtUtil.parseJWT(token);
            request.setAttribute("claims", claims); // Store claims for downstream use
            return true;
        } catch (Exception e) {
            sendJsonResponse(response,
                    Result.error()
                            .code(HttpStatus.UNAUTHORIZED.value())
                            .message("Unauthorized, please log in again.")
            );
            return false;
        }
    }

    private void sendJsonResponse(HttpServletResponse response, Result result) throws Exception {
        response.setStatus(result.getCode());
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter out = response.getWriter();
        out.write(toJson(result));
        out.flush();
        out.close();
    }

    private String toJson(Result result) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"success\":").append(result.getSuccess()).append(",");
        sb.append("\"code\":").append(result.getCode()).append(",");
        sb.append("\"message\":\"").append(escapeJson(result.getMessage())).append("\",");

        // Optional: serialize data if present
        if (result.getData() != null && !result.getData().isEmpty()) {
            sb.append("\"data\":").append(new com.fasterxml.jackson.databind.ObjectMapper().valueToTree(result.getData()));
        } else {
            sb.append("\"data\":null");
        }

        sb.append("}");
        return sb.toString();
    }

    private String escapeJson(String str) {
        return str == null ? "" : str.replace("\"", "\\\"");
    }
}
