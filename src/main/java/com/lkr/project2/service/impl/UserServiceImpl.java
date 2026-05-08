package com.lkr.project2.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import com.lkr.project2.common.Result;
import com.lkr.project2.common.ResultCode;
import com.lkr.project2.dto.UserDTO;
import com.lkr.project2.entity.User;
import com.lkr.project2.entity.UserInfo;
import com.lkr.project2.mapper.UserInfoMapper;
import com.lkr.project2.mapper.UserMapper;
import com.lkr.project2.service.UserService;
import com.lkr.project2.vo.UserDetailVO;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserInfoMapper userInfoMapper;
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final Map<String, String> tokenToUsername = new HashMap<>();
    private static final String CACHE_KEY_PREFIX = "user:detail:";

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

    @Override
    public Result<UserDetailVO> getUserDetail(Long userId) {
        String key = CACHE_KEY_PREFIX + userId;
        
        // 1. 先查缓存
        String json = redisTemplate.opsForValue().get(key);
        if (json != null && !json.isBlank()) {
            try {
                UserDetailVO cacheVO = JSONUtil.toBean(json, UserDetailVO.class);
                return Result.success(cacheVO);
            } catch (Exception e) {
                // 缓存数据异常，删除缓存，继续查数据库
                redisTemplate.delete(key);
            }
        }
        
        // 2. 查数据库
        UserDetailVO detail = userInfoMapper.getUserDetail(userId);
        if (detail == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        
        // 3. 写缓存（10分钟过期）
        redisTemplate.opsForValue().set(
            key, 
            JSONUtil.toJsonStr(detail), 
            10, 
            TimeUnit.MINUTES
        );
        
        return Result.success(detail);
    }

    @Override
    @Transactional
    public Result<String> updateUserInfo(UserInfo userInfo) {
        if (userInfo == null || userInfo.getUserId() == null) {
            return Result.error(ResultCode.PARAM_ERROR);
        }
        
        // 1. 更新数据库
        UserInfo existingInfo = userInfoMapper.selectByUserId(userInfo.getUserId());
        if (existingInfo != null) {
            userInfoMapper.updateByUserId(userInfo);
        } else {
            userInfoMapper.insert(userInfo);
        }
        
        // 2. 删除旧缓存
        String key = CACHE_KEY_PREFIX + userInfo.getUserId();
        redisTemplate.delete(key);
        
        return Result.success("更新成功");
    }

    @Override
    @Transactional
    public Result<String> deleteUser(Long userId) {
        // 1. 删除用户扩展信息
        userInfoMapper.deleteByUserId(userId);
        
        // 2. 删除用户基础信息
        userMapper.deleteById(userId);
        
        // 3. 删除缓存
        String key = CACHE_KEY_PREFIX + userId;
        redisTemplate.delete(key);
        
        return Result.success("删除成功");
    }
}