package com.gestionmedica.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final SecurityInterceptor interceptor; @Value("${app.cors.allowed-origins}") private String origins;
    public WebConfig(SecurityInterceptor interceptor) { this.interceptor=interceptor; }
    @Override public void addInterceptors(InterceptorRegistry registry) { registry.addInterceptor(interceptor).addPathPatterns("/api/clinica/**"); }
    @Override public void addCorsMappings(CorsRegistry registry) { registry.addMapping("/api/**").allowedOriginPatterns(java.util.Arrays.stream(origins.split(",")).map(String::trim).filter(value -> !value.isEmpty()).toArray(String[]::new)).allowedMethods("GET","POST","DELETE","OPTIONS").allowedHeaders("*"); }
}
