package com.lkr.project2.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lkr.project2.common.Result;
import com.lkr.project2.common.ResultCode;
import com.lkr.project2.dto.UserDTO;
import com.lkr.project2.entity.User;
import com.lkr.project2.mapper.UserMapper;
import com.lkr.project2.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    private static final Map<String, String> tokenToUsername = new HashMap<>();

    @Override
    public Result<String> register(UserDTO userDTO) {
        User dbUser = userMapper.selectByUsername(userDTO.getUsername());
        if (dbUser != null) {
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        userMapper.insert(user);
        return Result.success("注册成功!");
    }

    @Override
    public Result<String> login(UserDTO userDTO) {
        User dbUser = userMapper.selectByUsername(userDTO.getUsername());
        if (dbUser == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        if (!dbUser.getPassword().equals(userDTO.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }
        // 生成token
        String token = "token_" + System.currentTimeMillis() + "_" + userDTO.getUsername();
        // 存储token与用户名的映射
        tokenToUsername.put(token, userDTO.getUsername());
        return Result.success(token);
    }

    @Override
    public Result<String> getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        return Result.success("查询成功，用户信息：" + user.getUsername());
    }

    @Override
    public Result<Object> getUserPage(Integer pageNum, Integer pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        // 查询数据列表
        List<User> userList = userMapper.selectPage(pageSize, offset);
        // 查询总记录数
        Long total = userMapper.selectCount();
        // 计算总页数
        int pages = (int) Math.ceil((double) total / pageSize);
        
        // 组装分页结果
        Map<String, Object> result = new HashMap<>();
        result.put("records", userList);
        result.put("total", total);
        result.put("size", pageSize);
        result.put("current", pageNum);
        result.put("pages", pages);
        
        return Result.success(result);
    }
}