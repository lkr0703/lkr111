package com.lkr.project2.service;

import com.lkr.project2.common.Result;
import com.lkr.project2.dto.UserDTO;
import com.lkr.project2.entity.UserInfo;
import com.lkr.project2.vo.UserDetailVO;

public interface UserService {
    Result<String> register(UserDTO userDTO);
    Result<String> login(UserDTO userDTO);
    Result<String> getUserById(Long id);
    // 获取用户分页数据
    Result<Object> getUserPage(Integer pageNum, Integer pageSize);
    
    // 查询用户详情（多表联查 + Redis缓存）
    Result<UserDetailVO> getUserDetail(Long userId);
    
    // 更新用户扩展信息
    Result<String> updateUserInfo(UserInfo userInfo);
    
    // 删除用户
    Result<String> deleteUser(Long userId);
}