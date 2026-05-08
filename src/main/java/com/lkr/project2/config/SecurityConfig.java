package com.lkr.project2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)  // 由 WebConfig 处理 CORS
                .csrf(AbstractHttpConfigurer::disable)  // 关闭 CSRF 防护
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 无状态会话
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("POST", "/api/users/login").permitAll()  // 放行登录接口
                        .requestMatchers("POST", "/api/users").permitAll()       // 放行注册接口
                        .anyRequest().authenticated()  // 其他所有请求都必须先认证
                )
                .formLogin(AbstractHttpConfigurer::disable)  // 关闭表单登录
                .httpBasic(AbstractHttpConfigurer::disable); // 关闭 HTTP Basic 认证

        return http.build();
    }
}