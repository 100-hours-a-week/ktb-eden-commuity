package com.example.restapi_subject.global.config;

import com.example.restapi_subject.domain.auth.jwt.JwtAuthFilter;
import com.example.restapi_subject.domain.auth.jwt.LoginFilter;
import com.example.restapi_subject.domain.auth.service.AuthService;
import com.example.restapi_subject.domain.user.repository.UserRepository;
import com.example.restapi_subject.global.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Autowired
    UserRepository userRepository;

    // 1) 로그인 필터
    @Bean
    public FilterRegistrationBean<LoginFilter> loginFilter(AuthService authService, ObjectMapper objectMapper) {
        FilterRegistrationBean<LoginFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new LoginFilter(authService, objectMapper));
        reg.addUrlPatterns("/api/v1/auth/login");
        reg.setName("loginFilter");
        reg.setOrder(1);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(JwtUtil jwtUtil) {
        FilterRegistrationBean<JwtAuthFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new JwtAuthFilter(jwtUtil, userRepository));
        reg.addUrlPatterns("/*");
        reg.setName("jwtAuthFilter");
        reg.setOrder(2);
        return reg;
    }
}
