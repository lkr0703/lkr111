package com.lkr.project2.config;

import com.lkr.project2.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/api/**") // 拦截所有 /api 开头的请求
                .excludePathPatterns(       // 放行不需要拦截的路径
                        "/api/users/login",  // 登录接口（重要：必须放行！）
                        "/api/users"         // 新增用户接口
                );
    }
}