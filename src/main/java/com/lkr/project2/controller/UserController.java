package com.lkr.project2.controller;

import com.lkr.project2.common.Result;
import com.lkr.project2.common.ResultCode;
import com.lkr.project2.entity.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // 1. 查询用户（GET）
    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable Long id) {
        String data = "查询成功，正在返回 ID 为 " + id + " 的用户信息";
        return Result.success(data);
    }

    // 2. 新增用户（POST） - 接收 JSON 数据
    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        // 返回完整的用户对象，方便前端验证
        return Result.success(user);
    }

    // 3. 修改用户（PUT）
    @PutMapping("/{id}")
    public Result<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        String data = "更新成功，ID " + id + " 的用户已修改为：" + user.getName() + "，年龄：" + user.getAge();
        return Result.success(data);
    }

    // 4. 删除用户（DELETE）
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        String data = "删除成功，已移除 ID 为 " + id + " 的用户";
        return Result.success(data);
    }

    // 5. 登录接口（用于获取Token）
    @PostMapping("/login")
    public Result<String> login(@RequestParam String username,
                                @RequestParam String password) {
        // 简单验证用户名密码
        if ("admin".equals(username) && "123456".equals(password)) {
            // 模拟生成Token（实际项目应该用JWT等安全机制）
            String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
                    + "." + System.currentTimeMillis()
                    + ".mock_jwt_signature";
            return Result.success(token);
        } else {
            // 使用统一的错误响应
            return Result.error(ResultCode.ERROR);
        }
    }

    // 6. 获取当前用户信息（需要Token验证）
    @GetMapping("/profile")
    public Result<User> getProfile() {
        // 模拟用户信息
        User user = new User(1L, "张三", 25);
        return Result.success(user);
    }
}