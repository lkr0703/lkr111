package com.lkr.project2.interceptor;

import com.lkr.project2.common.Result;
import com.lkr.project2.common.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头获取 Authorization（Token）
        String token = request.getHeader("Authorization");

        // 校验Token：为空或空字符串则拦截
        if (token == null || token.isEmpty()) {
            // 设置响应为JSON格式
            response.setContentType("application/json;charset=UTF-8");
            // 构造错误响应
            Result<String> errorResult = Result.error(ResultCode.TOKEN_INVALID);
            String errorJson = "{\"code\":" + errorResult.getCode()
                    + ",\"msg\":\"" + errorResult.getMsg()
                    + "\",\"data\":null}";
            // 写入响应
            PrintWriter writer = response.getWriter();
            writer.write(errorJson);
            writer.flush();
            writer.close();
            return false; // 拦截，不放行到Controller
        }
        return true; // Token有效，放行
    }
}