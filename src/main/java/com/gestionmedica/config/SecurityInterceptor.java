package com.gestionmedica.config;

import com.gestionmedica.exception.ApiException;
import com.gestionmedica.service.AuthService;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SecurityInterceptor implements HandlerInterceptor {
    private final AuthService auth;
    public SecurityInterceptor(AuthService auth) { this.auth=auth; }
    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path=request.getRequestURI(); String method=request.getMethod();
        if (method.equals("OPTIONS") || path.equals("/actuator/health") || path.equals("/api/clinica/auth/register") || path.equals("/api/clinica/auth/login") || (method.equals("POST") && path.equals("/api/clinica/paciente")) || (method.equals("GET") && path.equals("/api/clinica/medicos"))) return true;
        String header=request.getHeader("Authorization"); if (header==null || !header.startsWith("Bearer ")) throw ApiException.unauthorized("Se requiere un token Bearer");
        request.setAttribute("patientId", auth.patientForToken(header.substring(7))); return true;
    }
}
