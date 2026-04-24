package com.lkr.project2.service;

import com.lkr.project2.common.Result;
import com.lkr.project2.dto.UserDTO;

public interface UserService {
    Result<String> register(UserDTO userDTO);
    Result<String> login(UserDTO userDTO);
    Result<String> getUserById(Long id);
    // 获取用户分页数据
    Result<Object> getUserPage(Integer pageNum, Integer pageSize);
}